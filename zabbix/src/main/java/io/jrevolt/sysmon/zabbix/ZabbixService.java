package io.jrevolt.sysmon.zabbix;

import io.jrevolt.sysmon.model.HostDef;
import io.jrevolt.sysmon.model.MonitoringItem;
import io.jrevolt.sysmon.model.MonitoringTemplate;
import io.jrevolt.sysmon.model.MonitoringTrigger;
import io.jrevolt.sysmon.model.ProxyDef;

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
import com.zabbix4j.application.ApplicationObject;
import com.zabbix4j.configuration.ConfigurationExportRequest;
import com.zabbix4j.configuration.ConfigurationExportResponse;
import com.zabbix4j.configuration.Option;
import com.zabbix4j.host.HostCreateRequest;
import com.zabbix4j.host.HostGetRequest;
import com.zabbix4j.host.HostObject;
import com.zabbix4j.hostgroup.HostgroupCreateRequest;
import com.zabbix4j.hostgroup.HostgroupGetRequest;
import com.zabbix4j.hostgroup.HostgroupGetResponse;
import com.zabbix4j.hostgroup.HostgroupObject;
import com.zabbix4j.hostinteface.HostInterfaceGetRequest;
import com.zabbix4j.hostinteface.HostInterfaceObject;
import com.zabbix4j.item.ItemCreateRequest;
import com.zabbix4j.item.ItemGetRequest;
import com.zabbix4j.item.ItemGetResponse;
import com.zabbix4j.item.ItemObject;
import com.zabbix4j.proxy.ProxyCreateRequest;
import com.zabbix4j.proxy.ProxyGetRequest;
import com.zabbix4j.proxy.ProxyGetResponse;
import com.zabbix4j.proxy.ProxyObject;
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

