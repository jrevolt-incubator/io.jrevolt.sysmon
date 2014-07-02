package io.jrevolt.sysmon.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("domain")
public class DomainCfg {

	Map<String, Node> nodes = new HashMap<>();

	public Map<String, Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, Node> nodes) {
		this.nodes = nodes;
	}

	@PostConstruct
	void init() {
		// copy the node key (hostname) into the node.hostname property
		nodes.entrySet().forEach(e -> e.getValue().setHostname(e.getKey()));
	}
}
