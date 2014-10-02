package io.jrevolt.sysmon.server;

import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class Utils {

	static ScheduledExecutorService executor = Executors.newScheduledThreadPool(30);

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
