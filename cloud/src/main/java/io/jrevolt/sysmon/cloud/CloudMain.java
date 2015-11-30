package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ListTagsResponse;
import io.jrevolt.sysmon.cloud.model.Tag;
import io.jrevolt.sysmon.model.SpringBootApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.jrevolt.sysmon")
//@EnableConfigurationProperties(CloudCfg.class)
public class CloudMain {

	public static void main(String[] args) {
		((LoggerContext) LoggerFactory.getILoggerFactory()).reset();
		SpringBootApp.run(CloudMain.class, args).getBean(CloudMain.class).run();
	}

	@Autowired
	CloudApp app;

	@Autowired
	CloudApi api;

	void run() {
		System.out.println(api.listVirtualMachines("23c3f24b-767c-45a5-88af-97463c84195e"));
//		System.out.println(api.listVirtualMachines(new Tag("ENV", "DEV")));
	}

}
