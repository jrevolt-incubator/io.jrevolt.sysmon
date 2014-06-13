package org.jrevolt.sysmon.common;

import com.thoughtworks.xstream.XStream;
import org.jrevolt.sysmon.core.SpringApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class JmsCfg {

	@Autowired
	SpringApp app;

	private MessageConverter messageConverter = new MessageConverter() {

		XStream xstream = new XStream();

		@Override
		public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
			return session.createTextMessage(xstream.toXML(object));
		}

		@Override
		public Object fromMessage(Message message) throws JMSException, MessageConversionException {
			return xstream.fromXML(((TextMessage) message).getText());
		}
	};

	public String getListenerId(String name) {
		try {
			String user = System.getProperty("user.name");
			String host = InetAddress.getLocalHost().getHostName();
			return String.format("%s@%s:%s:%s", user, host, name, app.getInstance());
		} catch (UnknownHostException never) {
			throw new AssertionError(never);
		}
	}

	public String getDestinationName(Method method) {
		return String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
	}

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}
}
