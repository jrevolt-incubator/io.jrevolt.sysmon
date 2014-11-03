package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.rest.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class ApiServiceTestImpl implements ApiService {

	@Autowired
	DomainDef domain;

	@Override
	public DomainDef getDomainDef() {
		return domain;
	}

}
