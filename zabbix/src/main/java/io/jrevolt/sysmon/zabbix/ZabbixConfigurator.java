package io.jrevolt.sysmon.zabbix;

import io.jrevolt.launcher.mvn.Artifact;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.Monitoring;
import io.jrevolt.sysmon.model.MonitoringItem;
import io.jrevolt.sysmon.model.MonitoringTrigger;
import io.jrevolt.sysmon.model.ProxyDef;
import io.jrevolt.sysmon.model.ServerDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zabbix4j.host.HostDeleteRequest;
import com.zabbix4j.host.HostGetRequest;
import com.zabbix4j.host.HostGetResponse;
import com.zabbix4j.host.HostObject;
import com.zabbix4j.hostgroup.HostgroupDeleteRequest;
import com.zabbix4j.hostgroup.HostgroupGetRequest;
import com.zabbix4j.hostgroup.HostgroupGetResponse;
import com.zabbix4j.item.ItemCreateRequest;
import com.zabbix4j.template.TemplateDeleteRequest;
import com.zabbix4j.template.TemplateGetRequest;
import com.zabbix4j.template.TemplateGetResponse;
import com.zabbix4j.template.TemplateObject;
import com.zabbix4j.template.TemplateUpdateRequest;
import com.zabbix4j.trigger.TriggerCreateRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import static io.jrevolt.sysmon.common.Utils.address;
import static io.jrevolt.sysmon.common.Utils.with;
import static java.lang.String.format;
import static java.util.Objects.*;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
public class ZabbixConfigurator {

	static private Logger LOG = LoggerFactory.getLogger(ZabbixConfigurator.class);

	@Autowired
	DomainDef domain;

	@Autowired
	ZabbixService zbx;

	@Autowired
	ZabbixCfg cfg;

	public void configure() {

		StopWatch benchmark = new StopWatch();
		benchmark.start();

		LOG.info("Using {} threads and DNS server: {}", cfg.getThreads(), cfg.getDnsServer());

		domain.getClusters().removeIf(c -> !cfg.getClusterInclude().matcher(c.getClusterName()).matches());
		domain.getClusters().removeIf(c -> cfg.getClusterExclude().matcher(c.getClusterName()).matches());

		ForkJoinPool pool = new ForkJoinPool(cfg.getThreads());
		pool.submit(()->{
			if (cfg.isReset()) {
				reset();
			}
			if (cfg.isConfigure()) {
				configureProxies();
				configureHostGroups();
				configureTemplates();
				configureHosts();
				configureApplications();
				configureItems();
				configureTriggers();
			}
		}).join();

		benchmark.stop();

		LOG.info("Completed in {} ms.", benchmark.getLastTaskTimeMillis());
	}

	void reset() {
		if (!cfg.isReset()) { return; }

		HostGetResponse hosts = zbx.api.host().get(with(new HostGetRequest(), r -> {
			r.getParams().setOutput("extend");
			r.getParams().setFilter(new HostGetRequest.Filter());
			domain.getClusters().stream().flatMap(c -> c.getServers().stream()).map(ServerDef::getName).forEach(h -> {
				r.getParams().getFilter().addHost(h);
			});
		}));
		if (!hosts.getResult().isEmpty()) zbx.api.host().delete(with(new HostDeleteRequest(), r->{
			hosts.getResult().stream().forEach(h->{
				LOG.info("Deleting host {}", h.getName());
				r.getParams().add(h.getHostid());
			});
		}));

		TemplateGetResponse templates = zbx.api.template().get(with(new TemplateGetRequest(), r -> {
			r.getParams().setOutput("extend");
			r.getParams().setFilter(new TemplateGetRequest.Filter());
			domain.getMonitoring().getTemplates().forEach(t->{
				r.getParams().getFilter().addHost(t.getName());
			});
		}));
		if (!templates.getResult().isEmpty()) zbx.api.template().delete(with(new TemplateDeleteRequest(), r->{
			templates.getResult().stream().forEach(t->{
				LOG.info("Deleting cluster template {}", t.getName());
				r.addTemplateId(t.getTemplateid());
			});
		}));
		domain.getMonitoring().getTemplates().parallelStream().forEach(t->{
			TemplateObject found = zbx.getTemplate(t.getName());
			if (isNull(found)) { return; }
			LOG.info("Deleting template {}", t.getName());
			zbx.api.template().delete(with(new TemplateDeleteRequest(), r -> r.addTemplateId(found.getTemplateid())));
		});

		HostgroupGetResponse groups = zbx.api.hostgroup().get(with(new HostgroupGetRequest(), r -> {
			r.getParams().setOutput("extend");
			r.getParams().setFilter(new HostgroupGetRequest.Filter());
			domain.getMonitoring().getGroups().forEach(g-> r.getParams().getFilter().addName(g));
		}));
		if (!groups.getResult().isEmpty()) zbx.api.hostgroup().delete(with(new HostgroupDeleteRequest(), r->{
			groups.getResult().stream().forEach(g->{
				LOG.info("Deleting cluster host group {}", g.getName());
				r.getParams().add(g.getGroupid());
			});
		}));

	}

