package io.jrevolt.sysmon.browser;

import org.springframework.boot.loader.FxMain;
import org.springframework.boot.loader.MvnLauncher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

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
