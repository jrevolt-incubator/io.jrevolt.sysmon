package org.jrevolt.sysmon.agent;

import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Service
public class DnsScanner {

	ExecutorService executor;

	@PostConstruct
	void init() {
		executor = Executors.newFixedThreadPool(10);
	}

	@PreDestroy
	void close() throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
		executor.shutdownNow();
	}

	public void scan() {
		try {
			List<Future<?>> result = new LinkedList<>();
			Resolver resolver = new SimpleResolver();
			InetAddress localhost = InetAddress.getLocalHost();
			String base = localhost.getHostAddress().replaceFirst("\\.\\d+\\.\\d+$", "");
			for (int i = 0; i<255; i++) {
				for (int j = 0; j < 255; j++) {
					result.add(executor.submit(() -> {
//						Lookup lookup = new Lookup();
					}));
				}
			}

		} catch (UnknownHostException e) {
			throw new UnsupportedOperationException(e);
		}
	}





}
