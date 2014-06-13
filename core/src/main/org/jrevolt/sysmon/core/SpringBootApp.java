package org.jrevolt.sysmon.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class SpringBootApp extends SpringApplication {

	static private SpringBootApp INSTANCE;

	static public ConfigurableApplicationContext run(Object source, String... args) {
		return new SpringBootApp(source).run(args);
	}

	static public SpringBootApp instance() {
		return INSTANCE;
	}

	//

	ConfigurableApplicationContext applicationContext;

	{ INSTANCE = this; }

	public SpringBootApp(Object... sources) {
		super(sources);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public <T> T autowire(T object) {
		getApplicationContext().getAutowireCapableBeanFactory().autowireBean(object);
		return object;
	}

	public <T> T lookup(Class<T> type) {
		Map<String, T> candidates = applicationContext.getBeansOfType(type);
		if (candidates.isEmpty()) { return null; }

		String key = candidates.keySet().iterator().next();
		return candidates.get(key);
	}


	@Override
	protected ConfigurableApplicationContext createApplicationContext() {
		return (this.applicationContext = super.createApplicationContext());
	}

	@Override
	protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
		try {
			super.configureEnvironment(environment, args);
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath*:spring.yaml");
			YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
			CompositePropertySource composite = new CompositePropertySource("classpath*:spring.yaml");
			for (Resource resource : resources) {
				composite.addPropertySource(loader.load(resource.getFilename(), resource, null));
			}
			environment.getPropertySources().addLast(composite);
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
