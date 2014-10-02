package io.jrevolt.sysmon.client.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.util.Callback;
import io.jrevolt.sysmon.model.SpringBootApp;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public abstract class FxHelper {

	static ScheduledExecutorService executor = Executors.newScheduledThreadPool(30);

	static final LinkedList<Runnable> updateQueue = new LinkedList<Runnable>() {{
		executor.scheduleAtFixedRate(()-> Platform.runLater(()->{
			if (isEmpty()) { return; }
			synchronized (updateQueue) {
				System.out.printf("updateQueue.size()=%d%n", size());
				while (!isEmpty()) { pop().run(); }
			}
		}), 1000, 500, TimeUnit.MILLISECONDS);
	}};

	static public <T extends Base> T load(Class<T> cls) {
		try {
			T controller = SpringBootApp.instance().lookup(cls);
			FXMLLoader loader = new FXMLLoader(cls.getResource(cls.getSimpleName() + ".fxml"));
//			loader.setController(controller);
			loader.setControllerFactory(aClass -> controller);
			controller.pane = loader.load();
			controller.initialize();
			return controller;
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	static public void fxasync(Runnable runnable) {
		Platform.runLater(runnable);
	}

	static public void fxupdate(Runnable runnable) {
		assert runnable != null;
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			synchronized (updateQueue) { updateQueue.add(runnable); }
		}
	}

	static public void async(Runnable runnable) {
		executor.submit(()->{
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	static public <T> Future<T> async(Callable<T> callable) {
		return executor.submit(callable);
	}
}
