package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.loader.mvn.MvnArtifact;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ClusterDef {

	String name;

	List<URI> proxies = new LinkedList<>();
	List<String> artifacts = new LinkedList<>();
	List<String> servers = new LinkedList<>();
	List<URI> provides = new LinkedList<>();
	List<URI> dependencies = new LinkedList<>();
	List<URI> management = new LinkedList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<String> artifacts) {
		this.artifacts = artifacts;
	}

	public List<URI> getProxies() {
		return proxies;
	}

	public void setProxies(List<URI> proxies) {
		this.proxies = proxies;
	}

	public List<URI> getProvides() {
		return provides;
	}

	public void setProvides(List<URI> provides) {
		this.provides = provides;
	}

	public List<String> getServers() {
		return servers;
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}

	public List<URI> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<URI> dependencies) {
		this.dependencies = dependencies;
	}

	public List<URI> getManagement() {
		return management;
	}

	public void setManagement(List<URI> management) {
		this.management = management;
	}

	///

	void init(DomainDef domain) {
		setServers(getServers().stream()
							  .map(s -> String.format("%s.%s", s, domain.getName()))
							  .collect(Collectors.toList()));
	}
}
