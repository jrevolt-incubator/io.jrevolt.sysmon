package org.jrevolt.sysmon.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class JmsSenderProxy implements InvocationHandler {

	@Autowired
	ConnectionFactory cf;

	@Autowired
	JmsCfg jmscfg;

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		String destination = jmscfg.getDestinationName(method);
		JMS jms = method.getAnnotation(JMS.class);
		JmsTemplate template = new JmsTemplate(cf);
		template.setPubSubDomain(jms.topic());
		template.setExplicitQosEnabled(true);
		template.setTimeToLive(template.getTimeToLive());
		template.setMessageConverter(jmscfg.getMessageConverter());
		template.send(destination, session -> jmscfg.getMessageConverter().toMessage(args, session));
		return null;
	}
}
