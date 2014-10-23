package io.jrevolt.sysmon.jms;

import io.jrevolt.sysmon.model.ClusterDef;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface ServerEvents {

	@JMS(topic = true, timeToLive = 20000L)
	@JMSSelector("(cluster is null or cluster='${sysmon.agent.clusterName}') and (server is null or server='${sysmon.agent.serverName}')")
	default void ping(@JMSProperty String cluster, @JMSProperty String server) {}

	@JMS(topic=true, timeToLive = 60000L)
	@JMSSelector("(cluster is null or cluster='${sysmon.agent.clusterName}') and (server is null or server='${sysmon.agent.serverName}')")
	default void restart(@JMSProperty String cluster, @JMSProperty String server) {}

	@JMS(topic = true, timeToLive = 60000L)
	@JMSSelector("name='${sysmon.agent.clusterName}'")
	default void checkCluster(@JMSProperty String name, ClusterDef clusterDef) {}

	@JMS(timeToLive = 60000L)
	@JMSSelector("name='${sysmon.agent.serverName}'")
	default void checkServer(@JMSProperty String name, ClusterDef clusterDef) {}
}
