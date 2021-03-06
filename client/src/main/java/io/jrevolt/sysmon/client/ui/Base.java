package io.jrevolt.sysmon.client.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.SnapshotResult;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

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
//		final URL url = getClass().getResource(getClass().getSimpleName() + ".css");
//		if (url != null) { pane.getStylesheets().add(url.toString()); }
	}

	public void show() {
		show(FxMain.stage());
	}

	public void show(Stage stage) {
		Scene scene = new Scene(pane);
		stage.setScene(scene);

//		URL url = getClass().getResource(getClass().getSimpleName() + ".css");
//		if (url != null) { scene.getStylesheets().add(url.toString()); }

		stage.show();
		getWindow().setOnCloseRequest(e -> {
			close();
		});
	}

	protected <C extends Base<P>, P extends Pane> C load(Class<C> cls) {
		return FxHelper.load(cls);
	}

	protected void close() {}

	protected Window getWindow() {
		return pane.getScene().getWindow();
	}

	protected void registerLayoutPersistor(TableView<?> table) {
		Preferences prefs = Preferences.userRoot().node(getClass().getName());
		table.getColumns().forEach(c-> {
			if (c.getId() == null) { return; }
			c.prefWidthProperty().set(prefs.getDouble(c.getId(), 70));
			c.widthProperty().addListener((observable, oldValue, newValue) -> FxHelper.async(() -> {
				prefs.putDouble(c.getId(), c.getWidth());
			}));
		});
	}
}
