package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.DomainDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
		Instant lastChecked = Instant.now();
		db.getAgents().values().stream().forEach(a->a.setLastChecked(lastChecked));
		events.ping(null, null);
	}


}
