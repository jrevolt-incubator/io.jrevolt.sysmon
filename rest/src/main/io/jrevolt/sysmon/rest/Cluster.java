package io.jrevolt.sysmon.rest;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Cluster {

	String name;

	Set<Node> nodes = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Node> getNodes() {
		return nodes;
	}
}
