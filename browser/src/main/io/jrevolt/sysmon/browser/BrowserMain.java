package io.jrevolt.sysmon.browser;

import javafx.application.Application;
import io.jrevolt.sysmon.model.SpringBootApp;
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
public class BrowserMain {

	static public void main(String[] args) {
		SpringBootApp.run(BrowserMain.class, args);
		Application.launch(FxMain.class);
	}
}
