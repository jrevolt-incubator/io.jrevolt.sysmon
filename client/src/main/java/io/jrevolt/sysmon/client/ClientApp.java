package io.jrevolt.sysmon.client;

import io.jrevolt.sysmon.rest.ApiService;

import org.glassfish.jersey.client.proxy.WebResourceFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import java.net.URI;
import java.net.URL;
import java.util.Collections;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableConfigurationProperties(ClientConfig.class)
public class ClientApp {

	@Value("${sysmon.server.baseUrl}")
	URI baseUrl;

	@Bean
	Client client() {
		return ClientBuilder.newClient();
	}

	@Bean @ConditionalOnMissingBean
	ApiService restService(Client client, ClientConfig cfg) {
		final MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add("Accept", "application/json");
		return WebResourceFactory.newResource(
				ApiService.class, client.target(baseUrl), false, headers, Collections.emptyList(), new Form());
	}

	@Bean
	WebTarget webtarget(Client client, ClientConfig cfg) {
		return client.target(cfg.getServerUrl());
	}

}
