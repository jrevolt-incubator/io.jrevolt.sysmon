package io.jrevolt.sysmon.model;

import java.net.InetAddress;
import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ProxyDef {

	static public enum Type { INTERNAL, INTRANET, PUBLIC }

	private String name;
	private InetAddress address;
	private List<RoutingDef> routing;

}
