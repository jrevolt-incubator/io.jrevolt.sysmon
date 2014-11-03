package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.EndpointStatus;
import io.jrevolt.sysmon.model.EndpointType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Endpoint {

	StringProperty cluster = new SimpleStringProperty();
	StringProperty server = new SimpleStringProperty();
	StringProperty artifact = new SimpleStringProperty();
	ObjectProperty<URI> uri = new SimpleObjectProperty<>();
	ObjectProperty<EndpointType> type = new SimpleObjectProperty<>();
	ObjectProperty<EndpointStatus> status = new SimpleObjectProperty<>();
	StringProperty comment = new SimpleStringProperty();

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

	public String getArtifact() {
		return artifact.get();
	}

	public StringProperty artifactProperty() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact.set(artifact);
	}

	public URI getUri() {
		return uri.get();
	}

	public ObjectProperty<URI> uriProperty() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri.set(uri);
	}

	public EndpointType getType() {
		return type.get();
	}

	public ObjectProperty<EndpointType> typeProperty() {
		return type;
	}

	public void setType(EndpointType type) {
		this.type.set(type);
	}

	public EndpointStatus getStatus() {
		return status.get();
	}

	public ObjectProperty<EndpointStatus> statusProperty() {
		return status;
	}

	public void setStatus(EndpointStatus status) {
		this.status.set(status);
	}

	public String getComment() {
		return comment.get();
	}

	public StringProperty commentProperty() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment.set(comment);
	}
}
