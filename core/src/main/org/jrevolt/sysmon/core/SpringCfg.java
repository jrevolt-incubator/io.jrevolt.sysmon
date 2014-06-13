package org.jrevolt.sysmon.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableConfigurationProperties(SpringApp.class)
public class SpringCfg {

	@Bean
	static private PropertySourcesPlaceholderConfigurer properties(ApplicationContext ctx) throws IOException {
		PropertySourcesPlaceholderConfigurer props = new PropertySourcesPlaceholderConfigurer();
		props.setLocations(ctx.getResources("classpath*:spring.yaml"));
		return props;
	}

}