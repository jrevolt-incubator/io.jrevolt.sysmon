package org.jrevolt.sysmon.common;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface AgentEvents {

	@JMS(timeToLive = 5000L)
	default void status(String server) {}

	@JMS
	default void applications() {}

	@JMS
	default void ports() {}
}
