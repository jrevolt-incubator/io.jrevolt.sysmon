package io.jrevolt.sysmon.server.rest;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import io.jrevolt.sysmon.rest.ApiService;
import io.jrevolt.sysmon.server.Server;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Server.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0", "spring.profiles=unittest"})
public class ApiServiceTest {

	@Autowired
	EmbeddedWebApplicationContext server;

	@Autowired
	ApiService rest;

	RestTemplate template = new TestRestTemplate();

	@Test
	public void test() {
		URI uri = UriBuilder.fromUri("http://localhost/rest/domain")
				.port(server.getEmbeddedServletContainer().getPort())
				.build();
		String body = template.getForEntity(uri, String.class).getBody();
		System.out.println(body);
	}


}
