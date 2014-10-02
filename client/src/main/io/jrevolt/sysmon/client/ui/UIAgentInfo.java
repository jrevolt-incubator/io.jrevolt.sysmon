package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.VersionInfo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Objects;

import static io.jrevolt.sysmon.model.AgentInfo.Status;
import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class UIAgentInfo {

	private StringProperty cluster = new SimpleStringProperty();
	private StringProperty server = new SimpleStringProperty();
	private StringProperty version = new SimpleStringProperty();
	private StringProperty artifact = new SimpleStringProperty();
	private ObjectProperty<Instant> built = new SimpleObjectProperty<>();
	private ObjectProperty<Status> status = new SimpleObjectProperty<>();
	private ObjectProperty<Instant> lastUpdated = new SimpleObjectProperty<>();
	private ObjectProperty<Duration> ping = new SimpleObjectProperty<>();

	public UIAgentInfo(AgentInfo a) {
		update(a);
	}

	///

	public String getCluster() {
		return cluster.get();
	}

	public StringProperty clusterProperty() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster.set(cluster);
	}

	public String getServer() {
		return server.get();
	}

	public StringProperty serverProperty() {
		return server;
	}

	public void setServer(String server) {
		this.server.set(server);
	}

	public String getVersion() {
		return version.get();
	}

	public StringProperty versionProperty() {
		return version;
	}

	public void setVersion(String version) {
		this.version.set(version);
	}

	public String getArtifact() {
		return artifact.get();
	}

	public StringProperty artifactProperty() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact.set(artifact);
	}

	public Instant getBuilt() {
		return built.get();
	}

	public ObjectProperty<Instant> builtProperty() {
		return built;
	}

	public void setBuilt(Instant built) {
		this.built.set(built);
	}

	public Status getStatus() {
		return status.get();
	}

	public ObjectProperty<Status> statusProperty() {
		return status;
	}

	public void setStatus(Status status) {
		this.status.set(status);
	}

	public Instant getLastUpdated() {
		return lastUpdated.get();
	}

	public ObjectProperty<Instant> lastUpdatedProperty() {
		return lastUpdated;
	}

	public void setLastUpdated(Instant lastUpdated) {
		this.lastUpdated.set(lastUpdated);
	}

	public Duration getPing() {
		return ping.get();
	}

	public ObjectProperty<Duration> pingProperty() {
		return ping;
	}

	public void setPing(Duration ping) {
		this.ping.set(ping);
	}


	///

	public void update(AgentInfo a) {
		clusterProperty().set(a.getCluster());
		serverProperty().set(a.getServer());
		versionProperty().set(nonNull(a.getVersion()) ? a.getVersion().getVersion() : null);
		artifactProperty().set(nonNull(a.getVersion()) ? a.getVersion().getArtifact() : null);
		builtProperty().set(nonNull(a.getVersion()) ? a.getVersion().getBuildTimestamp() : null);
		statusProperty().set(a.getStatus());
		lastUpdatedProperty().set(a.getLastUpdated());
		if (nonNull(a.getLastChecked()) && nonNull(a.getLastUpdated())) {
			pingProperty().set(Duration.between(a.getLastChecked(), a.getLastUpdated()));
		}

	}
}
