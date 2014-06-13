package org.jrevolt.sysmon.agent;

import org.jrevolt.sysmon.common.AgentEvents;
import org.jrevolt.sysmon.common.EventsHandler;
import org.jrevolt.sysmon.common.JmsSenderProxy;
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
	AgentEvents agentEvents(JmsSenderProxy jmsSenderProxy) {
		return (AgentEvents) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{AgentEvents.class},
				jmsSenderProxy);
	}


	@Bean
	EventsHandler serverEvents() {
		return new EventsHandler(ServerEvents.class);
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
