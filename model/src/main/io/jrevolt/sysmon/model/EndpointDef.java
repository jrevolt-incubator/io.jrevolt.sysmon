package io.jrevolt.sysmon.model;

import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class EndpointDef {

	private String jndi;
	private URI uri;
	private URI status;

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

	public URI getStatus() {
		return status;
	}

	public void setStatus(URI status) {
		this.status = status;
	}
}
