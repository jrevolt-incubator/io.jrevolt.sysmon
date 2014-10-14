package io.jrevolt.sysmon.model;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jdk.nashorn.internal.runtime.ScriptObject;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("domain")
public class DomainDef {

	private String name;
	private List<ClusterDef> clusters;

	///

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClusterDef> getClusters() {
		return clusters;
	}

	public void setClusters(List<ClusterDef> clusters) {
		this.clusters = clusters;
	}

	///

	@PostConstruct
	void init() {
		clusters.forEach(c -> c.init(this));
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.toString();
	}
}
