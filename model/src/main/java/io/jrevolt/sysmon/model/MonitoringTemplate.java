package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MonitoringTemplate extends DomainObject {

	private String name;
	private Set<String> groups = new LinkedHashSet<>();
	private List<String> templates = new LinkedList<>();
	private List<MonitoringItem> items = new LinkedList<>();

	///


	public MonitoringTemplate() {
	}

	public MonitoringTemplate(String name) {
		this.name = name;
	}


	///

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

	///

	void init() {
		items.forEach(i->i.init(this, null));
	}

	public Set<String> getAllTemplateDeps(Monitoring monitoring) {
		Map<String, MonitoringTemplate> all = new LinkedHashMap<>();
		Map<String, MonitoringTemplate> deps = new LinkedHashMap<>();
		monitoring.getTemplates().forEach(t-> all.put(t.getName(), t));
		return getAllTemplateDeps(this, all, deps);
	}

	private Set<String> getAllTemplateDeps(MonitoringTemplate root, Map<String, MonitoringTemplate> all, Map<String, MonitoringTemplate> deps) {
		deps.put(root.getName(), root);
		root.getTemplates().stream()
				.map(s->requireNonNull(all.get(s), "No such template: "+s))
				.filter(candidate->!deps.containsKey(candidate.getName()))
				.forEach(d ->{
					deps.put(d.getName(), d);
					getAllTemplateDeps(d, all, deps);
				});
		return new HashSet<>(deps.keySet());
	}


	///

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.toString();
	}
}
