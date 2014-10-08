package io.jrevolt.sysmon.server.rest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.server.Server;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DomainDefTest.App.class)
@WebAppConfiguration
@ActiveProfiles("unittest")
public class DomainDefTest {

	@Autowired
	DomainDef domain;

	@Test
	public void test() throws Exception {
		System.out.println();
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableSpringConfigured
	@EnableScheduling
	@ComponentScan("io.jrevolt.sysmon")
	@EnableConfigurationProperties(DomainDef.class)
	static public class App {}


}
