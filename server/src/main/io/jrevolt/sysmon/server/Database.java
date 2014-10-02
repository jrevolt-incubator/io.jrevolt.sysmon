package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.DomainDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service @Singleton
public class Database {

	@Autowired
	DomainDef domain;

	private Map<String,AgentInfo> agents = new HashMap<>();

	@PostConstruct
	void init() {
		domain.getClusters().values().forEach(c->{
			c.getServers().forEach(s-> {
				agents.put(s, new AgentInfo(c.getName(), s, AgentInfo.Status.UNKNOWN, null, null));
			});
		});
	}

	public Map<String, AgentInfo> getAgents() {
		return agents;
	}

	public void updateAgent(AgentInfo info) {
		agents.get(info.getServer()).updateFrom(info);
		fireUpdate(info.getServer());
	}

	///

	final Map<String, Set<Runnable>> listeners = new ConcurrentHashMap<>();

	public void onUpdate(String serverName, Runnable r) {
		synchronized (listeners) {
			Set<Runnable> jobs = listeners.get(serverName);
			if (jobs == null) {
				jobs = new HashSet<>();
				listeners.put(serverName, jobs);
			}
			jobs.add(r);
		}
	}

	void fireUpdate(String serverName) {
		Set<Runnable> jobs = listeners.remove(serverName);
		if (jobs == null) { return; }
		jobs.parallelStream().forEach(Runnable::run);
	}
}
