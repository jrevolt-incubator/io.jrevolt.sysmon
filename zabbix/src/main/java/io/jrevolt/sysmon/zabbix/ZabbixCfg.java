package io.jrevolt.sysmon.zabbix;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
@ConfigurationProperties("io.jrevolt.sysmon.zabbix")
public class ZabbixCfg {

	private URL url;
	private String user;
	private String password;
	private String dnsServer;
	private boolean reset;
	private boolean configure;
	private int threads;
	private Pattern clusterInclude;
	private Pattern clusterExclude;

	private boolean skipUpdate;
	private boolean skipHosts;
	private boolean skipItems;
	private boolean skipTriggers;

	private String defaultMonitoringGroup;

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDnsServer() {
		return dnsServer;
	}

	public void setDnsServer(String dnsServer) {
		this.dnsServer = dnsServer;
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

	public boolean isConfigure() {
		return configure;
	}

	public void setConfigure(boolean configure) {
		this.configure = configure;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public Pattern getClusterInclude() {
		return clusterInclude;
	}

	public void setClusterInclude(Pattern clusterInclude) {
		this.clusterInclude = clusterInclude;
	}

	public Pattern getClusterExclude() {
		return clusterExclude;
	}

	public void setClusterExclude(Pattern clusterExclude) {
		this.clusterExclude = clusterExclude;
	}

	public boolean isSkipUpdate() {
		return skipUpdate;
	}

	public void setSkipUpdate(boolean skipUpdate) {
		this.skipUpdate = skipUpdate;
	}

	public boolean isSkipHosts() {
		return skipHosts;
	}

	public void setSkipHosts(boolean skipHosts) {
		this.skipHosts = skipHosts;
	}

	public boolean isSkipItems() {
		return skipItems;
	}

	public void setSkipItems(boolean skipItems) {
		this.skipItems = skipItems;
	}

	public boolean isSkipTriggers() {
		return skipTriggers;
	}

	public void setSkipTriggers(boolean skipTriggers) {
		this.skipTriggers = skipTriggers;
	}

	public String getDefaultMonitoringGroup() {
		return defaultMonitoringGroup;
	}

	public void setDefaultMonitoringGroup(String defaultMonitoringGroup) {
		this.defaultMonitoringGroup = defaultMonitoringGroup;
	}
}
