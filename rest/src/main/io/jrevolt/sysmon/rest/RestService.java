package io.jrevolt.sysmon.rest;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.StatusInfo;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Path("/") @Produces("application/json")
public interface RestService {

	@GET @Path("status")
	StatusInfo status();

	@GET @Path("restart")
	Response restart();

	@GET @Path("domain")
	DomainDef getDomainDef();

	@GET @Path("jnlp/{resource}")
	Response resource(@PathParam("resource") String resource);

	@POST @Path("checkAll")
	void checkAll();

	/// agents ///

	@GET @Path("agents")
	List<AgentInfo> getAgentInfo();

	@GET @Path("ping/{server}")
	void ping(@PathParam("server") String server, @Suspended AsyncResponse response);

}
