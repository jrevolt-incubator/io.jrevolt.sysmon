package io.jrevolt.sysmon.browser;

import java.io.File;
import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("sysmon.browser")
public class BrowserConfig {

	URI serverUrl;

	File directory;

	public URI getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(URI serverUrl) {
		this.serverUrl = serverUrl;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
}
