package io.jrevolt.sysmon.agent;

import io.jrevolt.sysmon.common.Version;
import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.jms.CloudEvents;
import io.jrevolt.sysmon.jms.JmsReceiver;
import io.jrevolt.sysmon.jms.JmsSender;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.SpringBootApp;
import io.jrevolt.sysmon.model.VersionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.Proxy;
import java.time.Instant;

import static io.jrevolt.sysmon.common.Version.getVersion;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.jrevolt.sysmon")
//@EnableConfigurationProperties(DomainDef.class)
@EnableScheduling
public class Agent {

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
	AgentCfg cfg;
	
	@Autowired
	AgentEvents events;

	@Autowired
	ConfigurableApplicationContext ctx;

	@Autowired
	ActiveMQConnectionFactory amq;

	@PostConstruct
	void init() {
		AgentInfo info = new AgentInfo(
				cfg.getClusterName(), cfg.getServerName(),
				AgentInfo.Status.STARTED, new VersionInfo(getVersion(Agent.class)), Instant.now());
		events.started(info);
	}

	static public void main(String[] args) {
		SpringBootApp.run(Agent.class, args);
	}
}
