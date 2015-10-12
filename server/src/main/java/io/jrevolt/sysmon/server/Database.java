package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.NetworkInfo;
import io.jrevolt.sysmon.model.ServerDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service @Singleton
public class Database {

	@Autowired
	DomainDef domain;

	private Map<String,AgentInfo> agents = new HashMap<>();
	private Map<String,ClusterDef> clusters = new HashMap<>();
	private Map<String,ServerDef> servers = new HashMap<>();

	@PostConstruct
	void init() {
		domain.getClusters().forEach(c -> clusters.put(c.getClusterName(), c));
		domain.getClusters().forEach(c -> c.getServers().forEach(s -> {
			agents.put(s.getName(), new AgentInfo(c.getClusterName(), s.getName(), AgentInfo.Status.UNKNOWN, null, null));
			servers.put(s.getName(), s);
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

	public List<ClusterDef> getClusters() {
		return clusters.values().stream().collect(Collectors.toList());
	}

	public ClusterDef getCluster(String name) {
		return clusters.get(name);
	}

	public ServerDef getServer(String name) {
		return servers.get(name);
	}

	public List<NetworkInfo> getNetworkInfo() {
		return domain.getClusters().stream()
				.flatMap(c -> c.getServers().stream())
				.flatMap(s -> s.getNetwork().stream())
				.collect(Collectors.toList());
	}



	///

	public void updateAgent(AgentInfo info) {
		AgentInfo our = getAgent(info.getServer());
		if (our == null) { return; }
		
		boolean modified
				= !Objects.equals(our.getStatus(), info.getStatus())
				|| !Objects.equals(our.getVersion(), info.getVersion());

		our.setLastUpdated(info.getLastUpdated());
		if (modified) {
			our.setStatus(info.getStatus());
			our.setVersion(info.getVersion());
			our.setLastModified(info.getLastUpdated());
		}

		fireUpdate(our.getServer());
	}

	public void updateCluster(ClusterDef cluster) {
		clusters.get(cluster.getClusterName()).update(cluster);
	}

	public void updateServer(ServerDef server) {
		servers.get(server.getName()).update(server);
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
