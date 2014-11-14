package io.jrevolt.sysmon.agent;

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
import io.jrevolt.sysmon.model.VersionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
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
		pool.submit(() -> clusterDef.getServers().parallelStream().forEach(s -> checkServer(s, clusterDef)));
	}

	@Override
	public void checkServer(@JMSProperty String name, ClusterDef clusterDef) {
		pool.submit(() -> {
			List<EndpointDef> endpoints = new LinkedList<>();
			endpoints.addAll(clusterDef.getProvides());
			endpoints.addAll(clusterDef.getDependencies());
			endpoints.parallelStream().forEach(this::checkEndpoint);
			checkNetwork(clusterDef, name, clusterDef.getDependencies());
			events.clusterStatus(clusterDef);
		});
	}

	@Override
	public void setClusterAccess(@JMSProperty String clusterName, boolean isAllowAccess) {




	}

	void checkEndpoint(EndpointDef endpoint) {
		if (endpoint.getUri().getScheme().equals("jdbc")) {
			try {
				DriverManager.getConnection(endpoint.getUri().toString());
				endpoint.setStatus(EndpointStatus.OK);
			} catch (SQLException e) {
				String desc = Utils.getExceptionDesription(e);
				LOG.error(desc);
				endpoint.setStatus(EndpointStatus.ERROR);
				endpoint.setComment(desc);
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
				LOG.error(e.toString());
				endpoint.setStatus(EndpointStatus.UNAVAILABLE);
				endpoint.setComment(e.toString());

			} catch (IOException e) {
				String desc = Utils.getExceptionDesription(e);
				LOG.error(desc);
				endpoint.setStatus(EndpointStatus.ERROR);
				endpoint.setComment(desc);
			}
		} else {
			try {
				InetSocketAddress address = new InetSocketAddress(endpoint.getUri().getHost(), endpoint.getUri().getPort());
				Socket socket = new Socket();
				socket.connect(address, 2500);
				endpoint.setStatus(EndpointStatus.OK);
				endpoint.setComment("Connected!");
			} catch (IOException e) {
				endpoint.setStatus(EndpointStatus.ERROR);
				endpoint.setComment(Utils.getExceptionDesription(e));
			}
		}
	}

	void checkNetwork(ClusterDef cluster, String serverName, List<EndpointDef> dependencies) {
		cluster.getNetwork().clear();
		dependencies.parallelStream()
				.map(d -> checkNetwork(new NetworkInfo(
						cluster.getClusterName(), serverName, d.getUri().getHost(), Utils.resolvePort(d.getUri()))))
				.forEach(item -> cluster.getNetwork().add(item));
	}

	NetworkInfo checkNetwork(NetworkInfo net) {
		try {
			InetAddress src = InetAddress.getByName(net.getServer());
			net.setSrcAddress(src.getHostAddress());

			InetAddress dst = InetAddress.getByName(net.getDestination());
			net.setDstAddress(dst.getHostAddress());

			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(dst, net.getPort()), 1000);
			net.setStatus(socket.isConnected() ? NetworkInfo.Status.CONNECTED : NetworkInfo.Status.UNKNOWN);
			
		} catch (UnknownHostException e) {
			net.setStatus(NetworkInfo.Status.UNRESOLVED);
			net.setComment(Utils.getExceptionDesription(e));
		} catch (NoRouteToHostException e) {
			net.setStatus(NetworkInfo.Status.UNREACHABLE);
			net.setComment(Utils.getExceptionDesription(e));
		} catch (ConnectException e) {
			net.setStatus(NetworkInfo.Status.REFUSED);
			net.setComment(Utils.getExceptionDesription(e));
		} catch (IOException e) {
			net.setStatus(NetworkInfo.Status.ERROR);
			net.setComment(Utils.getExceptionDesription(e));
		}
		return net;
	}

	private AgentInfo createAgentInfo() {
		return new AgentInfo(
				cfg.getClusterName(), cfg.getServerName(), AgentInfo.Status.ONLINE,
				new VersionInfo(Version.getVersion(Agent.class)),
				Instant.now()
		);
	}
}
