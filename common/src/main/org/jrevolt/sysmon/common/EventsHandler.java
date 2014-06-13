package org.jrevolt.sysmon.common;

import org.jrevolt.sysmon.core.SpringApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
//@Component
public class EventsHandler {

	Class type;

	@Autowired
	AutowireCapableBeanFactory factory;

	@Autowired
	ConnectionFactory cf;

	@Autowired
	SpringApp app;

	@Autowired
	JmsCfg jmscfg;

	Map<String, AbstractMessageListenerContainer> containers = new HashMap<>();

	public EventsHandler(Class type) {
		this.type = type;
	}

	@PostConstruct
	void init() {
		Object handler = factory.getBean(type);

		for (Method method : type.getDeclaredMethods()) {
			String name = String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
			JMS jms = method.getAnnotation(JMS.class);
			DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
			container.setClientId(jmscfg.getListenerId(name));
			container.setConnectionFactory(cf);
			container.setMaxConcurrentConsumers(1);
			container.setDestinationName(name);
			container.setPubSubDomain(jms.topic());
//			container.setConnectLazily(true);
			container.setMessageConverter(jmscfg.getMessageConverter());
			container.setMessageListener((MessageListener) message -> {
				try {
					Object[] args = (Object[]) jmscfg.getMessageConverter().fromMessage(message);
					method.invoke(handler, args);
				} catch (Exception e) {
					throw new UnsupportedOperationException(e);
				}
			});
			factory.initializeBean(container, name);
			containers.put(name, container);
		}
	}

	@PostConstruct
	void start() {
		for (AbstractMessageListenerContainer container : containers.values()) {
			try {
				container.start();
			} catch (JmsException e) {
				e.printStackTrace();
			}
		}
	}

}
