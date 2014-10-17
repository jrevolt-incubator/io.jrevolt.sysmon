package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.NodeDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
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
	public void started(AgentInfo info) {
		status(info);
	}

	@Override
	public void status(AgentInfo info) {
		db.updateAgent(info);
	}

	@Override
	public void restarting(AgentInfo info) {
		info.setStatus(AgentInfo.Status.RESTARTING);
		info.setLastUpdated(Instant.now());
		db.updateAgent(info);
	}

	@Override
	public void clusterStatus(ClusterDef cluster) {
		db.updateCluster(cluster);
	}


}
