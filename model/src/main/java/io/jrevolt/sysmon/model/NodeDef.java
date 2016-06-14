package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class NodeDef {

	String hostname;

	public String getHostname() {
		return hostname;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
