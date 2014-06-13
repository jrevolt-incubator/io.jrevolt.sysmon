package org.jrevolt.sysmon.client;

import javafx.application.Application;
import org.jrevolt.sysmon.client.ui.FxMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.MutablePropertySources;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.jrevolt.sysmon")
public class ClientMain {

	static public void main(String[] args) {
		SpringApplication.run(ClientMain.class, args);
		Application.launch(FxMain.class);
	}
}
