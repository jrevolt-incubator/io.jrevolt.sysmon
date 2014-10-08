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

	@JMS(timeToLive = 10*60000L)
	default void status(AgentInfo info) {}

	@JMS(timeToLive = 15000L)
	default void restarting(AgentInfo info) {}
}
