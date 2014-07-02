package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.core.SpringBootApp;

import org.springframework.boot.SpringApplication;

import java.security.Security;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class ServerMain {
	public static void main(String[] args) throws Exception {
		SpringBootApp.run(Server.class, args);
	}
}
