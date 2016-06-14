package io.jrevolt.sysmon.common;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Utils {

	static public <T> T get(T[] array, int index) {
		return array != null && array.length > index ? array[index] : null;
	}

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
		if (uri.getHost() != null) { return uri.getHost(); }

		return address(uri).getHost();
	}

	static public int resolvePort(URI uri) {
		int port = uri.getPort();
		if (port != -1) { return port; }

		if (uri.getHost() != null) {
			port = uri.getScheme().equals("http") ? 80
					: uri.getScheme().equals("https") ? 443
					: 0;
			return port;
		}

		return address(uri).getPort();
	}

	static public URI address(URI uri) {
		if (uri.getHost() != null) { return uri; }

		URI tmp = URI.create("tcp://"+uri.toASCIIString().replaceFirst(
				"^(?:\\p{Alnum}+:)+//(?:[^@]*@)?([^:]+)(?::(\\p{Digit}+))?.*",
				"$1:$2"));
		return tmp;
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
		guarded(r).run();
	}

	static public Runnable guarded(Runnable r) {
		return () -> {
			try {
				r.run();
			} catch (Exception e) {
				e.printStackTrace();
				Log.debug(r, e.toString());
			}
		};
	}

	static public <T> T with(T t, Consumer<T> consumer) {
		if (t != null) { consumer.accept(t); }
		return t;
	}

	static public void doif(boolean condition, Runnable action) {
		if (condition) { action.run(); }
	}

}
