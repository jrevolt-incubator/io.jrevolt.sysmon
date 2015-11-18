package io.jrevolt.sysmon.zabbix;

import io.jrevolt.sysmon.model.MonitoringItem;
import io.jrevolt.sysmon.model.MonitoringTemplate;
import io.jrevolt.sysmon.model.MonitoringTrigger;
import io.jrevolt.sysmon.model.ServerDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import com.zabbix4j.GetRequestCommonParams;
import com.zabbix4j.ZabbixApi;
import com.zabbix4j.application.ApplicationCreateRequest;
import com.zabbix4j.application.ApplicationGetRequest;
import com.zabbix4j.application.ApplicationGetResponse;
import com.zabbix4j.application.ApplicationObject;
import com.zabbix4j.configuration.ConfigurationExportRequest;
import com.zabbix4j.configuration.ConfigurationExportResponse;
import com.zabbix4j.configuration.Option;
import com.zabbix4j.host.HostCreateRequest;
import com.zabbix4j.host.HostGetRequest;
import com.zabbix4j.host.HostGetResponse;
import com.zabbix4j.host.HostObject;
import com.zabbix4j.hostgroup.HostgroupCreateRequest;
import com.zabbix4j.hostgroup.HostgroupGetRequest;
import com.zabbix4j.hostgroup.HostgroupGetResponse;
import com.zabbix4j.hostgroup.HostgroupObject;
import com.zabbix4j.hostinteface.HostInterfaceObject;
import com.zabbix4j.item.ItemCreateRequest;
import com.zabbix4j.item.ItemGetRequest;
import com.zabbix4j.item.ItemGetResponse;
import com.zabbix4j.item.ItemObject;
import com.zabbix4j.template.TemplateCreateRequest;
import com.zabbix4j.template.TemplateCreateResponse;
import com.zabbix4j.template.TemplateGetRequest;
import com.zabbix4j.template.TemplateGetResponse;
import com.zabbix4j.template.TemplateObject;
import com.zabbix4j.trigger.TriggerCreateRequest;
import com.zabbix4j.trigger.TriggerGetRequest;
import com.zabbix4j.trigger.TriggerGetResponse;
import com.zabbix4j.trigger.TriggerObject;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static io.jrevolt.sysmon.common.Utils.with;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.isTrue;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Service
public class ZabbixService {

	static private Logger LOG = LoggerFactory.getLogger(ZabbixService.class);

	@Autowired
	ZabbixCfg cfg;

	@Autowired
	ZabbixApi api;

	public List<HostgroupGetResponse.Result> getHostGroups() {
		HostgroupGetRequest req = new HostgroupGetRequest();
		HostgroupGetResponse res = api.hostgroup().get(req);
		return res.getResult();
	}

	public HostgroupObject getHostGroup(String name, boolean create) {
		HostgroupObject g = getHostGroup(name);
		if (nonNull(g)) { return g; }
		if (!create) { return null; }

		LOG.info("Creating host group \"{}\"", name);
		api.hostgroup().create(with(new HostgroupCreateRequest(), r -> r.getParams().setName(name)));
		return getHostGroup(name);
	}

	public HostgroupObject getHostGroup(String name) {
		HostgroupGetRequest req = new HostgroupGetRequest();
		req.getParams().setFilter(new HostgroupGetRequest.Filter());
		req.getParams().getFilter().addName(name);
		HostgroupGetResponse res = api.hostgroup().get(req);
		isTrue(res.getResult().size()<=1, "Expected single result");
		return res.getResult().stream().findFirst().orElse(null);
	}

	/// templates ///

	public TemplateCreateResponse.Result createTemplate(String name) {
		TemplateCreateResponse res = api.template().create(with(new TemplateCreateRequest(), r -> {
			r.getParams().setHost(name);
		}));
		return res.getResult();
	}

	public TemplateObject getTemplate(MonitoringTemplate template) {
		return getTemplate(template.getName());
	}

