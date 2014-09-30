package io.jrevolt.sysmon.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Utils {
	static public InetAddress getLocalHost() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
