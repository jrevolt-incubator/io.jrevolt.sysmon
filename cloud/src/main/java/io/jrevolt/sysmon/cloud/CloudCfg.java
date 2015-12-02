package io.jrevolt.sysmon.cloud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
@ConfigurationProperties("io.jrevolt.sysmon.cloud")
public class CloudCfg {

	private URI baseUrl;
	private String apiKey;
	private String secretKey;
	private Map<String,String> tagFilter = new LinkedHashMap<>();
	private boolean useCache;

	public URI getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(URI baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Map<String, String> getTagFilter() {
		return tagFilter;
	}

	public void setTagFilter(Map<String, String> tagFilter) {
		this.tagFilter = tagFilter;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
}
