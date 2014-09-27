package io.jrevolt.sysmon.browser;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;

import io.jrevolt.sysmon.model.AppCfg;
import io.jrevolt.sysmon.model.SpringBootApp;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class FxMain extends Application {

	static private FxMain INSTANCE;

	static public FxMain instance() {
		return INSTANCE;
	}

	static public Stage stage() {
		return INSTANCE.stage;
	}

	static public void main(String[] args) {
		Application.launch(FxMain.class, args);
	}

	Stage stage;

	@Autowired
	AppCfg app;

	@Autowired
	BrowserConfig client;

	{
		SpringBootApp.instance().autowire(this);
	}

	@Override
	public void start(Stage stage) throws Exception {
		INSTANCE = this;
		this.stage = stage;
	}
}
