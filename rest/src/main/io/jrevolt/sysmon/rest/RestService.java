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
@Path("/") @Produces({"application/json", "application/xml"})
public interface RestService {

	@GET @Path("status")
	default StatusInfo status() { throw new UnsupportedOperationException(); }

	@GET @Path("restart/{cluster}/{server}")
	default Response restart(@PathParam("cluster") String cluster, @PathParam("server") String server) {
		throw new UnsupportedOperationException();
	}

	/// domain info ///

	@GET @Path("domain")
	default DomainDef getDomainDef() {
		throw new UnsupportedOperationException();
	}

	@GET @Path("servers")
	default List<String> getServers() { throw new UnsupportedOperationException(); }

	///

	@GET @Path("jnlp/{resource}")
	default Response resource(@PathParam("resource") String resource) {
		throw new UnsupportedOperationException();
	}

	@POST @Path("checkAll")
	default void checkAll() { throw new UnsupportedOperationException(); }


	/// agents ///

	@GET @Path("agents")
	default List<AgentInfo> getAgentInfo() { throw new UnsupportedOperationException(); }

	@GET @Path("ping/{server}")
	default void ping(@PathParam("server") String server, @Suspended AsyncResponse response) { throw new UnsupportedOperationException(); }

}
