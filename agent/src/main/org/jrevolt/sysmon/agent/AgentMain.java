package org.jrevolt.sysmon.agent;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.tcp.TcpTransport;
import org.jrevolt.sysmon.common.AgentEvents;
import org.jrevolt.sysmon.common.CloudEvents;
import org.jrevolt.sysmon.common.JmsReceiver;
import org.jrevolt.sysmon.common.JmsSender;
import org.jrevolt.sysmon.common.ServerEvents;
import org.jrevolt.sysmon.core.SpringBootApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.jms.ConnectionFactory;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.jrevolt.sysmon")
@EnableConfigurationProperties(DomainCfg.class)
@EnableScheduling
public class AgentMain {

	@Bean @Primary
	AgentEvents agentEvents(JmsSender jmsSender) {
		return (AgentEvents) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{AgentEvents.class},
				jmsSender);
	}

	@Bean @Primary
	CloudEvents cloudEvents(JmsSender jmsSender) {
		return (CloudEvents) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{CloudEvents.class},
				jmsSender);
	}

	@Bean
	JmsReceiver serverEventsReceiver() {
		return new JmsReceiver(ServerEvents.class);
	}

	@Bean
	JmsReceiver cloudEventsReceiver() {
		return new JmsReceiver(CloudEvents.class);
	}

	@Autowired
	AgentEvents events;

	@Autowired
	ConfigurableApplicationContext ctx;

	@Autowired
	ActiveMQConnectionFactory amq;

	@Scheduled(initialDelay = 5000L, fixedDelay = 5000L)
	void close() {
		System.out.println("Closing "+ctx);
		ctx.close();
	}

	static public void main(String[] args) {
		SpringBootApp.run(AgentMain.class, args);
	}
}
