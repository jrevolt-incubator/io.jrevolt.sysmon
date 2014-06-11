package org.jrevolt.sysmon.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
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
		org.springframework.messaging.Message<Object[]> msg = MessageBuilder
				.withPayload(args != null ? args : new Object[0])
				.build();
		System.out.println("");
		template.convertAndSend(destination, msg);
		return null;
	}
}
