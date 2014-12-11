package io.jrevolt.sysmon.client.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import io.jrevolt.sysmon.common.Utils;
import io.jrevolt.sysmon.model.SpringBootApp;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component @Singleton
public class FxHelper {

	static ScheduledExecutorService executor = Executors.newScheduledThreadPool(30);

	@PostConstruct
	void init() {
		System.out.println();
	}

	@PreDestroy
	private void close() {
		executor.shutdownNow();
	}

	static final LinkedList<Runnable> updateQueue = new LinkedList<Runnable>() {{
		executor.scheduleAtFixedRate(()-> Platform.runLater(()->{
			if (isEmpty()) { return; }
			synchronized (updateQueue) {
//				System.out.printf("updateQueue.size()=%d%n", size());
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
			Utils.runGuarded(runnable);
		} else {
			synchronized (updateQueue) { updateQueue.add(runnable); }
		}
	}

	static public void async(Runnable runnable) {
		executor.submit(()-> Utils.runGuarded(runnable));
	}

	static public <T> Future<T> async(Callable<T> callable) {
		return executor.submit(callable);
	}

	static public ScheduledExecutorService scheduler() {
		return executor;
	}

	static private StringProperty status = new SimpleStringProperty();

	static public StringProperty status() { return status; }

	static public boolean isVisible(Node node) {
		for (Node n = node; n != null; n = n.getParent()) {
			if (!n.isVisible()) { return false; }
		}
		return true;
	}
}