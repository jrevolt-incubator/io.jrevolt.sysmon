package io.jrevolt.sysmon.agent;

import io.jrevolt.sysmon.jms.AgentEvents;
import io.jrevolt.sysmon.model.NodeDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.concurrent.ForkJoinPool;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class NodeScanner {

	@Autowired
	AgentEvents events;

	public void scan(NodeDef node) {
		node.getProvides().stream().map(node::fixup).forEach(u -> {
			ForkJoinPool.commonPool().submit(() -> {
				try {
					URLConnection con = u.toURL().openConnection();
					if (con instanceof HttpURLConnection) {
						http((HttpURLConnection) con);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
	}
	
	void http(HttpURLConnection con) {}

}
