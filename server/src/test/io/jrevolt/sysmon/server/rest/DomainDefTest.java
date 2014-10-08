package io.jrevolt.sysmon.server.rest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.server.Server;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Server.class)
@WebAppConfiguration
@ActiveProfiles("unittest")
public class DomainDefTest {

	@Autowired
	DomainDef domain;

	@Test
	public void test() throws Exception {
		Assert.assertEquals("example.com", domain.getName());
		Assert.assertTrue(domain.getClusters().containsKey("sample"));
	}


}
