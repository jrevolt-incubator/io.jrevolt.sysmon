package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
	private Map<String,ClusterDef> clusters = new HashMap<>();

	@PostConstruct
	void init() {
		domain.getClusters().forEach(c -> clusters.put(c.getName(), c));
		domain.getClusters().forEach(c -> c.getServers().forEach(s -> {
			agents.put(s, new AgentInfo(c.getName(), s, AgentInfo.Status.UNKNOWN, null, null));
		}));
	}

	@PreDestroy
	void close() {
	}

	public AgentInfo getAgent(String server) {
		return agents.get(server);
	}

	public Map<String, AgentInfo> getAgents() {
		return agents;
	}

	public ClusterDef getCluster(String name) {
		return clusters.get(name);
	}

	public void updateAgent(AgentInfo info) {
		AgentInfo our = getAgent(info.getServer());
		if (our == null) { return; }

		info.setLastChecked(our.getLastChecked());

		our.updateFrom(info);
		fireUpdate(our.getServer());
	}

	public void updateCluster(ClusterDef cluster) {
		clusters.get(cluster.getName()).update(cluster);
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
