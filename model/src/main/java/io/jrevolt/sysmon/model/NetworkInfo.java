package io.jrevolt.sysmon.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class NetworkInfo {

	static public enum Status { CONNECTED, UNRESOLVED, UNREACHABLE, TIMEOUT, REFUSED, ERROR, UNKNOWN }

	private String cluster;
	private String server;

	private String destination;
	private int    port;

	private String srcAddress;
	private String dstAddress;

	private Status status;
	private String comment;

	public NetworkInfo() {
	}

	public NetworkInfo(String cluster, String server, String destination, int port) {
		this.cluster = cluster;
		this.server = server;
		this.destination = destination;
		this.port = port;
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(String srcAddress) {
		this.srcAddress = srcAddress;
	}

	public String getDstAddress() {
		return dstAddress;
	}

	public void setDstAddress(String dstAddress) {
		this.dstAddress = dstAddress;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	///


	@Override
	public String toString() {
		return String.format("%s->%s:%s (%s)", server, destination, port, status);
	}
}
