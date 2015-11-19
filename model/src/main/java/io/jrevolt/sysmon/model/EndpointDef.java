package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
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

	public EndpointDef(EndpointDef src, ServerDef server) {
		this.cluster = server.getCluster();
		this.server = server.getName();
		this.jndi = src.jndi;
		this.uri = src.uri;
		this.test = src.test;
		this.type = src.type;
		this.status = src.status;
	}

	public EndpointDef(EndpointDef src, String cluster, String server, String hostname) {
		this.cluster = cluster;
		this.server = server;
		this.jndi = src.jndi;
		this.uri = src.uri;
		if (hostname != null) {
			this.uri = UriBuilder.fromUri(src.uri).host(hostname).build();
		}
		this.test = src.test;
		this.type = src.type;
		this.status = src.status;
	}

	public EndpointDef(String uri) {
		this.uri = URI.create(uri);
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
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("uri", uri)
				.toString();
	}
}
