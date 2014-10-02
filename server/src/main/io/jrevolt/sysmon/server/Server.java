package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.common.VersionInfo;
import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.jms.JmsReceiver;
import io.jrevolt.sysmon.jms.JmsSender;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.DomainDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import org.apache.coyote.http11.Http11NioProtocol;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@EnableSpringConfigured
@EnableScheduling
@ComponentScan("io.jrevolt.sysmon")
@EnableConfigurationProperties(DomainDef.class)
public class Server {

	@Bean
	public ServletRegistrationBean jerseyServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
		registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
		return registration;
	}


	@Bean
	ServerEvents serverEvents(JmsSender jmsSender) {
		return (ServerEvents) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{ServerEvents.class},
				jmsSender);
	}

	@Bean
	JmsReceiver agentEvents() {
		return new JmsReceiver(AgentEvents.class);
	}

	@Autowired
	ServerEvents events;

	@Autowired DomainDef domain;

	@Bean @Profile("ssl")
	EmbeddedServletContainerCustomizer containerCustomizer() {
		return (ConfigurableEmbeddedServletContainer container) -> {
			TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
			tomcat.addConnectorCustomizers((connector) -> {
				connector.setPort(443);
				connector.setSecure(true);
				connector.setScheme("https");

				Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
				proto.setSSLEnabled(true);
				proto.setKeystoreFile(new File(
						"c:\\users\\patrik\\projects\\io.jrevolt\\io.jrevolt.sysmon\\server\\src\\main\\io\\jrevolt\\sysmon\\server\\rest\\keystore.jks")
													 .getAbsolutePath());
				proto.setKeystorePass("topsecret");
				proto.setKeystoreType("JCEKS");
				proto.setKeyAlias("st1mons11");
				proto.setKeyPass("topsecret");
			});

		};
	}

	@Bean
	ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(20);
	}

	@PostConstruct
	void init() {
		VersionInfo version = VersionInfo.forClass(Server.class);
		System.out.printf("%s (%s)%n", version.getArtifactUri(), version.getArtifactVersion());
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Server.class, args);
	}

}
