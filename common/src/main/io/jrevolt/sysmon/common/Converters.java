package io.jrevolt.sysmon.common;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class Converters implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor, ApplicationContextAware {

//	/**
//	 * Acts like a filter that decrypts string values in form {en:alias:encryptedValue}.
//	 * For now, encryption infrastructure is encapsulated in Utils class
//	 */
//	@TypeConverter
//	static public class DecryptingConverter implements Converter<String, String> {
//
//		SecureStoreManager store = new SecureStoreManager();
//
//		@Override
//		public String convert(String s) {
//			return (s != null) ? store.resolve(s) : null;
//		}
//	}
//
//	@TypeConverter
//	static public class FileConverter implements Converter<String,File> {
//		@Override
//		public File convert(String path) {
//			return StringUtils.isEmpty(path) ? null : new File(path);
//		}
//	}

	///

	private final String CONVERSION_SERVICE_NAME = "conversionService";

	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		registry.registerBeanDefinition(CONVERSION_SERVICE_NAME, BeanDefinitionBuilder.rootBeanDefinition(ConversionServiceFactoryBean.class).getBeanDefinition());
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (CONVERSION_SERVICE_NAME.equals(beanName)) {
			Map<String, Converter> beansOfType = appCtx.getBeansOfType(Converter.class);
			ConversionServiceFactoryBean conversionfactoryBean = (ConversionServiceFactoryBean) bean;
			Set converters = new HashSet<>(beansOfType.values());
			conversionfactoryBean.setConverters(converters);
		}
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	ApplicationContext appCtx;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appCtx = applicationContext;
	}

}
