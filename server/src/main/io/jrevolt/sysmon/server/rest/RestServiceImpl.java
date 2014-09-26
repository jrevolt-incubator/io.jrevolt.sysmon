package io.jrevolt.sysmon.server.rest;

import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.rest.RestService;
import io.jrevolt.sysmon.model.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Service
@Path("/")
public class RestServiceImpl implements RestService {

	@Autowired
	AppCfg app;

	@Autowired
	DomainDef domainDef;

	@Autowired
	ServerEvents events;

	@Override
	public String version() {
		return String.format("%s", app.getName());
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
