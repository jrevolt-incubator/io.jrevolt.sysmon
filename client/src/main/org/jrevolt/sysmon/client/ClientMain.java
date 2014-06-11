package org.jrevolt.sysmon.client;

import javafx.application.Application;
import org.jrevolt.sysmon.client.ui.FxMain;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.jrevolt.sysmon")
public class ClientMain {

	static private ClientMain INSTANCE;

	static public ClientMain instance() { return INSTANCE; }

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	StandardEnvironment env;

	{
		INSTANCE = this;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public <T> T lookup(Class<T> type) {
		Map<String, T> candidates = applicationContext.getBeansOfType(type);
		if (candidates.isEmpty()) { return null; }

		String key = candidates.keySet().iterator().next();
		return candidates.get(key);
	}

	public <T> T springify(T bean) {
		applicationContext.getAutowireCapableBeanFactory()
				.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		return bean;
	}

	static public void main(String[] args) {
		SpringApplication.run(ClientMain.class, args);
		Application.launch(FxMain.class);
	}
}
