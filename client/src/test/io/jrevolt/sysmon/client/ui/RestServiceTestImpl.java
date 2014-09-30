package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.StatusInfo;
import io.jrevolt.sysmon.rest.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class RestServiceTestImpl implements RestService {

	@Autowired
	DomainDef domain;

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
}
