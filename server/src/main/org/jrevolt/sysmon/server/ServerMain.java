package org.jrevolt.sysmon.server;

import org.jrevolt.sysmon.common.AgentEvents;
import org.jrevolt.sysmon.common.JmsReceiver;
import org.jrevolt.sysmon.common.JmsSender;
import org.jrevolt.sysmon.common.ServerEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan("org.jrevolt.sysmon")
public class ServerMain {

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
