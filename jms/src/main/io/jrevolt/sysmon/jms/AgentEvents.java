package io.jrevolt.sysmon.jms;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.NodeDef;

import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface AgentEvents {

	@JMS
	default void started(AgentInfo info) {}

	@JMS(timeToLive = 5000L)
	default void status(NodeDef node) {}

	@JMS
	default void applications() {}

	@JMS
	default void ports() {}

	@JMS
	default void provides(List<URI> uris) {}

	@JMS
	default void serverChecked(String clusterName, String serverName) {}

	@JMS(timeToLive = 5000L)
	default void restarting(String cluster, String host) {}
}
