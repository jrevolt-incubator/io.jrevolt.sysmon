package io.jrevolt.sysmon.model;

import java.time.Instant;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class AgentInfo {

	static public enum Status { STARTED, ONLINE, UNAVAILABLE }

	String cluster;
	String server;
	Status status;
	String version;

	public AgentInfo(String cluster, String server) {
		this.cluster = cluster;
		this.server = server;
	}

	public String getCluster() {
		return cluster;
	}

	public String getServer() {
		return server;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
