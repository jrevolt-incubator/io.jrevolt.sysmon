package io.jrevolt.sysmon.jms;

import com.thoughtworks.xstream.XStream;
import io.jrevolt.sysmon.model.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
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
	ConnectionFactory connectionFactory;

	@Autowired
	AppCfg app;

	@Bean @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	static JmsTemplate jmsTemplate(ConnectionFactory cf, JmsCfg jmscfg) {
		JmsTemplate template = new JmsTemplate(cf);
		return template;
	}

	private MessageConverter messageConverter = new MessageConverter() {

		XStream xstream = new XStream();

		@Override
		public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
			try {
				JmsMessage msg = new JmsMessage(InetAddress.getLocalHost(), (Object[]) object);
				return session.createTextMessage(xstream.toXML(msg));
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Object fromMessage(Message message) throws JMSException, MessageConversionException {
			JmsMessage msg = (JmsMessage) xstream.fromXML(((TextMessage) message).getText());
//			JmsContext.jmsContext()
			return msg;
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
