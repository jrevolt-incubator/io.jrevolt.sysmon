package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import java.io.File;
import java.util.Collection;

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
	}
}