	public TemplateObject getTemplate(String name) {
		TemplateGetResponse res = api.template().get(with(new TemplateGetRequest(), r -> {
			r.getParams().setOutput("extend");
			r.getParams().setFilter(new TemplateGetRequest.Filter());
			r.getParams().getFilter().addHost(name);
		}));
		return res.getResult().stream().findFirst().orElse(null);
	}

	public TemplateObject getTemplate(String name, boolean create) {
		TemplateObject template = getTemplate(name);
		if (nonNull(template)) { return template; }
		if (!create) { return null; }

		LOG.info("Creating template {}", name);
		api.template().create(with(new TemplateCreateRequest(), r -> {
			r.getParams().setHost(name);
			r.getParams().addGroupId(getHostGroup("Templates").getGroupid());
		}));
		return getTemplate(name);
	}

	/// hosts ///

	public HostObject getHost(ServerDef server, boolean create) {
		HostObject host = getHost(server.getName());
		if (nonNull(host)) { return host; }
		if (!create) { return null; }

		LOG.info("Create host {}", server.getName());
		api.host().create(with(new HostCreateRequest(), r->{
			r.setId(1);
			r.getParams().setHost(server.getName());
			server.getClusterDef().getMonitoring().getGroups().forEach(g->{
				r.getParams().addGroupId(getHostGroup(g).getGroupid());
			});
			server.getClusterDef().getMonitoring().getTemplates().forEach(t->{
				r.getParams().addTemplateId(getTemplate(t).getTemplateid());
			});
			r.getParams().addHostInterfaceObject(with(new HostInterfaceObject(), i->{
				i.setType(1);
				i.setMain(1);
				i.setDns(server.getName());
				i.setIp(getIp(server.getName()));
				i.setUseip(i.getIp() != null ? 1 : 0);
				i.setPort(10050);
			}));
			r.getParams().setStatus(null);
		}));

		return getHost(server.getName());
	}

	public HostObject getHost(String name) {
		HostGetRequest req = new HostGetRequest();
		req.getParams().setFilter(new HostGetRequest.Filter());
		req.getParams().getFilter().addHost(name);
		HostGetResponse res = api.host().get(req);
		return res.getResult().stream().findFirst().orElse(null);
	}


	/// items ///

	public ItemObject getItem(MonitoringItem mi, boolean create) {
		ItemObject item = getItem(mi);
		if (nonNull(item)) { return item; }
		if (!create) { return null; }

		LOG.info("Creating item \"{}\" for template {}", mi.getName(), mi.getTemplate().getName());
		Integer applicationId = getApplication(mi.getTemplate().getName(), "appserver", true).getApplicationid();
		api.item().create(with(new ItemCreateRequest(), t -> {
			t.getParams().setName(mi.getName());
			t.getParams().setKey_(mi.getCommand());
			t.getParams().setHostid(getTemplate(mi.getTemplate()).getTemplateid());
			t.getParams().setType(mi.getType().ordinal());
			t.getParams().setValue_type(mi.getValueType().ordinal());
			t.getParams().setData_type(mi.getDataType().ordinal());
			t.getParams().setApplications(singletonList(applicationId));
			t.getParams().setDelay(60);
		}));
		return getItem(mi);
	}

	public ItemObject getItem(MonitoringItem mi) {
		ItemGetResponse res = api.item().get(with(new ItemGetRequest(), r->{
			r.getParams().setOutput("extend");
			r.getParams().setSearch(new GetRequestCommonParams.Search());
			r.getParams().getSearch().setName(mi.getName());
			r.getParams().setHostids(singletonList(getTemplate(mi.getTemplate()).getTemplateid()));
		}));
		return res.getResult().stream().filter(r->r.getName().equals(mi.getName())).findFirst().orElse(null);
	}

	/// triggers ///

