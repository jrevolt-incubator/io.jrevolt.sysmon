package io.jrevolt.sysmon.server.rest;

import io.jrevolt.sysmon.common.VersionInfo;
import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.StatusInfo;
import io.jrevolt.sysmon.rest.RestService;
import io.jrevolt.sysmon.model.AppCfg;
import io.jrevolt.sysmon.server.Server;
import io.jrevolt.sysmon.server.ServerCfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.concurrent.ForkJoinPool;

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
	ServerEvents events;

	@Override
	public StatusInfo status() {
		VersionInfo version = VersionInfo.forClass(Server.class);
		return new StatusInfo(
				app.getName(), version.getArtifactUri(), version.getArtifactVersion(), version.getTimestamp());
	}

	@Override
	public Response restart() {
		events.restart();
		ForkJoinPool.commonPool().submit(() -> {
			LOG.info("Exiting on request. Setting error code to 7, service wrapper should restart us");
			System.exit(7);
		});
		return Response.accepted().build();
	}

	@Override
	public DomainDef getDomainDef() {
		return domainDef;
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
}
