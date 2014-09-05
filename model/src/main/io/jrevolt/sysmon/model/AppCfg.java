package io.jrevolt.sysmon.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
@ConfigurationProperties("spring.application")
public class AppCfg {

	String id;
	String name;
	String pid;
	UUID instance = UUID.randomUUID();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public UUID getInstance() {
		return instance;
	}

	public void setInstance(UUID instance) {
		this.instance = instance;
	}

	@PostConstruct
	void init() {
		System.out.println();
	}

}
