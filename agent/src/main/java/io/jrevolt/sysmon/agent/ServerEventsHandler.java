package io.jrevolt.sysmon.agent;

import io.jrevolt.sysmon.common.Log;
import io.jrevolt.sysmon.common.Utils;
import io.jrevolt.sysmon.common.Version;
import io.jrevolt.sysmon.jms.JMSProperty;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.EndpointDef;
import io.jrevolt.sysmon.model.EndpointStatus;
import io.jrevolt.sysmon.model.NetworkInfo;
import io.jrevolt.sysmon.model.ServerDef;
import io.jrevolt.sysmon.model.VersionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ServerEventsHandler implements ServerEvents {
	
	static private final Logger LOG = LoggerFactory.getLogger(ServerEventsHandler.class);

	@Autowired
	AgentCfg cfg;
	
	@Autowired
	AgentEvents events;

	ForkJoinPool pool;

	@PostConstruct
	void init() {
		pool = new ForkJoinPool(30);
	}

	@PreDestroy
	void close() {
		try {
			pool.shutdown();
			pool.awaitTermination(5, TimeUnit.SECONDS);
			pool.shutdownNow();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			pool = null;
		}
	}

	@Override
	public void ping(@JMSProperty String cluster, @JMSProperty String server) {
		events.status(createAgentInfo());
	}

	@Override
	public void restart(@JMSProperty String cluster, @JMSProperty String server) {
		events.restarting(createAgentInfo());
		pool.submit(()->{
			new Thread("ShutdownOnDemand") {
				@Override
				public void run() {
					LOG.info("Exiting on request. Setting error code to 7, service wrapper should restart us");
					System.exit(7);
				}
			}.run();
		});
		pool.shutdown();
	}

	@Override
	public void checkCluster(@JMSProperty String name, ClusterDef clusterDef) {
		System.out.println("### checkCluster "+name);
		ServerDef server = clusterDef.getServers().stream()
				.filter(s -> s.getName().equals(cfg.getServerName()))
				.findFirst().get();
		pool.submit(() -> checkServer(server));
	}

	@Override
	public void checkServer(@JMSProperty String name, ServerDef server) {
		System.out.println("### checkServer "+name);
		checkServer(server);
	}

	@Override
	public void setClusterAccess(@JMSProperty String clusterName, boolean isAllowAccess) {
	}

	void checkServer(ServerDef server) {
		pool.submit(Utils.guarded(() -> {
			List<EndpointDef> endpoints = new LinkedList<>();
			endpoints.addAll(server.getProvides());
			endpoints.addAll(server.getDependencies());
			endpoints.parallelStream().forEach(this::checkEndpoint);
			checkNetwork(server);
			events.serverStatus(server);
		}));
		System.out.println("## leaving checkServer()");
	}

	void checkEndpoint(EndpointDef endpoint) {
		if (endpoint.getUri().getScheme().equals("jdbc")) {
			try {
				DriverManager.getConnection(endpoint.getUri().toString());
				endpoint.setStatus(EndpointStatus.OK);
			} catch (SQLException e) {
				String desc = Utils.getExceptionDesription(e);
				if (desc.contains("Login failed")) {
					endpoint.setStatus(EndpointStatus.OK);
					endpoint.setComment(desc);
				} else {
					LOG.error(desc);
					endpoint.setStatus(EndpointStatus.ERROR);
					endpoint.setComment(desc);
				}
			}
		} else if (endpoint.getUri().getScheme().matches("https?")) {
			try {
				URL url = endpoint.getTest() == null
						? endpoint.getUri().toURL()
						: URI.create(endpoint.getUri().toString() + endpoint.getTest().toString()).toURL();
				URLConnection con = url.openConnection();
				con.setConnectTimeout(2500);
				con.setReadTimeout(5000);
				if (con instanceof HttpURLConnection) {
					if (endpoint.getUri().getFragment() != null) {
						if (endpoint.getUri().getFragment().contains("post")) {
							((HttpURLConnection) con).setRequestMethod("POST");
						}
						if (endpoint.getUri().getFragment().contains("method=")) {
							((HttpURLConnection) con).setRequestMethod(
									endpoint.getUri().getFragment().replaceFirst(".*method=([^;]+).*", "$1").toUpperCase());
						}
						if (endpoint.getUri().getFragment().contains("type=")) {
							con.setRequestProperty("Content-Type",
														  endpoint.getUri().getFragment().replaceFirst(".*type=([^;]+).*", "$1"));
						}
					}
				}
				int code = con instanceof HttpURLConnection ? ((HttpURLConnection) con).getResponseCode() : 0;

				switch (code) {
					case 200:
					case 302:
					case 401:
						endpoint.setStatus(EndpointStatus.OK);
						break;
					case 404:
						endpoint.setStatus(EndpointStatus.UNAVAILABLE);
						break;
					case 500:
						endpoint.setStatus(EndpointStatus.ERROR);
						break;
				}

				endpoint.setComment(String.format("HTTP %d", code));

			} catch (ConnectException e) {
				String desc = Utils.getExceptionDesription(e);
				LOG.error("{} : {}", endpoint.getUri(), desc);
				endpoint.setStatus(EndpointStatus.UNAVAILABLE);
				endpoint.setComment(desc);

			} catch (IOException e) {
				String desc = Utils.getExceptionDesription(e);
				LOG.error("{} : {}", endpoint.getUri(), desc);
				endpoint.setStatus(EndpointStatus.ERROR);
				endpoint.setComment(desc);
			}
		} else {
			try {
				InetSocketAddress address = new InetSocketAddress(Utils.resolveHost(endpoint.getUri()), Utils.resolvePort(endpoint.getUri()));
				Socket socket = new Socket();
				socket.connect(address, 2500);
				endpoint.setStatus(EndpointStatus.OK);
				endpoint.setComment("Connected!");
			} catch (IOException e) {
				String desc = Utils.getExceptionDesription(e);
				LOG.error("{} : {}", endpoint.getUri(), desc);
				endpoint.setStatus(EndpointStatus.ERROR);
				endpoint.setComment(desc);
			}
		}
	}

	void checkNetwork(ServerDef server) {
		System.out.println(ToStringBuilder.reflectionToString(server.getNetwork()));
		server.getNetwork().forEach(this::checkNetwork);
	}

	NetworkInfo checkNetwork(NetworkInfo net) {
		long timeout = 7000;
		try {
			InetAddress src = InetAddress.getByName(net.getServer());
			net.setSrcAddress(src.getHostAddress());

			InetAddress dst = InetAddress.getByName(net.getDestination());
			net.setDstAddress(dst.getHostAddress());

			long started = System.currentTimeMillis();
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(dst, net.getPort()), (int) timeout);
			long elapsed = System.currentTimeMillis() - started;

			net.setStatus(socket.isConnected() ? NetworkInfo.Status.CONNECTED : NetworkInfo.Status.UNKNOWN);
			net.setComment(socket.isConnected() ? null : "not connected?");
			net.setTime(elapsed);

//			SSLSocket sslsocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
//					socket,
//					socket.getInetAddress().getHostAddress(),
//					socket.getPort(), true);
//			sslsocket.addHandshakeCompletedListener(event -> {
//				try {
//					javax.security.cert.X509Certificate[] chain = event.getPeerCertificateChain();
//				} catch (SSLPeerUnverifiedException e) {
//					throw new UnsupportedOperationException(e);
//				}
//			});
//			sslsocket.startHandshake();
//			sslsocket.getSession().isValid();

		} catch (UnknownHostException e) {
			net.setStatus(NetworkInfo.Status.UNRESOLVED);
			net.setComment(Utils.getExceptionDesription(e));
		} catch (NoRouteToHostException e) {
			net.setStatus(NetworkInfo.Status.UNREACHABLE);
			net.setComment(Utils.getExceptionDesription(e));
		} catch (ConnectException e) {
			net.setStatus(NetworkInfo.Status.REFUSED);
			net.setComment(Utils.getExceptionDesription(e));
		} catch (SocketTimeoutException e) {
			net.setStatus(NetworkInfo.Status.TIMEOUT);
			net.setComment(Utils.getExceptionDesription(e));
			net.setTime(timeout);
		} catch (SSLHandshakeException e) {
			net.setComment(e.toString());
		} catch (IOException e) {
			net.setStatus(NetworkInfo.Status.ERROR);
			net.setComment(Utils.getExceptionDesription(e));
		}

		System.out.println(ToStringBuilder.reflectionToString(net));

		return net;
	}

	private AgentInfo createAgentInfo() {
		return new AgentInfo(
				cfg.getClusterName(), cfg.getServerName(), AgentInfo.Status.ONLINE,
				new VersionInfo(Version.getVersion(Agent.class)),
				Instant.now()
		);
	}

	void checkSsl(ServerDef server) {
		server.getDependencies().parallelStream().forEach(e -> {
			try {
				final URI uri = Utils.address(e.getUri());
				TrustManagerFactory tmf = initTrustManagerFactory();
				X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
				SavingTrustManager tm = new SavingTrustManager(defaultTrustManager, new Host(uri.getHost()));
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, new TrustManager[]{tm}, null);
				
			} catch (NoSuchAlgorithmException e1) {
				throw new UnsupportedOperationException(e1);
			} catch (KeyManagementException e1) {
				throw new UnsupportedOperationException(e1);
			}
		});
	}

	TrustManagerFactory initTrustManagerFactory() {
		try {
			File dflt = new File(System.getProperty("java.home"), "lib/security/cacerts");
			File file = new File(System.getProperty("javax.net.ssl.keyStore", dflt.getAbsolutePath()));
			InputStream in = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, "changeit".toCharArray());
			in.close();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			return tmf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		Host host;

		SavingTrustManager(X509TrustManager tm, Host host) {
			this.tm = tm;
			this.host = host;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return tm.getAcceptedIssuers();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//			tm.checkClientTrusted(chain, authType);
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			host.certificate = chain[0];
			host.rootCA = chain[chain.length-1];
			//tm.checkServerTrusted(chain, authType);
		}
	}

	static class Host {
		String name;
		InetAddress address;
		List<Endpoint> endpoints = new LinkedList<>();

		Boolean trusted;
		X509Certificate certificate;
		X509Certificate rootCA;
		Exception certError;

		public Host(String name) {
			this.name = name;
		}
	}

	static class Endpoint {
		Host host;
		Integer port;
		Boolean open;

		Endpoint(Host host, Integer port) {
			this.host = host;
			this.port = port;
		}
	}



}
