package io.jrevolt.sysmon.model;

import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Monitoring extends DomainObject {

	private List<String> groups = new LinkedList<>();
	private List<MonitoringItem> items = new LinkedList<>();
	private List<MonitoringTemplate> templates = new LinkedList<>();
	private String proxy;

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List<MonitoringItem> getItems() {
		return items;
	}

	public void setItems(List<MonitoringItem> items) {
		this.items = items;
	}

	public List<MonitoringTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<MonitoringTemplate> templates) {
		this.templates = templates;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	///

	void init() {
		templates.forEach(MonitoringTemplate::init);
	}
}
