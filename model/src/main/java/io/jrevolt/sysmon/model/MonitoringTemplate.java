package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MonitoringTemplate extends DomainObject {

	private String name;
	private Set<String> groups = new LinkedHashSet<>();
	private List<String> templates = new LinkedList<>();
	private List<MonitoringItem> items = new LinkedList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

	public List<MonitoringItem> getItems() {
		return items;
	}

	public void setItems(List<MonitoringItem> items) {
		this.items = items;
	}

	void init() {
		items.forEach(i->i.init(this, null));
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.toString();
	}
}
