package org.jrevolt.sysmon.client;

import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.jrevolt.sysmon.api.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableConfigurationProperties(ClientConfig.class)
public class ClientApp {

	static private ClientApp INSTANCE;

	static public ClientApp instance() { return INSTANCE; }

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	StandardEnvironment env;

	{
		INSTANCE = this;
	}

	@Bean
	Client client() {
		return ClientBuilder.newClient();
	}

	@Bean
	RestService restService(Client client, ClientConfig cfg) {
		return WebResourceFactory.newResource(RestService.class, client.target(cfg.getServerUrl()));
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


}
