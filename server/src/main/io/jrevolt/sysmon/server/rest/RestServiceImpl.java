package io.jrevolt.sysmon.server.rest;

import io.jrevolt.sysmon.common.Version;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.StatusInfo;
import io.jrevolt.sysmon.rest.RestService;
import io.jrevolt.sysmon.model.AppCfg;
import io.jrevolt.sysmon.server.Database;
import io.jrevolt.sysmon.server.Server;
import io.jrevolt.sysmon.server.ServerCfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.jrevolt.sysmon.server.Utils.async;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Service
@Path("/")
public class RestServiceImpl implements RestService {

	static private final Logger LOG = LoggerFactory.getLogger(RestServiceImpl.class);

	@Autowired
	AppCfg app;

	@Autowired
	ServerCfg cfg;

	@Autowired
	DomainDef domainDef;

	@Autowired
	Database db;

	@Autowired
	ServerEvents events;

	@Override
	public StatusInfo status() {
		Version version = Version.getVersion(Server.class);
		return new StatusInfo(
				app.getName(), version.getArtifactUri(), version.getArtifactVersion(), version.getTimestamp());
	}

	@Override
	public Response restart(String cluster, String server) {
		if ("all".equals(cluster)) { cluster = null; }
		if ("all".equals(server)) { server = null; }
		events.restart(cluster, server);
		if (cluster == null && server == null) {
			ForkJoinPool.commonPool().submit(() -> {
				LOG.info("Exiting on request. Setting error code to 7, service wrapper should restart us");
				System.exit(7);
			});
		}
		return Response.accepted().build();
	}

	@Override
	public DomainDef getDomainDef() {
		return domainDef;
	}

	@Override
	public List<String> getServers() {
		List<String> servers = domainDef.getClusters().values().stream()
				.flatMap(c -> c.getServers().stream())
				.collect(Collectors.toList());
		return servers;
	}

	@Override
	public Response resource(String resource) {
		String type
				= resource.endsWith(".html") ? "text/html"
				: resource.endsWith(".jnlp") ? "application/x-java-jnlp-file"
				: resource.endsWith(".jar") ? "application/java-archive"
				: null;
		return Response.ok(getClass().getResourceAsStream(resource))
				.type(type)
				.build();
	}

	@Override
	public void checkAll() {
		domainDef.getClusters().values().parallelStream().forEach(c->{
			events.checkCluster(c.getName(), c);
		});
	}

	@Override
	public List<AgentInfo> getAgentInfo() {
		return new ArrayList<>(db.getAgents().values());
	}

	@Override
	public AgentInfo ping(String server, int timeout, @Suspended AsyncResponse response) {
		async(() -> {
			Runnable action = () -> response.resume(db.getAgents().get(server));
			response.setTimeout(timeout, TimeUnit.SECONDS);
			response.setTimeoutHandler(r -> action.run());
			events.ping(null, server);
			db.onUpdate(server, action::run);
		});
		return null;
	}
}
