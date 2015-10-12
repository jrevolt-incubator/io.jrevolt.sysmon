package io.jrevolt.sysmon.model;

import java.time.Instant;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class StatusInfo {

	private String name;
	private String artifact;
	private String version;
	private Instant timestamp;

	public StatusInfo() {
	}

	public StatusInfo(String name, String artifact, String version, Instant timestamp) {
		this.name = name;
		this.artifact = artifact;
		this.version = version;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}
