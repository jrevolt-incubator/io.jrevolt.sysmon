package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ClusterDef {

	private String clusterName;
	private List<ServerDef> servers = new LinkedList<>();
	private List<EndpointDef> provides = new LinkedList<>();
	private List<EndpointDef> dependencies = new LinkedList<>();
	private List<ArtifactDef> artifacts = new LinkedList<>();
	private boolean isAccessAllowed;

	///

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public List<ServerDef> getServers() {
		return servers;
	}

	public void setServers(List<ServerDef> servers) {
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

	public boolean isAccessAllowed() {
		return isAccessAllowed;
	}

	public void setAccessAllowed(boolean isAccessAllowed) {
		this.isAccessAllowed = isAccessAllowed;
	}

	///

	public void setServerNames(List<String> servers) {
		this.servers.clear();
		this.servers.addAll(servers.stream()
				.map(this::expand).flatMap(Collection::stream)
				.map(ServerDef::new).collect(Collectors.toList()));

		// just the server list, all other attributes are empty -> relying on init()
	}

	public List<String> toServerNames() {
		return getServers().stream().map(ServerDef::getName).collect(Collectors.toList());
	}



	void init(DomainDef domain) {

		// fill in the implicit attributes
		getServers().forEach(s -> s.setCluster(getClusterName()));
		getProvides().forEach(e -> e.setType(EndpointType.PROVIDED));
		getDependencies().forEach(e -> e.setType(EndpointType.DEPENDENCY));

		// filter the server list: only domain members are accepted
		Pattern p = Pattern.compile(".*\\."+domain.getName());
		List<ServerDef> servers = getServers().stream()
				.filter(s -> p.matcher(s.getName()).matches())
				.distinct()
				.collect(Collectors.toList());
		getServers().clear();
		getServers().addAll(servers);


		// populate server provided endpoints based on cluster configured template
		// replace the server hostname template with actual server host name
		getServers().stream().forEach(s -> {
			s.getProvides().clear();
			s.getProvides().addAll(getProvides().stream()
					.map(e -> new EndpointDef(e, s.getCluster(), s.getName(), s.getName()))
					.collect(Collectors.toList()));
		});

		// populate server dependencies using cluster templates
		getServers().stream().forEach(s -> {
			s.getDependencies().clear();
			s.getDependencies().addAll(getDependencies().stream()
					.map(e -> new EndpointDef(e, s))
					.collect(Collectors.toList()));
		});

		// and finally, delegate init()
		getServers().forEach(ServerDef::init);

		// and clean initial configuration values that have been replicated into servers
		provides = null;
		dependencies = null;
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
				.append("name", clusterName)
				.toString();
	}
}
