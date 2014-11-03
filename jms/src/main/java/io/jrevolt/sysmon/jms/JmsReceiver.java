package io.jrevolt.sysmon.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.StringValueResolver;

import javax.annotation.PostConstruct;
import javax.jms.MessageListener;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

		public JmsListener(String name, Object handler, Method method, JmsCfg jmscfg, ConfigurableListableBeanFactory factory) {
			JMS jms = method.getAnnotation(JMS.class);
			JMSSelector selector = method.getAnnotation(JMSSelector.class);
			setClientId(jmscfg.getListenerId(name));
			setConnectionFactory(jmscfg.connectionFactory);
			setMaxConcurrentConsumers(1);
			setDestinationName(name);
			setPubSubDomain(jms.topic());
			if (selector != null) {
				setMessageSelector(factory.resolveEmbeddedValue(selector.value()));
			}
//			setConnectLazily(true);
			setMessageConverter(jmscfg.getMessageConverter());
			setMessageListener((MessageListener) message -> {
				JmsContext.withJmsContext(message, () -> {
					try {
						JmsMessage mymsg = (JmsMessage) jmscfg.getMessageConverter().fromMessage(message);
						if (LOG.isDebugEnabled()) {
							Map<String, Object> map = new HashMap<>();
							Parameter[] params = method.getParameters();
							for (int i=0; i<params.length; i++) {
								map.put(params[i].getName(), mymsg.getPayload()[i]);
							}
							LOG.debug("Received message. Invoking handler: {} {}", jmscfg.getDestinationName(method), map);
						}
						method.invoke(handler, mymsg.getPayload());
					} catch (Exception e) {
						throw new UnsupportedOperationException(e);
					}
				});
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
				addGenericArgumentValue(factory);
			}});
			((BeanDefinitionRegistry) factory).registerBeanDefinition(name, def);
			ctx.getBean(name);
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
