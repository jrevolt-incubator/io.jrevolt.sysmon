package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ClusterDef {

	private String name;
	private List<String> servers = new LinkedList<>();
	private List<EndpointDef> provides = new LinkedList<>();
	private List<EndpointDef> dependencies = new LinkedList<>();
	private List<ArtifactDef> artifacts = new LinkedList<>();

	///

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getServers() {
		return servers;
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}

	public List<EndpointDef> getProvides() {
		return provides;
	}

	public void setProvides(List<EndpointDef> provides) {
		this.provides = provides;
	}

	public List<EndpointDef> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<EndpointDef> dependencies) {
		this.dependencies = dependencies;
	}

	public List<ArtifactDef> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<ArtifactDef> artifacts) {
		this.artifacts = artifacts;
	}

///

	void init(DomainDef domain) {
		Pattern p = Pattern.compile(".*\\."+domain.getName());

		// expand list of servers; filter all but current domain
		setServers(getServers().stream()
							  .map(this::expand)
							  .flatMap(l -> l.stream())
							  .filter(s -> p.matcher(s).matches())
							  .distinct()
							  .collect(Collectors.toList()));

		// expand list of provided endpoints, update hostname
		List<EndpointDef> provides = getProvides().stream()
				.map(e -> getServers().stream()
						.map(s -> new EndpointDef(e, getName(), s, s))
						.collect(Collectors.toList()))
				.flatMap(l -> l.stream())
				.distinct()
				.collect(Collectors.toList());
		setProvides(provides);

		// set endpoint type (implicit in configuration)
		getProvides().stream().filter(e -> {
			e.setType(EndpointType.PROVIDED);
			return true;
		});
		getDependencies().stream().filter(e -> {
			e.setType(EndpointType.DEPENDENCY);
			return true;
		});
	}

	List<String> expand(String s) {
		List<String> result = new LinkedList<>();
		Pattern p = Pattern.compile("(.*)\\[(\\p{Digit}+)\\](.*)");
		Matcher m = p.matcher(s);
		if (!m.matches()) {
			result.add(s);
			return result;
		}

		String prefix = m.group(1);
		char[] sequence = m.group(2).toCharArray();
		String suffix = m.group(3);

		for (char c : sequence) {
			result.add(String.format("%s%s%s", prefix, c, suffix));
		}

		return result;
	}

	///

	public void update(ClusterDef cluster) {
		servers.clear();
		servers.addAll(cluster.servers);
		provides.clear();
		provides.addAll(cluster.provides);
		dependencies.clear();
		dependencies.addAll(cluster.dependencies);
		artifacts.clear();
		artifacts.addAll(cluster.artifacts);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.toString();
	}
}
