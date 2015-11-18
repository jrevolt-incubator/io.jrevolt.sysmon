package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MonitoringDef {

	private Set<String> groups = new LinkedHashSet<>();
	private List<String> templates = new LinkedList<>();
	private List<String> services = new LinkedList<>();
	private List<Integer> ports = new LinkedList<>();
	private List<MonitoringItem> items = new LinkedList<>();

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> template) {
		this.templates = template;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	public List<MonitoringItem> getItems() {
		return items;
	}

	public void setItems(List<MonitoringItem> items) {
		this.items = items;
	}

	///


	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("groups", groups)
				.append("template", templates)
				.toString();
	}
}
