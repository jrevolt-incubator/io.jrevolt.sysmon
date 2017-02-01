package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ModelTest.App.class)
@ActiveProfiles("unittest")
public class ModelTest {

	@SpringBootApplication
	@Configuration
	@EnableAutoConfiguration
	@ComponentScan("io.jrevolt.sysmon")
	@EnableConfigurationProperties(DomainDef.class)
	static public class App {}

	@Autowired
	DomainDef domain;

	@Test
	public void test() throws Exception {
		System.out.println("HEY!");
	}


}
