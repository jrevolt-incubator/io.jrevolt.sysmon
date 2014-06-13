package org.jrevolt.sysmon.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("sysmon.client")
public class ClientConfig {

	URI serverUrl;

	public URI getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(URI serverUrl) {
		this.serverUrl = serverUrl;
	}
}
