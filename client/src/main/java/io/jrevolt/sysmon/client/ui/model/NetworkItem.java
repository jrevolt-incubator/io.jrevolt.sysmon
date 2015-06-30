package io.jrevolt.sysmon.client.ui.model;

import io.jrevolt.sysmon.model.NetworkInfo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class NetworkItem {

	private StringProperty cluster = new SimpleStringProperty();
	private StringProperty server = new SimpleStringProperty();
	private StringProperty destination = new SimpleStringProperty();
	private IntegerProperty port = new SimpleIntegerProperty();
	private StringProperty sourceIP = new SimpleStringProperty();
	private StringProperty destinationIP = new SimpleStringProperty();
	private SimpleObjectProperty<Long> time = new SimpleObjectProperty<>();
	private ObjectProperty<NetworkInfo.Status> status = new SimpleObjectProperty<>();
	private StringProperty comment = new SimpleStringProperty();

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

	public String getSourceIP() {
		return sourceIP.get();
	}

	public StringProperty sourceIPProperty() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP.set(sourceIP);
	}

	public String getDestination() {
		return destination.get();
	}

	public StringProperty destinationProperty() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination.set(destination);
	}

	public String getDestinationIP() {
		return destinationIP.get();
	}

	public StringProperty destinationIPProperty() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP.set(destinationIP);
	}

	public int getPort() {
		return port.get();
	}

	public IntegerProperty portProperty() {
		return port;
	}

	public void setPort(int port) {
		this.port.set(port);
	}

	public Long getTime() {
		return time.get();
	}

	public SimpleObjectProperty<Long> timeProperty() {
		return time;
	}

	public void setTime(Long time) {
		this.time.set(time);
	}

	public NetworkInfo.Status getStatus() {
		return status.get();
	}

	public ObjectProperty<NetworkInfo.Status> statusProperty() {
		return status;
	}

	public void setStatus(NetworkInfo.Status status) {
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
