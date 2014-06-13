package org.jrevolt.sysmon.client.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.jrevolt.sysmon.client.ClientApp;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public abstract class FxHelper {

	static ExecutorService executor = Executors.newFixedThreadPool(8);

	static public <T extends Base> T load(Class<T> cls) {
		try {
			T controller = ClientApp.instance().lookup(cls);
			FXMLLoader loader = new FXMLLoader(cls.getResource(cls.getSimpleName() + ".fxml"));
			loader.setController(controller);
			controller.pane = loader.load();
			return controller;
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	static public void fxasync(Runnable runnable) {
		Platform.runLater(runnable);
	}

	static public void async(Runnable runnable) {
		executor.submit(runnable);
	}

	static public <T> Future<T> async(Callable<T> callable) {
		return executor.submit(callable);
	}
}
