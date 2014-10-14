package io.jrevolt.sysmon.client.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.prefs.Preferences;

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
		getWindow().setOnCloseRequest(e -> {
			close();
		});
	}

	protected <T extends Base> T load(Class<T> cls) {
		return FxHelper.load(cls);
	}

	protected void close() {}

	protected Window getWindow() {
		return pane.getScene().getWindow();
	}

	protected void registerLayoutPersistor(Class<? extends Base> root, TableView<?> table) {
		Preferences prefs = Preferences.userNodeForPackage(root);
		table.getColumns().forEach(c-> {
			c.prefWidthProperty().set(prefs.getDouble(c.getId(), 70));
			c.widthProperty().addListener((observable, oldValue, newValue) -> {
				prefs.putDouble(c.getId(), c.getWidth());
			});
		});
	}
}
