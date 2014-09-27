package io.jrevolt.sysmon.browser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jrevolt.sysmon.rest.RestService;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableConfigurationProperties(BrowserConfig.class)
public class BrowserApp {

	@Bean
	Client client() {
		return ClientBuilder.newClient();
	}

	@Bean @ConditionalOnMissingBean
	RestService restService(Client client, BrowserConfig cfg) {
		return WebResourceFactory.newResource(RestService.class, client.target(cfg.getServerUrl()));
	}



}
