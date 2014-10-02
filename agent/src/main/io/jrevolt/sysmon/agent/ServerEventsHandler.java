package io.jrevolt.sysmon.agent;

import io.jrevolt.sysmon.common.Version;
import io.jrevolt.sysmon.jms.JMSProperty;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.VersionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ForkJoinPool;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ServerEventsHandler implements ServerEvents {
	
	static private final Logger LOG = LoggerFactory.getLogger(ServerEventsHandler.class);

	@Autowired
	AgentCfg cfg;
	
	@Autowired
	AgentEvents events;

	@Override
	public void ping(@JMSProperty String cluster, @JMSProperty String server) {
		events.status(createAgentInfo());
	}

	@Override
	public void restart(@JMSProperty String cluster, @JMSProperty String server) {
		events.restarting(createAgentInfo());
		ForkJoinPool.commonPool().submit(() -> {
			LOG.info("Exiting on request. Setting error code to 7, service wrapper should restart us");
			System.exit(7);
		});
	}

	@Override
	public void checkCluster(@JMSProperty String name, ClusterDef clusterDef) {
//		try {
//			events.serverChecked(clusterDef.getName(), InetAddress.getLocalHost().getHostName());
//		} catch (Exception e) {
//			throw new UnsupportedOperationException(e);
//		}
	}

	private AgentInfo createAgentInfo() {
		return new AgentInfo(
				cfg.getClusterName(), cfg.getServerName(), AgentInfo.Status.ONLINE,
				new VersionInfo(Version.getVersion(Agent.class)),
				Instant.now()
		);
	}
}
