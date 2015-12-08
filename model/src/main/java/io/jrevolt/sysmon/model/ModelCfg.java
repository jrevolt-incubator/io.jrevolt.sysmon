package io.jrevolt.sysmon.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
@ConfigurationProperties("io.jrevolt.sysmon.model")
public class ModelCfg {

	private Pattern proxyFilter;
	private Pattern clusterFilter;

	public Pattern getProxyFilter() {
		return proxyFilter;
	}

	public void setProxyFilter(Pattern proxyFilter) {
		this.proxyFilter = proxyFilter;
	}

	public Pattern getClusterFilter() {
		return clusterFilter;
	}

	public void setClusterFilter(Pattern clusterFilter) {
		this.clusterFilter = clusterFilter;
	}
}
