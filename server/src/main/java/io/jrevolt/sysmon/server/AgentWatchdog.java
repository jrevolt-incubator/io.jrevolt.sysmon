package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.DomainDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.jrevolt.sysmon.common.Utils.runGuarded;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class AgentWatchdog {

	@Autowired
	DomainDef domain;

	@Autowired
	Database db;

	@Autowired
	ServerEvents events;

//	@Scheduled(initialDelay = 5000L, fixedRate = 20000L)
//	void refresh() {
//		domain.getClusters().values().stream()
//				.flatMap(c->c.getServers().stream())
//				.map(db::getAgent).filter(a->a!=null)
//				.forEach(this::pingAgent);
//
//	}

	@Scheduled(initialDelay = 5000L, fixedRate = 10000L)
	void pingAllAgents() {
		runGuarded(()->{
			synchronized (db) {
				Instant now = Instant.now();
				events.ping(null, null);
				db.getAgents().values().forEach(a-> {
					if (a.getStatus().equals(AgentInfo.Status.CHECKING)) { return; }
					a.setLastChecked(now);
				});
			}
		});
	}

	@Scheduled(initialDelay = 5000L, fixedRate = 10000L)
	void checkTimeouts() {
		runGuarded(()->{
			synchronized (db) {
				db.getAgents().values().forEach(a->{
					boolean isTimeout = a.getLastChecked() != null && a.getLastChecked().until(Instant.now(), ChronoUnit.SECONDS) > 10;
					if (isTimeout) {
						a.setStatus(AgentInfo.Status.UNKNOWN);
						a.setVersion(null);
						a.setLastModified(Instant.now());
					}
				});
			}
		});
	}


}
