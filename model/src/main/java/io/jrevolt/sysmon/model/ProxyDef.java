package io.jrevolt.sysmon.model;

import java.net.InetAddress;
import java.net.URI;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ProxyDef extends HostDef {

	public enum Type {
		INTERNAL,  	// not visible from outside of system
		EXTERNAL,	// exposed to users/clients
		PUBLIC,		// exposed to internet
	}

	private Type type;
	private URI endpoint;
	private List<RoutingDef> routing;
	private Monitoring monitoring;

	///

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

	@Override
	public Monitoring getMonitoring() {
		return monitoring;
	}

	public void setMonitoring(Monitoring monitoring) {
		this.monitoring = monitoring;
	}

	///

	// used by YAML
	public void setProxyName(String name) {
		this.name = name;
	}

	///

	void init(DomainDef domain) {
		if (nonNull(monitoring)) { monitoring.init(this); }
	}
}
