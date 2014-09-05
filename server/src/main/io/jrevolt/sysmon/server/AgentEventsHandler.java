package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.model.NodeDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class AgentEventsHandler implements AgentEvents {

	@Autowired
	Database db;

	@Override
	public void status(NodeDef node) {
//		throw new UnsupportedOperationException(); // todo implement this
	}

	@Override
	public void provides(List<URI> uris) {
//		throw new UnsupportedOperationException(); // todo implement this
	}
}
