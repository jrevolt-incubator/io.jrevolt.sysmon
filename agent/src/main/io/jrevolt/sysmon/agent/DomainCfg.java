package io.jrevolt.sysmon.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("domain")
public class DomainCfg {

	Map<String, Node> nodes;

	public Map<String, Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, Node> nodes) {
		this.nodes = nodes;
	}

	@PostConstruct
	void init() {
		System.out.println();
	}
}
