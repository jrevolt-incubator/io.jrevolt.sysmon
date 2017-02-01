package io.jrevolt.sysmon.model;

import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Monitoring extends DomainObject {

	private List<String> groups = new LinkedList<>();
	@Valid
	private List<MonitoringItem> items = new LinkedList<>();
	private List<MonitoringTemplate> templates = new LinkedList<>();

	private String proxy;

	private transient HostDef hostDef;

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

	public HostDef getHostDef() {
		return hostDef;
	}

	public void setHostDef(HostDef hostDef) {
		this.hostDef = hostDef;
	}

	///

	List<String> getTemplateNames() {
		return getTemplates().stream().map(MonitoringTemplate::getName).collect(Collectors.toList());
	}


	///

	void init(HostDef hostDef) {
		setHostDef(hostDef);
		templates.forEach(MonitoringTemplate::init);

		// unnamed items are silently removed (they were used as YAML templates, and are not needed anymore)
		getItems().removeIf(i -> isNull(i.getName()));

		if (nonNull(hostDef)) getItems().forEach(i->i.init(null, hostDef));
	}
}
