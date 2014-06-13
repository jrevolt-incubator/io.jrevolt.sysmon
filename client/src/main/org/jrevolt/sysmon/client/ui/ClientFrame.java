package org.jrevolt.sysmon.client.ui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import org.jrevolt.sysmon.api.RestService;
import org.jrevolt.sysmon.core.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.util.Formatter;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ClientFrame extends Base<BorderPane> {

	@Autowired
	StandardEnvironment env;

	@Autowired
	AppCfg app;

	@Autowired
	RestService rest;

	@Override
	protected void initialize() {
		super.initialize();

		pane.setCenter(new TextArea() {{
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter(sb);
			f.format("version: %s%n", rest.version());
			for (PropertySource<?> src : env.getPropertySources()) {
				try {
					String[] names = ((MapPropertySource) src).getPropertyNames();
					for (String name : names) {
						f.format("%s : %s%n", name, src.getProperty(name));
					}
				} catch (Exception e) {
					System.out.println(e + " : " + src);
				}
			}
			setText(sb.toString());
		}});

	}
}
