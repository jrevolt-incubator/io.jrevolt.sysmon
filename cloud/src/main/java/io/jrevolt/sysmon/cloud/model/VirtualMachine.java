package io.jrevolt.sysmon.cloud.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class VirtualMachine extends ApiObject {

	//enum State { Running, Stopped, Present, Destroyed, Expunged }

	String id;
	String displayname;
	String domainid;
	String state;
	String zoneid;
	String zonename;
	int cpunumber;
	int cpuspeed;
	int memory;
	String ipaddress;
	Set<Tag> tags = new LinkedHashSet<>();
	List<NIC> nic = new LinkedList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getDomainid() {
		return domainid;
	}

	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZoneid() {
		return zoneid;
	}

	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}

	public String getZonename() {
		return zonename;
	}

	public void setZonename(String zonename) {
		this.zonename = zonename;
	}

	public int getCpunumber() {
		return cpunumber;
	}

	public void setCpunumber(int cpunumber) {
		this.cpunumber = cpunumber;
	}

	public int getCpuspeed() {
		return cpuspeed;
	}

	public void setCpuspeed(int cpuspeed) {
		this.cpuspeed = cpuspeed;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public List<NIC> getNic() {
		return nic;
	}

	public void setNic(List<NIC> nic) {
		this.nic = nic;
	}


	///

	public boolean containsTag(String name) {
		return tags.stream().map(Tag::getKey).distinct().anyMatch(s->s.equals(name));
	}

	public String getTag(String name) {
		return getTag(name, null);
	}

	public String getTag(String name, String dflt) {
		return tags.stream()
				.filter(t -> t.getKey().equals(name))
				.map(t->t.getValue())
				.findFirst().orElse(dflt);
	}

}
