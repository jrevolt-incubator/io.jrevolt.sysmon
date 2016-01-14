package io.jrevolt.sysmon.cloud;

import io.jrevolt.launcher.util.CommandLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Configuration
public class CloudApp {

	static private final Logger LOG = LoggerFactory.getLogger(CloudApp.class);

	@Autowired
	ConfigurableApplicationContext ctx;

	@Bean
	public CloudApi cloudApi(CloudApiHandler handler) {
		return (CloudApi) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[]{CloudApi.class},
				handler
		);
	}

	@Bean
	public CommandLine commandLine(ApplicationArguments args) {
		return CommandLine.parse(new ArrayDeque<>(Arrays.asList(args.getSourceArgs())));
	}

	/// commands

	@Bean @ConditionalOnProperty(name="command", havingValue="startAll")
	public CloudCommand startAll(CloudService cloud, @Value("${level}") int level) {
		return () -> {
			LOG.info("startAll(level:{})", level);
			cloud.startAll(level);
		};
	}

	@Bean @ConditionalOnProperty(name="command", havingValue="stopAll")
	public CloudCommand stopAll(CloudService cloud, @Value("${level}") int level) {
		return () -> {
			LOG.info("stopAll(level:{})", level);
			cloud.stopAll(level);
		};
	}

	@Bean @ConditionalOnProperty(name="command", havingValue="rebootVM")
	public CloudCommand rebootVM(CloudService cloud, @Value("${id}") String id) {
		return () -> {
			LOG.info("rebootVM(id:{})", id);
			cloud.rebootVM(id);
		};
	}

	@Bean @ConditionalOnProperty(name="command", havingValue="listVMs")
	public CloudCommand listVMs(CloudService cloud) {
		return () -> {
			LOG.info("listVMs()");
			cloud.listVMs();
		};
	}

	@Bean @ConditionalOnProperty(name="command", havingValue="listTags")
	public CloudCommand listTags(CloudService cloud) {
		return () -> {
			LOG.info("listTags()");
			cloud.listTags();
		};
	}

	@Bean @ConditionalOnMissingBean
	public CloudCommand help() {
		return () -> {
			System.out.println("--command=(startAll|stopAll) --level=<level>");
		};
	}

}
