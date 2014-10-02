package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.AgentInfo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class UIAgentInfo {

	StringProperty cluster = new SimpleStringProperty();
	StringProperty server = new SimpleStringProperty();
	StringProperty version = new SimpleStringProperty();
	StringProperty status = new SimpleStringProperty();
	ObjectProperty<Instant> lastUpdated = new SimpleObjectProperty<>();

	public UIAgentInfo(AgentInfo a) {
		update(a);
	}

	StringProperty cluster() { return cluster; }
	StringProperty server() { return server; }
	StringProperty version() { return status; }
	StringProperty status() { return status; }
	ObjectProperty<Instant> lastUpdated() { return lastUpdated; }

	public String getCluster() {
		return cluster.get();
	}

	public StringProperty clusterProperty() {
		return cluster;
	}

	public String getServer() {
		return server.get();
	}

	public StringProperty serverProperty() {
		return server;
	}

	public String getStatus() {
		return status.get();
	}

	public StringProperty statusProperty() {
		return status;
	}

	public String getVersion() {
		return version.get();
	}

	public StringProperty versionProperty() {
		return version;
	}

	public Instant getLastUpdated() {
		return lastUpdated.get();
	}

	public ObjectProperty<Instant> lastUpdatedProperty() {
		return lastUpdated;
	}

	///

	public void update(AgentInfo a) {
		cluster().set(a.getCluster());
		server().set(a.getServer());
		version().set(a.getVersion());

		status().set(a.getStatus().name());

		lastUpdated().set(a.getLastUpdated());
	}
}
