package io.jrevolt.sysmon.common;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public interface ServerEvents {

	@JMS(topic = true, timeToLive = 5000)
	default void ping() {}

	@JMS(topic=true, timeToLive = 5000)
	default void restart() {}
}
