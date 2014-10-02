package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.StatusInfo;
import io.jrevolt.sysmon.rest.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class RestServiceTestImpl implements RestService {

	@Autowired
	DomainDef domain;

	@Override
	public StatusInfo status() {
		throw new UnsupportedOperationException(); // todo implement this
	}

	@Override
	public Response restart() {
		throw new UnsupportedOperationException(); // todo implement this
	}

	@Override
	public DomainDef getDomainDef() {
		return domain;
	}

	@Override
	public Response resource(String resource) {
		throw new UnsupportedOperationException(); // todo implement this
	}

	@Override
	public void checkAll() {
		throw new UnsupportedOperationException(); // todo implement this
	}

	@Override
	public List<AgentInfo> getAgentInfo() {
		throw new UnsupportedOperationException(); // todo implement this
	}

	@Override
	public void ping(String server, AsyncResponse response) {
		throw new UnsupportedOperationException(); // todo implement this
	}
}
