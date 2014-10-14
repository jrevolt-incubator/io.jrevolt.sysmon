package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class EndpointDef {

	private String cluster;
	private String server;

	private String jndi;
	private URI uri;
	private URI test;
	private EndpointType type;
	private EndpointStatus status;
	private String comment;

	public EndpointDef() {
	}

	public EndpointDef(EndpointDef src, String hostname) {
		this.cluster = src.cluster;
		this.server = src.server;
		this.jndi = src.jndi;
		this.uri = UriBuilder.fromUri(src.uri).host(hostname).build();
		this.type = src.type;
		this.status = src.status;
	}

	///


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

	public String getJndi() {
		return jndi;
	}

	public void setJndi(String jndi) {
		this.jndi = jndi;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public URI getTest() {
		return test;
	}

	public void setTest(URI test) {
		this.test = test;
	}

	public EndpointType getType() {
		return type;
	}

	public void setType(EndpointType type) {
		this.type = type;
	}

	public EndpointStatus getStatus() {
		return status;
	}

	public void setStatus(EndpointStatus status) {
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
		return new ToStringBuilder(this)
				.append("uri", uri)
				.toString();
	}
}
