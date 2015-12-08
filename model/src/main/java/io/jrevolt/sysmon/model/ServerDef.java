package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ServerDef extends HostDef {

	private String cluster;
	private List<EndpointDef> provides = new LinkedList<>();
	private List<EndpointDef> dependencies = new LinkedList<>();
	private List<ArtifactDef> artifacts = new LinkedList<>();
	private List<NetworkInfo> network = new LinkedList<>();
	private List<SSLInfo> ssl = new LinkedList<>();

	private ClusterDef clusterDef;

	public ServerDef() {
	}

	public ServerDef(String name) {
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

	public ClusterDef getClusterDef() {
		return clusterDef;
	}

	public void setClusterDef(ClusterDef clusterDef) {
		this.clusterDef = clusterDef;
	}

	///

	@Override
	public Monitoring getMonitoring() {
		return clusterDef.getMonitoring();
	}


	///

	void init(ClusterDef clusterDef) {
		this.clusterDef = clusterDef;
		network.clear();
		network.addAll(getDependencies().stream()
				.map(d -> new NetworkInfo(d.getCluster(), d.getServer(), Utils.resolveHost(d.getUri()), Utils.resolvePort(d.getUri())))
				.distinct()
				.collect(Collectors.toList()));
		//Utils.with(getMonitoring(), m -> m.init(this));
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



}
