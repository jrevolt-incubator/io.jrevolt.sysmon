package io.jrevolt.sysmon.cloud;

import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Utils {

	public interface Interruptible {
		void run() throws InterruptedException;
	}

	static public void interruptible(Interruptible action) {
		try {
			action.run();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

}
