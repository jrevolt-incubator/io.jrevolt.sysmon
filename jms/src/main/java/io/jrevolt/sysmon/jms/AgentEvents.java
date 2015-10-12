package io.jrevolt.sysmon.jms;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.EndpointDef;
import io.jrevolt.sysmon.model.NodeDef;
import io.jrevolt.sysmon.model.ServerDef;

import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface AgentEvents {

	@JMS(timeToLive = 20000L)
	default void started(AgentInfo info) {}

	@JMS(timeToLive = 60000L)
	default void status(AgentInfo info) {}

	@JMS(timeToLive = 30000L)
	default void restarting(AgentInfo info) {}

	@JMS(timeToLive = 60000L)
	default void clusterStatus(ClusterDef cluster) {}

	@JMS(timeToLive = 60000L)
	default void serverStatus(ServerDef server) {}


}
