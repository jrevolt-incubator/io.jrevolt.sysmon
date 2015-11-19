package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.Utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.jrevolt.sysmon.common.Utils.with;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@ConfigurationProperties("domain")
public class DomainDef {

	private String name;
	private List<ClusterDef> clusters = new LinkedList<>();
	private Monitoring monitoring;

	///

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClusterDef> getClusters() {
		return clusters;
	}

	public void setClusters(List<ClusterDef> clusters) {
		this.clusters = clusters;
	}

	public Monitoring getMonitoring() {
		return monitoring;
	}

	public void setMonitoring(Monitoring monitoring) {
		this.monitoring = monitoring;
	}

	///

	@PostConstruct
	void init() {

		// QDH filter
		clusters.removeIf(c -> !c.getClusterName().matches(".*soa.*"));

		// replicate template hierarchy into host groups (a template is also a group)
		Utils.with(monitoring, m->{
			m.init();
			m.getTemplates().forEach(t -> {
				Set<String> deps = getAllTemplateDeps(t);
				t.getGroups().addAll(deps);
				deps.addAll(m.getGroups()); // normalize, get rid of duplicates (m.groups is a list)
				m.getGroups().clear();
				m.getGroups().addAll(deps);
			});
		});

		clusters.forEach(c -> c.init(this));

		try {
			new Yaml().dump(this, new FileWriter("c:/users/patrik/var/test.yaml"));
		} catch (IOException ignore) {
		}
	}

	Set<String> getAllTemplateDeps(MonitoringTemplate root) {
		Map<String, MonitoringTemplate> all = new LinkedHashMap<>();
		Map<String, MonitoringTemplate> deps = new LinkedHashMap<>();
		getMonitoring().getTemplates().forEach(t-> all.put(t.getName(), t));
		return getAllTemplateDeps(root, all, deps);
	}

	private Set<String> getAllTemplateDeps(MonitoringTemplate root, Map<String, MonitoringTemplate> all, Map<String, MonitoringTemplate> deps) {
		deps.put(root.getName(), root);
		root.getTemplates().stream()
				.map(s->requireNonNull(all.get(s)))
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
