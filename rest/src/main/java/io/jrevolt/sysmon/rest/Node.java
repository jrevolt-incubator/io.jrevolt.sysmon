package io.jrevolt.sysmon.rest;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Node {

	InetAddress host;

	Set<Service> services = new HashSet<>();

	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}

	public Set<Service> getServices() {
		return services;
	}
}
