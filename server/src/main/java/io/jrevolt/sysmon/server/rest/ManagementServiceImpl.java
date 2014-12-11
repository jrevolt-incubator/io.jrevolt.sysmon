package io.jrevolt.sysmon.server.rest;

import io.jrevolt.sysmon.jms.ServerEvents;
import io.jrevolt.sysmon.rest.ManagementService;
import io.jrevolt.sysmon.server.Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
@Path("/management")
public class ManagementServiceImpl implements ManagementService {

	@Autowired
	ServerEvents events;

	@Autowired
	Database db;


	@Override
	public List<ClusterAccess> getClusterAccess(String cluster) {
		return db.getClusters().stream()
				.map(c->new ClusterAccess(c.getClusterName(), c.getServers(), c.isAccessAllowed()))
				.collect(Collectors.toList());
	}
}
