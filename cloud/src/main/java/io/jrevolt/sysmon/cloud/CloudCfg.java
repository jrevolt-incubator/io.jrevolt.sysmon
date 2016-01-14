package io.jrevolt.sysmon.cloud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
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

	static private final Logger LOG = LoggerFactory.getLogger(CloudCfg.class);

	private URI baseUrl;
	private String apiKey;
	transient
	private String secretKey;
	private Map<String,String> tagFilter = new LinkedHashMap<>();
	private boolean useCache;
	private boolean dryRun;
	private boolean skipStartWait;
	private int logMaxResponseLength = 1000;
	private boolean sortByHostName;
	private boolean sortByStartLevel;

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

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public boolean isSkipStartWait() {
		return skipStartWait;
	}

	public void setSkipStartWait(boolean skipStartWait) {
		this.skipStartWait = skipStartWait;
	}

	public int getLogMaxResponseLength() {
		return logMaxResponseLength;
	}

	public void setLogMaxResponseLength(int logMaxResponseLength) {
		this.logMaxResponseLength = logMaxResponseLength;
	}

	public boolean isSortByHostName() {
		return sortByHostName;
	}

	public void setSortByHostName(boolean sortByHostName) {
		this.sortByHostName = sortByHostName;
	}

	public boolean isSortByStartLevel() {
		return sortByStartLevel;
	}

	public void setSortByStartLevel(boolean sortByStartLevel) {
		this.sortByStartLevel = sortByStartLevel;
	}

	@PostConstruct
	void onInit() {
		LOG.info(ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE));
	}
}
