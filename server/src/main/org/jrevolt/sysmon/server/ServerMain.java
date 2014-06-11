package org.jrevolt.sysmon.server;

import org.jrevolt.sysmon.common.AgentEvents;
import org.jrevolt.sysmon.common.EventsHandler;
import org.jrevolt.sysmon.common.JmsSenderProxy;
import org.jrevolt.sysmon.common.ServerEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.jrevolt.sysmon")
public class ServerMain {

	@Bean
	ServerEvents serverEvents(JmsSenderProxy jmsSenderProxy) {
		return (ServerEvents) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{ServerEvents.class},
				jmsSenderProxy);
	}

	@Bean
	EventsHandler agentEvents() {
		return new EventsHandler(AgentEvents.class);
	}

	@Autowired
	ServerEvents events;

//	@Scheduled(fixedRate = 10000)
	@PostConstruct
	void run() {
		try {
			events.ping();
			events.restart();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Exception {
		SpringApplication.run(ServerMain.class, args);
	}


}
