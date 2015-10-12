package io.jrevolt.sysmon.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Path("/management") @Consumes("application/json") @Produces("application/json")
public interface ManagementService {

	static public class ClusterAccess {
		String clusterName;
		List<String> servers;
		boolean isAccessAllowed;

		public ClusterAccess(String clusterName, List<String> servers, boolean isAccessAllowed) {
			this.clusterName = clusterName;
			this.servers = servers;
			this.isAccessAllowed = isAccessAllowed;
		}

		public String getClusterName() {
			return clusterName;
		}

		public void setClusterName(String clusterName) {
			this.clusterName = clusterName;
		}

		public List<String> getServers() {
			return servers;
		}

		public void setServers(List<String> servers) {
			this.servers = servers;
		}

		public boolean isAccessAllowed() {
			return isAccessAllowed;
		}

		public void setAccessAllowed(boolean isAccessAllowed) {
			this.isAccessAllowed = isAccessAllowed;
		}
	}

	@GET
	@Path("cluster/access")
	default List<ClusterAccess> getClusterAccess (@PathParam("clusterName") String cluster) {
		throw new UnsupportedOperationException();
	}

	@GET
	@Path("cluster/access/{clusterName}")
	default boolean isClusterAccessAllowed(@PathParam("clusterName") @DefaultValue("") String cluster) {
		throw new UnsupportedOperationException();
	}

	@POST
	@Path("access/{clusterName}")
	default Response setAccessAllowed(@PathParam("clusterName") String clusterName, @QueryParam("allowed") boolean allowed) {
		throw new UnsupportedOperationException();
	}

}
