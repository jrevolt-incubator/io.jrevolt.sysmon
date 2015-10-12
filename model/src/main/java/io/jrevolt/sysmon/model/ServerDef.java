package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ServerDef {

	private String name;
	private String cluster;
	private List<EndpointDef> provides = new LinkedList<>();
	private List<EndpointDef> dependencies = new LinkedList<>();
	private List<ArtifactDef> artifacts = new LinkedList<>();
	private List<NetworkInfo> network = new LinkedList<>();
	private List<SSLInfo> ssl = new LinkedList<>();

	public ServerDef() {
	}

	public ServerDef(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
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

	public List<NetworkInfo> getNetwork() {
		return network;
	}

	public void setNetwork(List<NetworkInfo> network) {
		this.network = network;
	}

	public List<SSLInfo> getSsl() {
		return ssl;
	}

	public void setSsl(List<SSLInfo> ssl) {
		this.ssl = ssl;
	}

	///

	void init() {
		network.clear();
		network.addAll(getDependencies().stream()
				.map(d -> new NetworkInfo(d.getCluster(), d.getServer(), Utils.resolveHost(d.getUri()), Utils.resolvePort(d.getUri())))
				.distinct()
				.collect(Collectors.toList()));
	}

	public void update(ServerDef server) {
		provides.clear();
		provides.addAll(server.getProvides());
		dependencies.clear();
		dependencies.addAll(server.getDependencies());
		network.clear();
		network.addAll(server.getNetwork());
		ssl.clear();
		ssl.addAll(server.getSsl());
	}

	///


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ServerDef serverDef = (ServerDef) o;

		if (!cluster.equals(serverDef.cluster)) return false;
		if (!name.equals(serverDef.name)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + cluster.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ServerDef{");
		sb.append("name='").append(name).append('\'');
		sb.append(", cluster='").append(cluster).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
