package io.jrevolt.sysmon.jms;

import io.jrevolt.sysmon.model.ClusterDef;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface ServerEvents {

	@JMS(topic = true, timeToLive = 5000L)
	default void ping() {}

	@JMS(topic=true)
	default void restart() {}

	@JMS
	default void reportServers() {}

	@JMS(topic = true)
	default void reportProvides() {}

	@JMS
	default void checkProvidedEndpoints(ClusterDef clusterDef) {}
}
