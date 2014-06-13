package org.jrevolt.sysmon.agent;

import org.jrevolt.sysmon.common.AgentEvents;
import org.jrevolt.sysmon.common.JmsReceiver;
import org.jrevolt.sysmon.common.JmsSender;
import org.jrevolt.sysmon.common.ServerEvents;
import org.jrevolt.sysmon.core.SpringBootApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.jrevolt.sysmon")
public class AgentMain {

	@Bean
	AgentEvents agentEvents(JmsSender jmsSender) {
		return (AgentEvents) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{AgentEvents.class},
				jmsSender);
	}


	@Bean
	JmsReceiver serverEvents() {
		return new JmsReceiver(ServerEvents.class);
	}

	@Autowired
	AgentEvents events;

	@PostConstruct
	void run() {
		events.status("agent");
	}

	static public void main(String[] args) {
		SpringBootApp.run(AgentMain.class, args);
	}
}
