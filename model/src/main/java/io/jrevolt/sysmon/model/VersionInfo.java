package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.InstantConverter;
import io.jrevolt.sysmon.common.Version;


import org.springframework.boot.launcher.mvn.Artifact;

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
		this.artifact = src.isUnknown() ? src.getArtifactUri() : new Artifact(src.getArtifactUri()).getArtifactId();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VersionInfo that = (VersionInfo) o;

		if (artifact != null ? !artifact.equals(that.artifact) : that.artifact != null) return false;
		if (buildTimestamp != null ? !buildTimestamp.equals(that.buildTimestamp) : that.buildTimestamp != null)
			return false;
		if (version != null ? !version.equals(that.version) : that.version != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = artifact != null ? artifact.hashCode() : 0;
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (buildTimestamp != null ? buildTimestamp.hashCode() : 0);
		return result;
	}
}
