package io.jrevolt.sysmon.agent;

import io.jrevolt.sysmon.core.SpringBootApp;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class AgentMain {
	static public void main(String[] args) {
		SpringBootApp.run(Agent.class, args);
	}
}
