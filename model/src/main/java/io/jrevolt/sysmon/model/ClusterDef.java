package io.jrevolt.sysmon.model;

import io.jrevolt.launcher.mvn.Artifact;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.jrevolt.sysmon.common.Utils.with;
import static java.lang.String.format;
import static org.springframework.util.Assert.isTrue;

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

	private MonitoringDef monitoring = new MonitoringDef();

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

	public MonitoringDef getMonitoring() {
		return monitoring;
	}

	public void setMonitoring(MonitoringDef monitoring) {
		this.monitoring = monitoring;
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

		String groupName = format("cluster-%s", getClusterName());

		// register cluster host group
		with(domain.getMonitoring(), m -> m.getGroups().add(groupName));

		// define cluster template
		domain.getMonitoring().getTemplates().add(with(new MonitoringTemplate(), t->{
			t.setName(format("cluster-%s", getClusterName()));
			t.getTemplates().addAll(getMonitoring().getTemplates());
			t.getGroups().addAll(domain.getAllTemplateDeps(t));
			t.getGroups().addAll(getMonitoring().getGroups());
			t.getGroups().add("Templates");

			getMonitoring().getGroups().clear();
			getMonitoring().getGroups().addAll(t.getGroups());

			// reset cluster templates, replace all with this one
			getMonitoring().getTemplates().clear();
			getMonitoring().getTemplates().add(t.getName());


			// if "deployment" domain monitoring template is defined,
			// configure cluster monitoring item for every artifact in cluster artifact list
			domain.getMonitoring().getItems().stream().filter(i->i.getTag().equals("deployment")).distinct().forEach(i->{
				getArtifacts().forEach(a->{
					String artifactId = Artifact.parse(a.getUri().getSchemeSpecificPart()).getArtifactId();
					copyItem(artifactId, t, i);
				});
			});

			// if "endpoint" template is defined,
			// configure cluster monitoring item for every provided endpoint
			domain.getMonitoring().getItems().stream().filter(i->i.getTag().equals("endpoint")).distinct().forEach(i->{
				getArtifacts().forEach(a-> a.getProvides().forEach(e-> copyItem(e.getUri().toString(), t, i)));
			});
		}));

		// define cluster template
		if (false) domain.getMonitoring().getTemplates().add(with(new MonitoringTemplate(), t->{
			t.setName(format("cluster-%s", getClusterName()));
			t.getGroups().add("Templates");
			t.getGroups().add(t.getName());
//			t.getGroups().addAll(domain.getAllTemplateDeps(t));
			t.getTemplates().addAll(getMonitoring().getTemplates());
			with(getMonitoring(), m -> {
				t.getTemplates().addAll(m.getTemplates());
				t.getGroups().addAll(m.getTemplates());
			});

			// if "deployment" domain monitoring template is defined,
			// configure cluster monitoring item for every artifact in cluster artifact list
			domain.getMonitoring().getItems().stream().filter(i->i.getTag().equals("deployment")).distinct().forEach(i->{
				getArtifacts().stream().forEach(a->{
					String artifactId = Artifact.parse(a.getUri().getSchemeSpecificPart()).getArtifactId();
					t.getItems().add(with(new MonitoringItem(i), m->{
						m.setName(m.getName().replace("$name", artifactId));
						m.setCommand(m.getCommand().replace("$name", artifactId));
						m.init(t);
					}));
				});
			});

			getMonitoring().getGroups().addAll(domain.getAllTemplateDeps(t));
		}));


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

		// and finally, delegate to init()
		getServers().forEach(s->s.init(this));

		// and clean initial configuration values that have been replicated into servers
		provides = null;
		dependencies = null;
	}

	private void copyItem(String name, MonitoringTemplate dst, MonitoringItem src) {
		dst.getItems().add(with(new MonitoringItem(src), m->{
			m.setName(m.getName().replace("$name", name));
			m.setCommand(m.getCommand().replace("$name", name));
			m.init(dst);
		}));
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
			result.add(format("%s%s%s", prefix, c, suffix));
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
