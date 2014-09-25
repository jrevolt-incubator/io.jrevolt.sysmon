package io.jrevolt.sysmon.rest;

import io.jrevolt.sysmon.model.DomainDef;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Path("/") @Produces("application/json")
public interface RestService {

	@GET @Path("version")
	String version();

	@GET @Path("domain")
	DomainDef getDomainDef();

}
