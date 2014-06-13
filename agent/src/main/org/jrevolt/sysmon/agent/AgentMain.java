package org.jrevolt.sysmon.agent;

import org.jrevolt.sysmon.common.AgentEvents;
import org.jrevolt.sysmon.common.EventsHandler;
import org.jrevolt.sysmon.common.JmsSenderProxy;
import org.jrevolt.sysmon.common.ServerEvents;
import org.jrevolt.sysmon.core.SpringApp;
import org.jrevolt.sysmon.core.SpringCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
		SpringApplication app = new SpringApplication(AgentMain.class) {
			@Override
			protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
				try {
					super.configureEnvironment(environment, args);
					PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
					Resource[] resources = resolver.getResources("classpath*:spring.yaml");
					YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
					CompositePropertySource composite = new CompositePropertySource("classpath*:spring.yaml");
					for (Resource resource : resources) {
						composite.addPropertySource(loader.load(resource.getFilename(), resource, null));
					}
					environment.getPropertySources().addLast(composite);
				} catch (IOException e) {
					throw new UnsupportedOperationException(e);
				}
			}
		};
		app.run(args);
	}
}
