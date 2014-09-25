package io.jrevolt.sysmon.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jdk.nashorn.internal.runtime.ScriptObject;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("domain")
public class DomainDef {

	String name;
	Map<String, ClusterDef> clusters = new LinkedHashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, ClusterDef> getClusters() {
		return clusters;
	}

	public void setClusters(Map<String, ClusterDef> clusters) {
		this.clusters = clusters;
	}

	@PostConstruct
	void init() {
		clusters.entrySet().forEach(e -> e.getValue().setName(e.getKey()));
	}

	public NodeDef currentNode() {
		throw new UnsupportedOperationException(); // todo implement this
	}

}
