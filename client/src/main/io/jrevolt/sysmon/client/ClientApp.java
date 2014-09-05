package io.jrevolt.sysmon.client;

import io.jrevolt.sysmon.rest.RestService;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableConfigurationProperties(ClientConfig.class)
public class ClientApp {

	@Bean
	Client client() {
		return ClientBuilder.newClient();
	}

	@Bean
	RestService restService(Client client, ClientConfig cfg) {
		return WebResourceFactory.newResource(RestService.class, client.target(cfg.getServerUrl()));
	}



}
