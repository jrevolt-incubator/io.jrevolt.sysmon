package org.jrevolt.sysmon.server.rest;

import org.jrevolt.sysmon.api.RestService;
import org.jrevolt.sysmon.core.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
