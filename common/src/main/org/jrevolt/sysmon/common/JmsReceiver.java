package org.jrevolt.sysmon.common;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jrevolt.sysmon.core.AppCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
//@Component
public class JmsReceiver {

	static private final Logger LOG = LoggerFactory.getLogger(JmsReceiver.class);

	Class type;

	@Autowired
	AutowireCapableBeanFactory factory;

	@Autowired
	ConnectionFactory cf;

	@Autowired
	AppCfg app;

	@Autowired
	JmsCfg jmscfg;

	Map<String, AbstractMessageListenerContainer> containers = new HashMap<>();

	public JmsReceiver(Class type) {
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
					LOG.debug("Received message. Invoking handler: {} {}", jmscfg.getDestinationName(method), ToStringBuilder.reflectionToString(args));
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
