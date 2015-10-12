package io.jrevolt.sysmon.model;

import java.net.InetAddress;
import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ProxyDef {

	static public enum Type { INTERNAL, ADMIN, INTRANET, PUBLIC }

	private String name;
	private Type type;
	private URI endpoint;
	private List<RoutingDef> routing;

	///

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public URI getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(URI endpoint) {
		this.endpoint = endpoint;
	}

	public List<RoutingDef> getRouting() {
		return routing;
	}

	public void setRouting(List<RoutingDef> routing) {
		this.routing = routing;
	}


	///

	// used by YAML
	public void setProxyName(String name) {
		this.name = name;
	}
}
