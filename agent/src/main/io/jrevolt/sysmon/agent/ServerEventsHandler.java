package io.jrevolt.sysmon.agent;

import io.jrevolt.sysmon.jms.JMSProperty;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.NodeDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.ForkJoinPool;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ServerEventsHandler implements ServerEvents {

	@Autowired
	ConfigurableApplicationContext ctx;

	@Autowired
	AgentEvents events;

//	@Autowired
	DomainDef domain;

	@Override
	public void ping() {
		events.status(domain.currentNode());
	}

	@Override
	public void restart() {
		System.out.println("ServerEvents.restart()");
		// restart (special exit code handled by launcher script)
		ForkJoinPool.commonPool().submit(() -> {
			System.out.println("EXIT");
			System.exit(7);
		});
	}

	public void reportProvides() {
		NodeDef node = domain.currentNode();
		events.provides(node.getProvides());
	}

	@Override
	public void checkCluster(@JMSProperty String name, ClusterDef clusterDef) {
		try {
			events.serverChecked(clusterDef.getName(), InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
