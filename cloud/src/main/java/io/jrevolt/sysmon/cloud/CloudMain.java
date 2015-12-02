package io.jrevolt.sysmon.cloud;

import io.jrevolt.launcher.util.CommandLine;
import io.jrevolt.sysmon.cloud.model.QueryAsyncJobResultResponse;
import io.jrevolt.sysmon.model.SpringBootApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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

	public static void main(String[] args) {
		((LoggerContext) LoggerFactory.getILoggerFactory()).reset();
		ConfigurableApplicationContext ctx = SpringBootApp.run(CloudMain.class, args);
		ctx.getBean(CloudCommand.class).run();
		ctx.close();
	}

}
