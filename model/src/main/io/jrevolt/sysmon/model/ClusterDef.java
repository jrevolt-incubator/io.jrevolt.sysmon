package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.loader.mvn.MvnArtifact;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		Pattern p = Pattern.compile(".*\\."+domain.getName());
		setServers(getServers().stream()
							  .map(this::expand)
							  .flatMap(l -> l.stream())
							  .filter(s -> p.matcher(s).matches())
							  .distinct()
							  .collect(Collectors.toList()));
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
}
