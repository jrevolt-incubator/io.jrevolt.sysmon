package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.Log;

import javax.validation.Valid;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ProxyDef extends HostDef {

	public enum Type {
		INTERNAL,  	// not visible from outside of system
		EXTERNAL,	// exposed to users/clients
		PUBLIC,		// exposed to internet
		INTRANET, // QDH used by data
	}

	private Type type;
	private URI uri;
	private List<EndpointDef> provides = new LinkedList<>();
	private List<RoutingDef> routing = new LinkedList<>();
	@Valid
	private Monitoring monitoring;

	///


	public ProxyDef() {
	}

	public ProxyDef(String name) {
		setProxyName(name);
	}

	///

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

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
		if (isNull(uri)) {
			String s = String.format("https://%s", getName());
			Log.warn(this, "Missing URI. Defaulting to: {}", s);
			setUri(URI.create(s));
		}
		if (nonNull(monitoring)) { monitoring.init(this); }
	}
}
