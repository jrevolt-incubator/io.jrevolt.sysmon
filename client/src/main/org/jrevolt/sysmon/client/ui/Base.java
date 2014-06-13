package org.jrevolt.sysmon.client.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public abstract class Base<T extends Pane> {

	@FXML
	URL location;

	@FXML
	protected T pane;

//	@FXML
	protected void initialize() {
	}

	public void show() {
		show(FxMain.stage());
	}

	public void show(Stage stage) {
		Scene scene = new Scene(pane);
//        URL css = getClass().getResource(getClass().getSimpleName() + ".css");
//        scene.getStylesheets().add(css.toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	protected <T extends Base> T load(Class<T> cls) {
		return FxHelper.load(cls);
	}

	protected Window getWindow() {
		return pane.getScene().getWindow();
	}

}
