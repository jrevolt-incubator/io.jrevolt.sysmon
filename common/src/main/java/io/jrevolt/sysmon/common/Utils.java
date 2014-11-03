package io.jrevolt.sysmon.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

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

	static public String getExceptionDesription(Throwable t) {
		StringBuilder sb = new StringBuilder();
		for (; t != null; t = t.getCause()) {
			if (sb.length() > 0) { sb.append("Caused by "); }
			sb.append(t.toString()).append("\n");
		}
		return sb.toString();
	}

	public static void runGuarded(Runnable r) {
		try {
			r.run();
		} catch (Exception e) {
			Log.debug(r, e.toString());
		}
	}

}
