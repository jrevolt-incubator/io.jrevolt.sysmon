package org.jrevolt.sysmon.common;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.annotation.PostConstruct;
import javax.jms.MessageListener;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
//@Component
public class JmsReceiver {

	static private final Logger LOG = LoggerFactory.getLogger(JmsReceiver.class);

	Class type;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	ConfigurableListableBeanFactory factory;

	@Autowired
	JmsCfg jmscfg;

//	Map<String, DefaultMessageListenerContainer> containers = new HashMap<>();

	public JmsReceiver(Class type) {
		this.type = type;
	}

	static public class JmsListener extends DefaultMessageListenerContainer {
		public JmsListener(String name, Object handler, Method method, JmsCfg jmscfg) {
			JMS jms = method.getAnnotation(JMS.class);
			setClientId(jmscfg.getListenerId(name));
			setConnectionFactory(jmscfg.connectionFactory);
			setMaxConcurrentConsumers(1);
			setDestinationName(name);
			setPubSubDomain(jms.topic());
//			setConnectLazily(true);
			setMessageConverter(jmscfg.getMessageConverter());
			setMessageListener((MessageListener) message -> {
				try {
					Object[] args = (Object[]) jmscfg.getMessageConverter().fromMessage(message);
					LOG.debug("Received message. Invoking handler: {} {}", jmscfg.getDestinationName(method),
								 ToStringBuilder.reflectionToString(args));
					method.invoke(handler, args);
				} catch (Exception e) {
					throw new UnsupportedOperationException(e);
				}
			});
			setAutoStartup(true);
		}
	}


	@PostConstruct
	void init() {
		Object handler = ctx.getBean(type);

		for (Method method : type.getDeclaredMethods()) {
			String name = String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
			GenericBeanDefinition def = new GenericBeanDefinition();
			def.setBeanClass(JmsListener.class);
			def.setConstructorArgumentValues(new ConstructorArgumentValues() {{
				addGenericArgumentValue(name);
				addGenericArgumentValue(handler);
				addGenericArgumentValue(method);
				addGenericArgumentValue(jmscfg);
			}});
			((BeanDefinitionRegistry) factory).registerBeanDefinition(name, def);
			ctx.getBean(name);
			System.out.printf("%s : created bean %s%n", ctx, name);
		}
	}

//	@PreDestroy
//	void close() {
//		for (DefaultMessageListenerContainer container : containers.values()) {
//			container.stop();
//			container.destroy();
//		}
//	}

}