	public void configureProxies() {
		Set<String> names = new HashSet<>();
		domain.getProxies().stream()
				.map(ProxyDef::getMonitoring).filter(Objects::nonNull)
				.map(Monitoring::getProxy).filter(Objects::nonNull)
				.forEach(names::add);
		names.parallelStream().forEach(s->zbx.getProxy(s, true));
	}

	public void configureHostGroups() {
		domain.getMonitoring().getGroups().forEach(g->zbx.getHostGroup(g, true));
	}

	public void configureTemplates() {
		// preinitialize instances
		domain.getMonitoring().getTemplates().parallelStream().forEach(t-> zbx.getTemplate(t.getName(), true));
		// do update
		domain.getMonitoring().getTemplates().parallelStream().forEach(t-> {
			zbx.api.template().update(with(new TemplateUpdateRequest(), r -> {
				LOG.info("Updating template {}", t.getName());
				TemplateObject current = zbx.getTemplate(t.getName());
				r.getParams().setTemplateid(current.getTemplateid());
				t.getGroups().forEach(s-> with(zbx.getHostGroup(s), g->r.getParams().addGroupId(g.getGroupid())));
				t.getTemplates().forEach(s-> {
					with(zbx.getTemplate(s), pt->r.getParams().addTemplateId(pt.getTemplateid()));
				});
			}));
		});

		//domain.getClusters().stream().map(ClusterDef::getMonitoring).flatMap(m->m.getTemplates())

		if (false) domain.getClusters().parallelStream().forEach(c->{
			String name = format("cluster-%s", c.getClusterName());
			zbx.getTemplate(name, true);
			zbx.api.template().update(with(new TemplateUpdateRequest(), r -> {
				LOG.info("Updating template {}", name);
				r.getParams().setTemplateid(zbx.getTemplate(name).getTemplateid());
				r.getParams().addGroupId(zbx.getHostGroup(name).getGroupid());
				r.getParams().addGroupId(zbx.getHostGroup("Templates").getGroupid());
				if (nonNull(c.getMonitoring())) {
					c.getMonitoring().getTemplates().stream().map(t->zbx.getTemplate(t)).filter(Objects::nonNull).forEach(t->{
						r.getParams().addTemplateId(t.getTemplateid());
					});
				}
			}));
		});
	}

	public void configureHosts() {
		if (cfg.isSkipHosts()) {
			LOG.info("Skipping hosts configuration");
			return;
		}

		domain.getClusters().parallelStream().flatMap(c->c.getServers().stream()).forEach(s->{
			zbx.getHost(s, true);
		});
		domain.getProxies().parallelStream().forEach(p->{
			zbx.getHost(p, true);
		});
	}

	public void configureApplications() {
		//zbx.getApplication("appserver", true);
	}