import static io.jrevolt.sysmon.common.Utils.with;
import static java.lang.String.format;
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
		return template != null ? getTemplate(template.getName()) : null;
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

	/// proxies ///

	public ProxyObject getProxy(String name) {
		List<ProxyGetResponse.Result> result = api.proxy().get(with(new ProxyGetRequest(), r -> {
			r.getParams().setOutput("extend");
		})).getResult();
		return result.stream()
				.filter(p -> p.getHost().equals(name))
				.findFirst().orElse(null);
	}

	public ProxyObject getProxy(String name, boolean create) {
		ProxyObject proxy = getProxy(name);

		if (proxy != null) { return proxy; }
		if (!create) { return null; }

		api.proxy().create(with(new ProxyCreateRequest(), r -> {
			r.getParams().setHost(name);
			r.getParams().setStatus(5); // FIXME hardcoded proxy type (active)
		}));

		return getProxy(name);
	}

	/// hosts ///

	public HostObject getHost(HostDef hostdef, boolean create) {
		HostObject host = getHost(hostdef.getName());
		if (nonNull(host)) { return host; }
		if (!create) { return null; }

		LOG.info("Create host {}", hostdef.getName());
		api.host().create(with(new HostCreateRequest(), r->{
			r.setId(1);
			r.getParams().setHost(hostdef.getName());
			with(hostdef.getMonitoring(), m->{
				m.getGroups().forEach(g->{
					r.getParams().addGroupId(getHostGroup(g).getGroupid());
				});
				m.getTemplates().forEach(t->{
					r.getParams().addTemplateId(getTemplate(t).getTemplateid());
				});
			});
			r.getParams().addHostInterfaceObject(with(new HostInterfaceObject(), i->{
				i.setType(1);
				i.setMain(1);
				i.setDns(hostdef.getName());
				i.setIp(getIp(hostdef.getName()));
				i.setUseip(i.getIp() != null ? 1 : 0);
				i.setPort(10050);
			}));
			if (hostdef instanceof ProxyDef) {
				configure(r, ((ProxyDef) hostdef));
			}
			r.getParams().setStatus(null);
			if (r.getParams().getGroups() == null) {
				r.getParams().addGroupId(getHostGroup(cfg.getDefaultMonitoringGroup(), true).getGroupid());
			}
		}));

		return getHost(hostdef.getName());
	}

	private void configure(HostCreateRequest r, ProxyDef proxy) {
		String zproxyname = proxy.getMonitoring() != null ? proxy.getMonitoring().getProxy() : null;
		if (zproxyname == null) { return; }

		r.getParams().setProxy_hostid(getProxy(zproxyname).getProxyid());
	}

	public HostObject getHost(String name) {
		return api.host().get(with(new HostGetRequest(), r->{
			r.getParams().setFilter(new HostGetRequest.Filter());
			r.getParams().getFilter().addHost(name);
		})).getResult().stream().findFirst().orElse(null);
	}


	/// items ///

	public ItemObject getItem(MonitoringItem mi, boolean create) {
		ItemObject item = getItem(mi);
		if (nonNull(item)) { return item; }
		if (!create) { return null; }

		LOG.info("Creating item \"{}\" for template {}",
					mi.getName(),
					nonNull(mi.getTemplate()) ? mi.getTemplate().getName() : "<none>"
		);
		Integer applicationId = getApplication(mi, "appserver", true).getApplicationid();
		api.item().create(with(new ItemCreateRequest(), i -> {
			i.getParams().setName(mi.getName());
			i.getParams().setKey_(mi.getCommand());
			i.getParams().setHostid(resolveHostId(mi));
			i.getParams().setInterfaceid(0);
			i.getParams().setType(mi.getType().ordinal());
			i.getParams().setValue_type(mi.getValueType().ordinal());
			i.getParams().setData_type(mi.getDataType().ordinal());
			i.getParams().setParams(mi.getParams());
			i.getParams().setUnits(mi.getUnits());
			if (nonNull(mi.getFormula())) {
				i.getParams().setFormula(mi.getFormula().floatValue());
				i.getParams().setMultiplier(1);
			}
			if (mi.getType().equals(MonitoringItem.Type.EXTERNAL)) {
				HostInterfaceObject iface = getHostInterface(getHost(mi.getHostDef().getName()));
				i.getParams().setInterfaceid(iface.getInterfaceid());
			}
			i.getParams().setApplications(singletonList(applicationId));
			i.getParams().setDelay(60);
		}));
		return getItem(mi);
	}

	public ItemObject getItem(MonitoringItem mi) {
		ItemGetResponse res = api.item().get(with(new ItemGetRequest(), r->{
			r.getParams().setOutput("extend");
			r.getParams().setSearch(new GetRequestCommonParams.Search());
			r.getParams().getSearch().setName(mi.getName());
			if (nonNull(mi.getTemplate())) {
				r.getParams().setHostids(singletonList(getTemplate(mi.getTemplate()).getTemplateid()));
			}
		}));
		return res.getResult().stream().filter(r->r.getName().equals(mi.getName())).findFirst().orElse(null);
	}

	/// triggers ///

	public TriggerObject getTrigger(MonitoringTrigger mt, boolean create) {
		TriggerObject trigger = getTrigger(mt);
		if (nonNull(trigger)) { return trigger; }
		if (!create) { return null; }

		MonitoringItem i = mt.getItem();
		LOG.info("Creating trigger \"{}\" for item \"{}\" in {} {}",
					mt.getName(), i.getName(),
					nonNull(i.getTemplate()) ? "template" : "host",
					i.getTemplateName());
		api.trigger().create(with(new TriggerCreateRequest(), r->{
			r.getParams().setDescription(mt.getName());
			r.getParams().setExpression(mt.getExpression().replace(
					"$item", format("%s:%s", i.getTemplateName(), i.getCommand())));
			r.getParams().setPriority(mt.getSeverity().ordinal());
		}));
		return getTrigger(mt);
	}

	public TriggerObject getTrigger(MonitoringTrigger trigger) {
		return api.trigger().get(with(new TriggerGetRequest(), r -> {
			r.getParams().setSearch(new GetRequestCommonParams.Search());
			r.getParams().getSearch().setName(trigger.getName());
			r.getParams().setHostids(singletonList(resolveHostId(trigger.getItem())));
		})).getResult().stream()
				.filter(r -> r.getDescription().equals(trigger.getName()))
				.findFirst().orElse(null);
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

	public synchronized ApplicationObject getApplication(MonitoringItem item, String name, boolean create) {
		Integer hostId = resolveHostId(item);

		ApplicationObject app = api.application().get(with(new ApplicationGetRequest(), r -> {
			r.getParams().setOutput("extend");
			r.getParams().setSearch(with(new ItemGetRequest.Search(), s -> {
				s.setName(name);
			}));
			r.getParams().setHostids(singletonList(hostId));
		})).getResult().stream().filter(o->o.getName().equals(name)).findFirst().orElse(null);
		if (nonNull(app)) { return app; }
		if (!create) { return null; }

		api.application().create(with(new ApplicationCreateRequest(), r -> {
			r.getParams().setName(name);
			r.getParams().setHostid(hostId);
		}));

		return getApplication(item, name, false);
	}

	private Integer resolveHostId(MonitoringItem item) {
		return nonNull(item.getTemplate()) ? getTemplate(item.getTemplate()).getTemplateid()
		: nonNull(item.getHostDef()) ? getHost(item.getHostDef().getName()).getHostid()
		: null;
	}

	HostInterfaceObject getHostInterface(HostObject host) {
		return api.hostInterface().get(with(new HostInterfaceGetRequest(), r -> {
			r.getParams().setHostids(singletonList(host.getHostid()));
		})).getResult().stream().findFirst().orElse(null);
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
