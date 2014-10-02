package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.InstantConverter;
import io.jrevolt.sysmon.common.Version;

import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.time.Instant;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class VersionInfo {

	private String artifact;
	private String version;

	@XStreamConverter(value=InstantConverter.class)
	private Instant buildTimestamp;

	public VersionInfo() {
	}

	public VersionInfo(Version src) {
		this.artifact = src.getArtifactUri();
		this.version = src.getArtifactVersion();
		this.buildTimestamp = src.getTimestamp();
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

	public Instant getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(Instant buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}
}
