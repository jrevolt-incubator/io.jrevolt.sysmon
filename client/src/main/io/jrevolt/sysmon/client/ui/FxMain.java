package io.jrevolt.sysmon.client.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import io.jrevolt.sysmon.core.AppCfg;
import io.jrevolt.sysmon.core.SpringBootApp;
import org.springframework.beans.factory.annotation.Autowired;

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

		stage.setTitle(app.getName());
		base.show();
	}

	Class<? extends Base> getMainFrameClass() {
		return ClientFrame.class;
	}

	protected void customize() {}

}