	public void configureItems() {
		if (cfg.isSkipItems()) {
			LOG.info("Skipping items configuration");
			return;
		}

		domain.getMonitoring().getTemplates().parallelStream().forEach(t->{
			t.getItems().parallelStream().forEach(i-> zbx.getItem(i, true));
		});

		domain.getClusters().stream()
				.map(ClusterDef::getMonitoring).filter(Objects::nonNull)
				.flatMap(m->m.getItems().stream())
				.parallel().forEach(i-> zbx.getItem(i, true));

		domain.getProxies().stream().filter(p->nonNull(p.getMonitoring())).forEach(p -> {
			p.getMonitoring().getItems().parallelStream().forEach(i -> zbx.getItem(i, true));
		});

		if (false) domain.getClusters().parallelStream().filter(c->nonNull(c.getMonitoring())).forEach(c-> {
			String templateName = format("cluster-%s", c.getClusterName());
			TemplateObject template = zbx.getTemplate(templateName);
			c.getMonitoring().getItems().parallelStream().forEach(i -> {
				if (nonNull(zbx.getItem(i))) {
					LOG.info("Item \"{}\" exists in cluster template \"{}\"", i.getName(), templateName);
				} else {
					LOG.info("Creating item \"{}\" in cluster template \"{}\"", i.getName(), templateName);
					zbx.api.item().create(with(new ItemCreateRequest(), t -> {
						t.getParams().setName(i.getName());
						t.getParams().setKey_(i.getCommand());
						t.getParams().setHostid(template.getTemplateid());
						t.getParams().setType(0);
						t.getParams().setData_type(i.getDataType().ordinal());
						t.getParams().setValue_type(i.getValueType().ordinal());
						//t.getParams().setInterfaceid();
						//applications?
						t.getParams().setDelay(60);
					}));
				}
			});
		});
		if (false) domain.getClusters().parallelStream().forEach(c-> {
			String templateName = format("cluster-%s", c.getClusterName());
			TemplateObject template = zbx.getTemplate(templateName);
			c.getArtifacts().stream().forEach(a->{
				boolean isUriValid = a.getUri().getScheme().equals("mvn");
				Artifact artifact = isUriValid ? Artifact.tryparse(a.getUri().getSchemeSpecificPart()) : null;
				if (artifact == null) {
					LOG.warn("Skipping invalid artifact URI: {}", a.getUri());
					return;
				}
				String itemName = format("appserver: deployment: %s", artifact.getArtifactId());
				String itemKey = format("appserver.check[z_deployment %s]", artifact.getArtifactId());
				if (nonNull(zbx.getItem(null))) {
					LOG.info("Item \"{}\" exists in cluster template \"{}\"", itemName, templateName);
				} else {
					zbx.api.item().create(with(new ItemCreateRequest(), i->{
						i.getParams().setName(itemName);
						i.getParams().setKey_(itemKey);
						i.getParams().setHostid(template.getTemplateid());
						i.getParams().setType(0);
						i.getParams().setValue_type(1);
						i.getParams().setDelay(60);
					}));
				}
			});
		});
	}

	public void configureTriggers() {
		if (cfg.isSkipTriggers()) {
			LOG.info("Skipping triggers configuration");
			return;
		}

		domain.getMonitoring().getTemplates().stream()
				.flatMap(t->t.getItems().stream())
				.map(MonitoringItem::getTrigger).filter(Objects::nonNull)
				.parallel().forEach(t -> zbx.getTrigger(t, true));

		domain.getClusters().stream()
				.flatMap(c -> c.getServers().stream())
				.map(ServerDef::getMonitoring).filter(Objects::nonNull)
				.flatMap(m -> m.getItems().stream())
				.map(MonitoringItem::getTrigger).filter(Objects::nonNull)
				.parallel().forEach(t -> zbx.getTrigger(t, true));

		domain.getProxies().stream()
				.map(ProxyDef::getMonitoring).filter(Objects::nonNull)
				.flatMap(m -> m.getItems().stream())
				.map(MonitoringItem::getTrigger).filter(Objects::nonNull)
				.parallel().forEach(t -> zbx.getTrigger(t, true));


		if (false) domain.getClusters().parallelStream().forEach(c->{
			c.getArtifacts().parallelStream().forEach(a->{
				String templateName = format("cluster-%s", c.getClusterName());
				boolean isValid = a.getUri().getScheme().equals("mvn");
				Artifact artifact = isValid ? Artifact.tryparse(a.getUri().getSchemeSpecificPart()) : null;
				if (artifact == null) {
					LOG.warn("Invalid URI. Skipping trigger for artifact {}", a.getUri());
					return;
				}
				String name = format("appserver: unavailable deployment: %s", artifact.getArtifactId());
				if (nonNull(zbx.getTrigger(name, zbx.getTemplate(templateName)))) {
					LOG.info("Trigger \"{}\" exists in cluster template \"{}\"", name, templateName);
				} else {
					LOG.info("Creating trigger \"{}\"", name);
					zbx.api.trigger().create(with(new TriggerCreateRequest(), r->{
						r.getParams().setDescription(name);
						r.getParams().setExpression(format(
								"{%s:appserver.check[z_deployment %s].last()}<>1", templateName, artifact.getArtifactId()));
						r.getParams().setPriority(MonitoringTrigger.Severity.HIGH.ordinal());
					}));
				}
			});
		});
	}

}
