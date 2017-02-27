package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;
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

	public void run() {
		System.out.println("Typ vztahu;Source KP;Source Typ KP;Target KP;Target Typ KP;TENANT_ID;customer_id");
		domain.getClusters().stream().map(ClusterDef::getServers).flatMap(Collection::stream).forEach(s->{
			System.out.printf("composition;cluster-%s;Cluster;%s;Server;DEUS;6%n", s.getCluster(), s.getName());
			s.getClusterDef().getProxies().forEach(p->{
				System.out.printf("composition;%s;LoadBalancer;cluster-%s;Cluster;DEUS;6%n", p.getName(), s.getCluster());
			});
		});
//		domain.getProxies().forEach(proxy ->{
//			proxy.getProvides().stream()
//					.map(ep->ep.getUri().getHost()).distinct()
//					.forEach(cluster -> {
//				System.out.printf("used by;%s;load balancer;%s;cluster;DEUS;6%n", proxy.getUri().getHost(), cluster);
//			});
//		});
		domain.getClusters().forEach(cl->{
			cl.getServers().stream().findFirst().ifPresent(sd->{
				sd.getDependencies().stream()
						.map(d->getHostFromUri(d.getUri())).distinct()
						.forEach(h->{
					Optional<ProxyDef> pd = domain.getProxies().stream()
							.filter(p -> p.getUri().getHost().matches(h)).findFirst();
					String kind = pd.isPresent() ? "load balancer" : "external";
					String name = pd.isPresent() ? pd.get().getName() : h;
					System.out.printf("%10s;%30s;%20s;%20s;%20s;%n", "used by", name, kind, cl.getClusterName(), "cluster");
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
}
