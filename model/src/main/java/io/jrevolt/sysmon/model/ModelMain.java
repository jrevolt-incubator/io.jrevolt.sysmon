package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Formatter;
import java.util.Optional;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.jrevolt.sysmon")
@EnableConfigurationProperties(DomainDef.class)
public class ModelMain {

	static public void main(String[] args) {
		((LoggerContext) LoggerFactory.getILoggerFactory()).reset();
		SpringBootApp.run(ModelMain.class, args).getBean(ModelMain.class).run();
	}

	@Autowired
	DomainDef domain;

	@Value("${ModelMain.file:-}")
	File file;

	@Value("${ModelMain.format:%13s;%30s;%20s;%30s;%20s;%10s;%13s%n}")
	String format;

	public void run() {
		Formatter out = out();
		out.format(
				format,
				"Typ vztahu", "Source KP", "Source Typ KP", "Target KP", "Target Typ KP", "TENANT_ID", "customer_id");
		composition(out);
		usedBy(out);
	}

	private void composition(Formatter out) {
		domain.getClusters().stream().map(ClusterDef::getServers).flatMap(Collection::stream).forEach(s->{
			out.format(
					format,
					"composition", "cluster-"+s.getCluster(), "Cluster", s.getName(), "Server", "DEUS", "6");
			s.getClusterDef().getProxies().forEach(p->{
				out.format(
						format,
						"composition", p.getName(), "LoadBalancer", "cluster-"+s.getCluster(), "Cluster", "DEUS", "6");
			});
		});
	}

	private void usedBy(Formatter out) {
		domain.getProxies().forEach(proxy ->{
			proxy.getProvides().stream()
					.map(ep->ep.getUri().getHost()).distinct()
					.forEach(cluster -> {
				out.format(
						format,
						"used by", "load balancer", proxy.getUri().getHost(), cluster, "cluster", "DEUS", "6");
			});
		});
		domain.getClusters().forEach(cl->{
			cl.getServers().stream().findFirst().ifPresent(sd->{
				sd.getDependencies().stream()
						.map(d->getHostFromUri(d.getUri())).distinct()
						.forEach(h->{
					Optional<ProxyDef> pd = domain.getProxies().stream()
							.filter(p -> p.getUri().getHost().matches(h)).findFirst();
					String kind = pd.isPresent() ? "load balancer" : "external";
					String name = pd.isPresent() ? pd.get().getName() : h;
					out.format(
							format,
							"used by", name, kind, cl.getClusterName(), "cluster", "DEUS", "6");
				});
			});
		});
	}

	String getHostFromUri(URI uri) {
		if (uri.getScheme().equals("jdbc")) {
			uri = URI.create(uri.getSchemeSpecificPart());
			String h = uri.getAuthority().replaceFirst("[:;].*", "");
			return h;
		}
		return uri.getHost();
	}

	private Formatter out() {
		try {
			if (file == null || file.getName().equals("-")) {
				return new Formatter(System.out);
			} else {
				return new Formatter(file);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
