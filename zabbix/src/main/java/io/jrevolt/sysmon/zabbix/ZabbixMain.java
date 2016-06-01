package io.jrevolt.sysmon.zabbix;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.SpringBootApp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import com.zabbix4j.ZabbixApi;
import com.zabbix4j.ZabbixApiException;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.jrevolt.sysmon")
@EnableConfigurationProperties(DomainDef.class)
public class ZabbixMain {

	static Logger LOG = LoggerFactory.getLogger(ZabbixMain.class);


	static public void main(String[] args) {
		((LoggerContext) LoggerFactory.getILoggerFactory()).reset();
		SpringBootApp.run(ZabbixMain.class, args).getBean(ZabbixConfigurator.class).configure();
	}

	@Bean
	ZabbixApi zabbixApi(ZabbixCfg cfg) {
		if (cfg.getProxy() != null) {
			String[] proxy = cfg.getProxy().split(":");
			System.setProperty("http.proxyHost", proxy[0]);
			System.setProperty("http.proxyPort", proxy[1]);
			System.setProperty("https.proxyHost", proxy[0]);
			System.setProperty("https.proxyPort", proxy[1]);
		}
		ZabbixApi api = new ZabbixApi(cfg.getUrl().toString());
		try {
			api.login(cfg.getUser(), cfg.getPassword());
			LOG.debug("Successfully logged into Zabbix {}", cfg.getUrl());
		} catch (ZabbixApiException e) {
			LOG.error("Failed to login into Zabbix", e);
		}
		return api;
	}

}
