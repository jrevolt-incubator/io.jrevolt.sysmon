package io.jrevolt.sysmon.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class JmsContext {

	static private ThreadLocal<JmsContext> tlJmsContext = new ThreadLocal<>();

	static public JmsContext jmsContext() {
		return tlJmsContext.get();
	}

	static public void withJmsContext(Message message, Runnable runnable) {
		try {
			tlJmsContext.set(new JmsContext(message));
			runnable.run();
		} finally {
			tlJmsContext.remove();
		}
	}

	Message message;

	InetAddress server;

	public JmsContext(Message message) {
		try {
			this.message = message;
			this.server = InetAddress.getByName(message.getStringProperty("server"));
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public Message getMessage() {
		return message;
	}

	public InetAddress getServer() {
		return server;
	}
}
