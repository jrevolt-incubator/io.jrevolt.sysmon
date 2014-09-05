package io.jrevolt.sysmon.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Path("/")
public interface RestService {

	@GET @Path("version")
	String version();

}