	public TriggerObject getTrigger(MonitoringTrigger mt, boolean create) {
		TriggerObject trigger = getTrigger(mt);
		if (nonNull(trigger)) { return trigger; }
		if (!create) { return null; }

		LOG.info("Creating trigger \"{}\" for item \"{}\" in template {}",
					mt.getName(), mt.getItem().getName(), mt.getItem().getTemplate().getName());
		api.trigger().create(with(new TriggerCreateRequest(), r->{
			r.getParams().setDescription(mt.getName());
			r.getParams().setExpression(mt.getExpression().replace(
					"$item", format("%s:%s", mt.getItem().getTemplate().getName(), mt.getItem().getCommand())));
			r.getParams().setPriority(mt.getSeverity().ordinal());
		}));
		return getTrigger(mt);
	}

	public TriggerObject getTrigger(MonitoringTrigger trigger) {
		return getTrigger(trigger.getName(), getTemplate(trigger.getItem().getTemplate()));
	}

	public TriggerObject getTrigger(String name, TemplateObject template) {
		TriggerGetResponse res = api.trigger().get(with(new TriggerGetRequest(), r -> {
			r.getParams().setSearch(new GetRequestCommonParams.Search());
			r.getParams().getSearch().setName(name);
			r.getParams().setHostids(singletonList(template.getTemplateid()));
		}));
		return res.getResult().stream().filter(r -> r.getDescription().equals(name)).findFirst().orElse(null);
	}


	public TriggerObject updateTrigger(MonitoringTrigger mt) {
		TriggerObject t = getTrigger(mt);
		api.trigger().create(with(new TriggerCreateRequest(), r->{
			r.getParams().setExpression(mt.getExpression().replace(
					"$item", format("%s:%s", mt.getItem().getTemplate().getName(), mt.getItem().getName())));
			r.getParams().setPriority(mt.getSeverity().ordinal());
		}));
		return getTrigger(mt);
	}

	public ConfigurationExportResponse exportHostGroups(List<Integer> groups) {
		ConfigurationExportRequest req = new ConfigurationExportRequest();
		req.getParams().setFormat("xml");
		Option options = new Option();
		groups.stream().forEach(options::addGroupId);
		req.getParams().setOptions(options);
		ConfigurationExportResponse res = api.configuration().export(req);
		return res;
	}

	public ConfigurationExportResponse exportHosts(List<Integer> hosts) {
		ConfigurationExportRequest req = new ConfigurationExportRequest();
		req.getParams().setFormat("xml");
		Option options = new Option();
		hosts.stream().forEach(options::addHostId);
		req.getParams().setOptions(options);
		ConfigurationExportResponse res = api.configuration().export(req);
		return res;
	}

	/// applications

	public synchronized ApplicationObject getApplication(String template, String name, boolean create) {
		Integer templateId = getTemplate(template).getTemplateid();
		ApplicationObject app = api.application().get(with(new ApplicationGetRequest(), r -> {
			r.getParams().setOutput("extend");
			r.getParams().setSearch(with(new ItemGetRequest.Search(), s -> {
				s.setName(name);
			}));
			r.getParams().setHostids(singletonList(templateId));
		})).getResult().stream().filter(o->o.getName().equals(name)).findFirst().orElse(null);
		if (nonNull(app)) { return app; }
		if (!create) { return null; }

		api.application().create(with(new ApplicationCreateRequest(), r -> {
			r.getParams().setName(name);
			r.getParams().setHostid(templateId);
		}));

		return getApplication(template, name, false);
	}

	///

	String getIp(String host) {
		try {
			SimpleResolver resolver = new SimpleResolver(cfg.getDnsServer());
			Lookup l = new Lookup(host);
			l.setResolver(resolver);
			l.run();
			if (l.getResult() == Lookup.SUCCESSFUL) {
				String resolved = l.getAnswers()[0].rdataToString();
				return resolved;
			} else {
				return null;
			}
		} catch (UnknownHostException e) {
			return null;
		} catch (TextParseException e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
