package io.jrevolt.sysmon.client.ui;

import static io.jrevolt.sysmon.model.AgentInfo.Status;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import io.jrevolt.sysmon.model.AgentInfo;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class UIAgentInfo {

	private StringProperty cluster = new SimpleStringProperty();
	private StringProperty server = new SimpleStringProperty();

	private ObjectProperty<Status> status = new SimpleObjectProperty<>();
	private ObjectProperty<Duration> lastChecked = new SimpleObjectProperty<>();
	private ObjectProperty<Duration> lastUpdated = new SimpleObjectProperty<>();

	private ObjectProperty<Duration> lastModified = new SimpleObjectProperty<>();
	private ObjectProperty<Long> ping = new SimpleObjectProperty<>();

	private StringProperty version = new SimpleStringProperty();
	private ObjectProperty<LocalDateTime> built = new SimpleObjectProperty<>();

	{
//		lastChecked.addListener((observable, oldValue, newValue) -> lastChecked.set(Duration.between(newValue, Instant.now())));
//		lastUpdated.addListener((observable, oldValue, newValue) -> ping.set(Duration.between(getLastChecked(), newValue)));
	}

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

	public Status getStatus() {
		return status.get();
	}

	public ObjectProperty<Status> statusProperty() {
		return status;
	}

	public void setStatus(Status status) {
		this.status.set(status);
	}

	public Duration getLastUpdated() {
		return lastUpdated.get();
	}

	public ObjectProperty<Duration> lastUpdatedProperty() {
		return lastUpdated;
	}

	public void setLastUpdated(Duration lastUpdated) {
		this.lastUpdated.set(lastUpdated);
	}

	public Duration getLastModified() {
		return lastModified.get();
	}

	public ObjectProperty<Duration> lastModifiedProperty() {
		return lastModified;
	}

	public void setLastModified(Duration lastModified) {
		this.lastModified.set(lastModified);
	}

	public Duration getLastChecked() {
		return lastChecked.get();
	}

	public ObjectProperty<Duration> lastCheckedProperty() {
		return lastChecked;
	}

	public void setLastChecked(Duration lastChecked) {
		this.lastChecked.set(lastChecked);
	}

	public Long getPing() {
		return ping.get();
	}

	public ObjectProperty<Long> pingProperty() {
		return ping;
	}

	public void setPing(Long ping) {
		this.ping.set(ping);
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

	public LocalDateTime getBuilt() {
		return built.get();
	}

	public ObjectProperty<LocalDateTime> builtProperty() {
		return built;
	}

	public void setBuilt(LocalDateTime built) {
		this.built.set(built);
	}


	///

	public void update(AgentInfo a) {
		final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		cluster.set(a.getCluster());
		server.set(a.getServer());
		version.set(nonNull(a.getVersion()) ? a.getVersion().getVersion() : null);
		built.set(nonNull(a.getVersion())
							 ? LocalDateTime.from(a.getVersion().getBuildTimestamp().truncatedTo(ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()))
							 : null);
		status.set(a.getStatus());
		lastChecked.set(nonNull(a.getLastChecked())
				? Duration.between(now, a.getLastChecked().truncatedTo(ChronoUnit.SECONDS))
				: null);
		lastUpdated.set(nonNull(a.getLastUpdated())
				? Utils.friendlify(Duration.between(now, a.getLastUpdated().truncatedTo(ChronoUnit.SECONDS)))
				: null);
		lastModified.set(nonNull(a.getLastModified())
				? Utils.friendlify(Duration.between(now.truncatedTo(ChronoUnit.SECONDS), a.getLastModified().truncatedTo(ChronoUnit.SECONDS)))
				: null);

		lastChecked.set(nonNull(a.getLastChecked())
				? Duration.between(now.truncatedTo(ChronoUnit.SECONDS), a.getLastChecked().truncatedTo(ChronoUnit.SECONDS))
				:null);
		ping.set((nonNull(a.getLastChecked()) && nonNull(a.getLastUpdated()))
				? Duration.between(a.getLastChecked(), a.getLastUpdated()).toMillis()
				: null);
	}


}
