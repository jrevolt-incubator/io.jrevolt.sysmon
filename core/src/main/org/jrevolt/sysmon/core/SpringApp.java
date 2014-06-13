package org.jrevolt.sysmon.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("spring.application")
public class SpringApp {

	String id;
	String name;
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

	public UUID getInstance() {
		return instance;
	}

	public void setInstance(UUID instance) {
		this.instance = instance;
	}
}
