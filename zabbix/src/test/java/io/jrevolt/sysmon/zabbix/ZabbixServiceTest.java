package io.jrevolt.sysmon.zabbix;


import io.jrevolt.sysmon.model.DomainDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import com.zabbix4j.configuration.ConfigurationExportResponse;
import com.zabbix4j.host.HostCreateRequest;
import com.zabbix4j.hostgroup.HostgroupObject;
import com.zabbix4j.hostinteface.HostInterfaceObject;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ZabbixMain.class)
@ActiveProfiles("unittest")
public class ZabbixServiceTest {

	@Autowired
	ZabbixService zbx;

	@Autowired
	DomainDef domain;

	<T> T with(T t, Consumer<T> consumer) {
		consumer.accept(t);
		return t;
	}

	String getIp(String host) {
		try {
			SimpleResolver resolver = new SimpleResolver("st1adds01");
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

	@Test
	public void configureHosts() {
		domain.getClusters().stream().flatMap(c->c.getServers().stream()).forEach(s->{
			zbx.api.host().create(with(new HostCreateRequest(), r->{
				r.getParams().setHost(s.getName());
				r.getParams().addGroupId(zbx.getHostGroup("Linux servers").getGroupid());
				r.getParams().addHostInterfaceObject(with(new HostInterfaceObject(), i->{
					i.setType(1);
					i.setMain(1);
					i.setDns(s.getName());
					i.setIp(getIp(s.getName()));
					i.setUseip(i.getIp() != null ? 1 : 0);
					i.setPort(10050);
				}));
			}));
		});
	}

	@Test
	public void getHostGroups() {
		assertFalse("No host groups", zbx.getHostGroups().isEmpty());
	}

	@Test
	public void getHostGroup() {
		assertNotNull("No host group", zbx.getHostGroup("Linux servers"));
	}

	@Test @Ignore
	public void getHosts() {
//		assertFalse("No host groups", zbx.getHosts().isEmpty());
	}

	@Test
	public void exportHosts() {
		List<Integer> hosts = new LinkedList<>();
		hosts.add(zbx.getHost("Zabbix server").getHostid());
		ConfigurationExportResponse res = zbx.exportHosts(hosts);
		System.out.println(res.getResult());
	}

	@Test
	public void exportHostGroups() {
		List<Integer> groups = new LinkedList<>();
		groups.addAll(zbx.getHostGroups().stream().map(HostgroupObject::getGroupid).collect(Collectors.toList()));
		ConfigurationExportResponse res = zbx.exportHostGroups(groups);
		System.out.println(res.getResult());
	}


}
