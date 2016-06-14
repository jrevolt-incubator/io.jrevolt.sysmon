package io.jrevolt.sysmon.zabbix;

import io.jrevolt.sysmon.model.ProxyDef;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
	private Pattern resetFilter;

	private boolean skipUpdate;
	private boolean skipHosts;
	private boolean skipItems;
	private boolean skipTriggers;

	private String defaultMonitoringGroup;

	private Map<ProxyDef.Type, String> proxies = new HashMap<>();

	private String proxy;

	private ActionMessage actionMessage;

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

	public Pattern getResetFilter() {
		return resetFilter;
	}

	public void setResetFilter(Pattern resetFilter) {
		this.resetFilter = resetFilter;
	}

	public Map<ProxyDef.Type, String> getProxies() {
		return proxies;
	}

	public void setProxies(Map<ProxyDef.Type, String> proxies) {
		this.proxies = proxies;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public ActionMessage getActionMessage() {
		return actionMessage;
	}

	public void setActionMessage(ActionMessage actionMessage) {
		this.actionMessage = actionMessage;
	}

	static public class ActionMessage {
		private String subject;
		private String message;
		private String recoverySubject;
		private String recoveryMessage;

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getRecoverySubject() {
			return recoverySubject;
		}

		public void setRecoverySubject(String recoverySubject) {
			this.recoverySubject = recoverySubject;
		}

		public String getRecoveryMessage() {
			return recoveryMessage;
		}

		public void setRecoveryMessage(String recoveryMessage) {
			this.recoveryMessage = recoveryMessage;
		}
	}
}
