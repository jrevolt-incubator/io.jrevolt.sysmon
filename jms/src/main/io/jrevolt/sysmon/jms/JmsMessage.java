package io.jrevolt.sysmon.jms;

import java.net.InetAddress;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class JmsMessage {

	InetAddress origin;

	Object[] payload;

	public JmsMessage(InetAddress origin, Object[] payload) {
		this.origin = origin;
		this.payload = payload;
	}

	public InetAddress getOrigin() {
		return origin;
	}

	public Object[] getPayload() {
		return payload;
	}
}
