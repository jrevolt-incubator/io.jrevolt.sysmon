package io.jrevolt.sysmon.jms;

import io.jrevolt.sysmon.model.ClusterDef;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface ServerEvents {

	@JMS(topic = true, timeToLive = 5000L)
	@JMSSelector("(cluster is null or cluster='${cluster.name}') and (server is null or server='${server.name}')")
	default void ping(@JMSProperty String cluster, @JMSProperty String server) {}

	@JMS(topic=true)
	@JMSSelector("(cluster is null or cluster='${cluster.name}') and (server is null or server='${server.name}')")
	default void restart(@JMSProperty String cluster, @JMSProperty String server) {}

	@JMS
	default void reportServers() {}

	@JMS(topic = true)
	default void reportProvides() {}

	@JMS(topic = true)
	@JMSSelector("name='${cluster.name}'")
	default void checkCluster(@JMSProperty String name, ClusterDef clusterDef) {}
}
