package io.jrevolt.sysmon.server;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.DomainDef;

import static io.jrevolt.sysmon.common.Utils.runGuarded;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class ServerWatchdog {

	@Autowired
	DomainDef domain;

	@Autowired
	Database db;

	@Autowired
	ServerEvents events;

//	@Scheduled(initialDelay = 5000L, fixedRate = 10000L)
	void checkAll() {
		runGuarded(()->{
			Instant lastChecked = Instant.now();
			db.getAgents().values().stream().forEach(a->a.setLastChecked(lastChecked));
			events.ping(null, null);
		});
	}

//	@Scheduled(fixedRate = 10000L)
	public void checkCluster() {
		new Thread("ShutdownOnDemand") {
			@Override
			public void run() {
				domain.getClusters().parallelStream().forEach(c-> runGuarded(() -> {
					events.checkCluster(c.getClusterName(), c);
				}));
			}
		}.run();
//		ForkJoinPool pool = new ForkJoinPool(30);
	}


}
