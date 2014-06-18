package io.jrevolt.sysmon.client;

import io.jrevolt.sysmon.client.ui.FxMain;
import javafx.application.Application;
import io.jrevolt.sysmon.core.SpringBootApp;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.jrevolt.sysmon")
public class ClientMain {

	static public void main(String[] args) {
		SpringBootApp.run(ClientMain.class, args);
		Application.launch(FxMain.class);
	}
}