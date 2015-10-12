package io.jrevolt.sysmon.client.ui;

import javafx.application.Application;
import javafx.stage.Stage;

import io.jrevolt.sysmon.client.ClientConfig;
import io.jrevolt.sysmon.client.ClientMain;
import io.jrevolt.sysmon.common.Version;
import io.jrevolt.sysmon.model.AppCfg;
import io.jrevolt.sysmon.model.SpringBootApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.prefs.Preferences;

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
	ConfigurableApplicationContext ctx;

	@Autowired
	AppCfg app;

	@Autowired
	ClientConfig client;

	@Autowired FxHelper helper;

	{
		SpringBootApp.instance().autowire(this);
	}

	@Override
	public void start(Stage stage) throws Exception {
		INSTANCE = this;
		this.stage = stage;

		Base base = FxHelper.load(getMainFrameClass());

//        Date jvmLaunched = new Date(Long.parseLong(System.getProperty("__jvm_launched")));
//        Date appletLaunched = new Date(Long.parseLong(System.getProperty("__applet_launched")));
//        Date now = new Date(System.currentTimeMillis());
//
//        System.out.println("launch time: "+(now.getTime() - jvmLaunched.getTime()));

		loadStage();

		Version version = Version.getVersion(ClientMain.class);
		stage.setTitle(String.format(
				"%s (%s, %s)", app.getName(), version.getArtifactVersion(),
				LocalDate.from(version.getTimestamp().atZone(ZoneOffset.systemDefault()))));

		base.show();

		stage.setOnCloseRequest(event -> {
			saveStage();
			ctx.close();
//			System.exit(0);
		});
	}

	Class<? extends Base> getMainFrameClass() {
		return ClientFrame.class;
	}

	protected void customize() {}

	public void saveStage() {
		Preferences prefs = Preferences.userRoot().node(FxMain.class.getName());
		prefs.putDouble("stage.x", stage().getX());
		prefs.putDouble("stage.y", stage().getY());
		prefs.putDouble("stage.width", stage().getWidth());
		prefs.putDouble("stage.height", stage().getHeight());
	}

	public void loadStage() {
		Preferences prefs = Preferences.userRoot().node(FxMain.class.getName());
		stage().setX(prefs.getDouble("stage.x", 0));
		stage().setY(prefs.getDouble("stage.d", 0));
		stage().setWidth(prefs.getDouble("stage.width", 1024));
		stage().setHeight(prefs.getDouble("stage.height", 768));
	}

}
