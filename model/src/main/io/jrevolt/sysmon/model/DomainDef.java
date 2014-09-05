package io.jrevolt.sysmon.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("domain")
public class DomainDef {

	Map<String, NodeDef> nodes = new HashMap<>();

	public Map<String, NodeDef> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, NodeDef> nodes) {
		this.nodes = nodes;
	}

	@PostConstruct
	void init() {
		// copy the node key (hostname) into the node.hostname property
		nodes.entrySet().forEach(e -> e.getValue().setHostname(e.getKey()));
	}

	///

	public NodeDef currentNode() {
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			return nodes.get(hostname);
		} catch (UnknownHostException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
