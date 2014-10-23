package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ArtifactDef {

	private URI uri;
	private List<EndpointDef> provides;
	private List<EndpointDef> dependencies;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public List<EndpointDef> getProvides() {
		return provides;
	}

	public void setProvides(List<EndpointDef> provides) {
		this.provides = provides;
	}

	public List<EndpointDef> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<EndpointDef> dependencies) {
		this.dependencies = dependencies;
	}
}
