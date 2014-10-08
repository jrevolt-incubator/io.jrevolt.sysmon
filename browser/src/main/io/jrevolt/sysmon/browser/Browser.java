package io.jrevolt.sysmon.browser;

import org.springframework.boot.loader.MvnLauncher;
import org.springframework.boot.loader.mvn.MvnArtifact;

import javafx.fxml.FXML;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Browser {


	@FXML void run() {
		MvnLauncher l = new MvnLauncher();
		ClassLoader cl = l.resolve(MvnArtifact.parse("io.jrevolt.sysmon:io.jrevolt.sysmon.client:integration-SNAPSHOT"),
											Thread.currentThread().getContextClassLoader());
		try {
			Class<?> cls = Class.forName("io.jrevolt.sysmon.client.ClientMain", true, cl);
			Method m = cls.getMethod("main", String[].class);
			m.invoke(null);
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
