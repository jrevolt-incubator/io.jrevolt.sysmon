package io.jrevolt.sysmon.model;

import io.jrevolt.launcher.vault.VaultConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.env.EnumerableCompositePropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class SpringBootApp extends SpringApplication {

	static {
		loadSystemProperties("system.properties");
	}

	static private SpringBootApp INSTANCE;

	static public ConfigurableApplicationContext run(Object source, String... args) {
		return new SpringApplicationBuilder()
				.sources(source)
				.environment(VaultConfiguration.initStandardEnvironment())
				.main(SpringBootApp.class)
				.run(args);
//		return new SpringBootApp(source).run(args);
	}

	static public SpringBootApp instance() {
		return INSTANCE;
	}

	static private void loadSystemProperties(String name) {
		File f = new File(name);
		if (!f.exists()) { return; }
		try (InputStream in = new FileInputStream(f)) {
			Properties props = new Properties();
			props.load(in);
			System.getProperties().putAll(props);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//

	ConfigurableApplicationContext applicationContext;

	{
		INSTANCE = this;
//		try {
//			String hostname = InetAddress.getLocalHost().getHostName();
//			System.setProperty("hostname", hostname);
//		} catch (UnknownHostException ignore) {
//		}
	}

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

			PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
			YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

			String pattern = "classpath*:spring.yaml";
			EnumerableCompositePropertySource composite = new EnumerableCompositePropertySource(pattern);
			Resource[] resources = resourceResolver.getResources(pattern);
			for (Resource r : resources) {
				composite.add(loader.load(r.getURI().toString(), r, null));
			}
			environment.getPropertySources().addLast(composite);

		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
