package io.jrevolt.sysmon.server.rest;

import io.jrevolt.sysmon.rest.RestService;
import io.jrevolt.sysmon.model.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Service
@Path("/")
public class RestServiceImpl implements RestService {

	@Autowired
	AppCfg app;

	@Override
	public String version() {
		return String.format("%s", app.getName());
	}
}
