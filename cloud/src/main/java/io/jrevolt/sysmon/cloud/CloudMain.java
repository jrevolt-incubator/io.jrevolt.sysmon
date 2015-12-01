package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.ListTagsResponse;
import io.jrevolt.sysmon.cloud.model.QueryAsyncJobResultResponse;
import io.jrevolt.sysmon.cloud.model.Tag;
import io.jrevolt.sysmon.model.SpringBootApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableCaching
@ComponentScan("io.jrevolt.sysmon")
//@EnableConfigurationProperties(CloudCfg.class)
public class CloudMain {

	static private final Logger LOG = LoggerFactory.getLogger(CloudMain.class);

	public static void main(String[] args) {
		((LoggerContext) LoggerFactory.getILoggerFactory()).reset();
		ConfigurableApplicationContext ctx = SpringBootApp.run(CloudMain.class, args);
		ctx.getBean(CloudMain.class).run();
		ctx.close();
	}

	@Autowired
	CloudApp app;

	@Autowired
	CloudService cloud;

	@Autowired
	CloudApi api;

	void run() {

		cloud.startAll(100);

		if (true) return;

//		System.out.println(api.listVirtualMachines("23c3f24b-767c-45a5-88af-97463c84195e", null));
//		System.out.println(api.listVirtualMachines(null, "sd1egov61"));
		cloud.listVMs(null).stream()
				.map(vm->vm.getTag("ENV"))
				.filter(Objects::nonNull)
				.distinct()
				.forEach(System.out::println);

		cloud.listVMs("DEV").stream()
				.map(vm -> vm.getTag("fqdn"))
				.filter(s -> s != null && s.matches("si.*"))
				.forEach(System.out::println);

		Set<String> jobs = new CopyOnWriteArraySet<>();
		cloud.listVMs("DEV").stream()
				.map(CloudVM::new)
				.filter(vm -> vm.getHostname() != null && vm.getHostname().matches("sd1soa03"))
				.parallel().forEach(vm -> {
			LOG.debug("Starting VM {} (startLevel: {})", vm.getHostname(), vm.getStartLevel());
			jobs.add(api.startVirtualMachine(vm.getId()).getJobid());
		});

		while (!jobs.isEmpty()) {
			jobs.parallelStream().forEach(id->{
				QueryAsyncJobResultResponse result = api.queryAsyncJobResult(id);
				if (result.getJobstatus() == 1) {
					jobs.remove(id);
				}
			});
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}




//		System.out.println(api.listVirtualMachines(new Tag("ENV", "DEV")));
	}

}
