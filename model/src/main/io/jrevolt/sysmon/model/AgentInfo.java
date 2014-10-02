package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.InstantConverter;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.time.Instant;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class AgentInfo {

	static public enum Status { STARTED, ONLINE, CHECKING, UNAVAILABLE, UNKNOWN }

	String cluster;
	String server;
	Status status;
	String version;

	@XStreamConverter(value=InstantConverter.class)
	Instant lastUpdated;

	public AgentInfo() {
	}

	public AgentInfo(String cluster, String server, Status status, String version, Instant lastUpdated) {
		this.cluster = cluster;
		this.server = server;
		this.status = status;
		this.version = version;
		this.lastUpdated = lastUpdated;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
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

	public Instant getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Instant lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	///

	public void updateFrom(AgentInfo info) {
		setStatus(info.getStatus());
		setVersion(info.getVersion());
		setLastUpdated(info.getLastUpdated());
	}

	///

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
