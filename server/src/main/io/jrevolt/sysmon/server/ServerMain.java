package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.common.ServerEvents;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import io.jrevolt.sysmon.common.AgentEvents;
import io.jrevolt.sysmon.common.JmsReceiver;
import io.jrevolt.sysmon.common.JmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@EnableSpringConfigured
//@EnableScheduling
@ComponentScan("io.jrevolt.sysmon")
public class ServerMain {

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

	@Scheduled(fixedRate = 5000)
	void run() {
		try {
			events.ping();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Exception {
		SpringApplication.run(ServerMain.class, args);
	}


}