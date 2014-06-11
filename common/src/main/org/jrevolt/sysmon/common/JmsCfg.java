package org.jrevolt.sysmon.common;

import com.thoughtworks.xstream.XStream;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class JmsCfg {

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

	public String getDestinationName(Method method) {
		return String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
	}

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}
}
