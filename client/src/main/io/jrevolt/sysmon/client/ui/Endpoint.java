package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.ClusterDef;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Endpoint {

	static public enum Type { PROXY, PROVIDED, DEPENDENCY, MANAGEMENT }
	static public enum Status { UNKNOWN, CHECKING, OK, UNAVALABLE, ERROR, UNDETERMINED }

	ObjectProperty<URI> uri;
	ObjectProperty<Type> type;
	StringProperty server;
	ObjectProperty<ClusterDef> cluster;

	ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNKNOWN);

	StringProperty comment = new SimpleStringProperty("");

	public Endpoint(URI uri,Type type, String server, ClusterDef cluster) {
		this.uri = new SimpleObjectProperty<>(uri);
		this.type = new SimpleObjectProperty<>(type);
		this.server = new SimpleStringProperty(server);
		this.cluster = new SimpleObjectProperty<>(cluster);
	}

	public ObjectProperty<URI> uri() { return uri; }
	public ObjectProperty<Type> type() { return type; }
	public StringProperty server() { return server; }
	public ObjectProperty<ClusterDef> cluster() { return cluster; }
	public ObjectProperty<Status> status() { return status; }
	public StringProperty comment() { return comment; }

	public URI getUri() { return uri.getValue(); }
	public Type getType() { return type.get(); }
	public String getServer() { return server.get(); }
	public ClusterDef getCluster() { return cluster.get(); }
	public Status getStatus() { return status.get(); }
	public String getComment() { return comment.get(); }

	public String getClusterName() { return cluster.get().getName(); }
	public String getStatusName() { return status.get().name(); }
	public String getTypeName() { return type.get().name(); }
}
