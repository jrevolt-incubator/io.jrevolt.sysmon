package io.jrevolt.sysmon.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Configuration
public class CloudApp {

	@Autowired
	ConfigurableApplicationContext ctx;

	@Bean
	public CloudApi cloudApi(CloudApiHandler handler) {
		return (CloudApi) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[]{CloudApi.class},
				handler
		);
	}

}
