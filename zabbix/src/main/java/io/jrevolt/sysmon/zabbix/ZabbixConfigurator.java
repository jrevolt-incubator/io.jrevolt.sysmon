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
import com.zabbix4j.hostgroup.HostgroupDeleteRequest;
import com.zabbix4j.hostgroup.HostgroupGetRequest;
import com.zabbix4j.item.ItemCreateRequest;
import com.zabbix4j.template.TemplateDeleteRequest;
import com.zabbix4j.template.TemplateGetRequest;
import com.zabbix4j.template.TemplateObject;
import com.zabbix4j.template.TemplateUpdateRequest;
import com.zabbix4j.trigger.TriggerCreateRequest;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

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

		pool.submit(() -> {
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
				configureUserGroups();
				configureActions();
			}
		}).join();

		benchmark.stop();

		LOG.info("Completed in {} ms.", benchmark.getLastTaskTimeMillis());
	}

	void reset() {
		if (!cfg.isReset()) { return; }

		if (isNull(cfg.getResetFilter())) {
			LOG.error("No resetFilter specified. Skipping reset.");
			return;
		}

		zbx.api.host().get(new HostGetRequest()).getResult().parallelStream()
				.filter(h->cfg.getResetFilter().matcher(h.getName()).matches())
				.forEach(h->{
					LOG.info("Deleting host {}", h.getName());
					zbx.api.host().delete(with(new HostDeleteRequest(), r -> r.getParams().add(h.getHostid())));
				});

		zbx.api.template().get(new TemplateGetRequest()).getResult().stream()
				.filter(h->cfg.getResetFilter().matcher(h.getName()).matches())
				.forEach(h->{
					LOG.info("Deleting template {}", h.getName());
					zbx.api.template().delete(with(new TemplateDeleteRequest(), r->r.getParams().add(h.getTemplateid())));
				});

		zbx.api.hostgroup().get(new HostgroupGetRequest()).getResult().stream()
				.filter(h->cfg.getResetFilter().matcher(h.getName()).matches())
				.forEach(h->{
					LOG.info("Deleting host group {}", h.getName());
					zbx.api.hostgroup().delete(with(new HostgroupDeleteRequest(), r->r.getParams().add(h.getGroupid())));
				});
	}

	public void configureUserGroups() {
		domain.getMonitoring().getGroups().forEach(g-> zbx.getUserGroup(g, true));
	}

	public void configureActions() {
		domain.getMonitoring().getGroups().forEach(g-> zbx.getAction(g, true));
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
		domain.getMonitoring().getTemplates().parallelStream()
				.filter(t->!t.getName().matches("template-.*")) // filter out specific templates
				.forEach(t-> {
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
			zbx.getHost(s, GetMode.UPDATE);
		});
		domain.getProxies().parallelStream().forEach(p->{
			zbx.getHost(p, GetMode.UPDATE);
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

	void uncaught(Thread thread, Throwable thrown) {
		LOG.error("Uncaught exception", thrown);
	}

}
