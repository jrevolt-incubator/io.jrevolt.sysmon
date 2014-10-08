package io.jrevolt.sysmon.browser;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class BrowserMain extends Application {

	static public void main(String[] args) {
		Application.launch(BrowserMain.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			Class cls = Browser.class;
			FXMLLoader loader = new FXMLLoader(cls.getResource(cls.getSimpleName() + ".fxml"));
			Pane o = loader.load();
			Scene scene = new Scene(o);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}


}
