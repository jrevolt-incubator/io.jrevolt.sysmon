package io.jrevolt.sysmon.rest;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.NetworkInfo;
import io.jrevolt.sysmon.model.ServerDef;
import io.jrevolt.sysmon.model.StatusInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Path("/api") @Consumes("application/json") @Produces("application/json")
public interface ApiService {

	@GET @Path("status")
	default StatusInfo status() { throw new UnsupportedOperationException(); }

	@GET @Path("restart")
	default Response restart() {
		throw new UnsupportedOperationException();
	}

	@GET @Path("restartAgent/{cluster}/{server}")
	default Response restartAgent(@PathParam("cluster") String cluster, @PathParam("server") String server) {
		throw new UnsupportedOperationException();
	}

	@GET @Path("checkServer/{server}")
	default Response checkServer(@PathParam("server") String server) {
		throw new UnsupportedOperationException();
	}

	/// domain info ///

	@GET @Path("domain")
	default DomainDef getDomainDef() {
		throw new UnsupportedOperationException();
	}

	@GET @Path("cluster/{name}")
	default ClusterDef getClusterDef(@PathParam("name") String name) {
		throw new UnsupportedOperationException();
	}

	@GET @Path("server/{name}")
	default List<ServerDef> getServerDefs(@PathParam("name") String name) {
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
	default AgentInfo ping(@PathParam("server") String server, @QueryParam("timeout") int timeout, @Suspended AsyncResponse response) { throw new UnsupportedOperationException(); }

	/// UI

	@GET @Path("/network")
	default List<NetworkInfo> getNetworkInfo() { throw new UnsupportedOperationException(); }
}
