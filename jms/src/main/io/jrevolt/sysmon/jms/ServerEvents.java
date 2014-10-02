package io.jrevolt.sysmon.jms;

import io.jrevolt.sysmon.model.ClusterDef;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface ServerEvents {

	@JMS(topic = true, timeToLive = 5000L)
	@JMSSelector("(clusterName is null or clusterName='${cluster.name}') and (serverName is null or serverName='${server.name}')")
	default void ping(@JMSProperty String clusterName, @JMSProperty String serverName) {}

	@JMS(topic=true)
	default void restart() {}

	@JMS
	default void reportServers() {}

	@JMS(topic = true)
	default void reportProvides() {}

	@JMS(topic = true)
	@JMSSelector("name='${cluster.name}'")
	default void checkCluster(@JMSProperty String name, ClusterDef clusterDef) {}
}
