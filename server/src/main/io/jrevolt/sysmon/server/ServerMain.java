package io.jrevolt.sysmon.server;

import io.jrevolt.sysmon.model.SpringBootApp;

import org.springframework.boot.loader.util.UrlSupport;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class ServerMain {
	public static void main(String[] args) throws Exception {
		UrlSupport.registerUrlProtocolHandlers();
		SpringBootApp.run(Server.class, args);
	}
}
