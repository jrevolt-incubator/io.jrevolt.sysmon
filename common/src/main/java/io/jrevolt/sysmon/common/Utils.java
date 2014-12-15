package io.jrevolt.sysmon.common;

import java.net.InetAddress;
import java.net.URI;
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

	static public InetAddress getInetAddress(byte[] address) {
		try {
			return InetAddress.getByAddress(null, address);
		} catch (UnknownHostException never) {
			throw new AssertionError(never);
		}
	}

	static public String resolveHost(URI uri) {
		return (uri.getHost() != null)
				? uri.getHost()
				: resolveHost(URI.create(uri.getSchemeSpecificPart()));
	}

	static public int resolvePort(URI uri) {
		int port = uri.getPort();
		if (port != -1) { return port; }

		if (uri.getHost() == null) {
			return resolvePort(URI.create(uri.getSchemeSpecificPart()));
		}

		port = uri.getScheme().equals("http") ? 80
				: uri.getScheme().equals("https") ? 443
				: 0;
		return port;
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
