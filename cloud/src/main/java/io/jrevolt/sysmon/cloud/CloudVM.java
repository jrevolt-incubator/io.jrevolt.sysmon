package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.NIC;
import io.jrevolt.sysmon.cloud.model.VirtualMachine;

import com.google.gson.Gson;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class CloudVM {

	private String id;
	private String name;

	// tags
	private String hostname;
	private String environment;
	private Integer startLevel;
	private Integer startWait;

	// src
	private VirtualMachine virtualMachine;

	public CloudVM(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
		this.id = virtualMachine.getId();
		this.name = virtualMachine.getDisplayname();
		this.environment = virtualMachine.getTag("environment");
		this.hostname = virtualMachine.getTag("hostname");
		this.startLevel = Optional.ofNullable(virtualMachine.getTag("startLevel")).map(Integer::parseInt).orElse(null);
		this.startWait = Optional.ofNullable(virtualMachine.getTag("startWait")).map(Integer::parseInt).orElse(null);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getHostname() {
		return hostname;
	}

	public String getEnvironment() {
		return environment;
	}

	public Integer getStartLevel() {
		return startLevel;
	}

	public Integer getStartWait() {
		return startWait;
	}

	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}

	///

	public String getIpAddress() {
		return getVirtualMachine().getNic().stream().map(NIC::getIpaddress).collect(Collectors.toList()).toString();
	}

	public String getMacAddress() {
		return getVirtualMachine().getNic().stream().map(NIC::getMacaddress).collect(Collectors.toList()).toString();
	}

	///

	public String toString() {
		return new Gson().toJsonTree(this).toString();
	}

}
