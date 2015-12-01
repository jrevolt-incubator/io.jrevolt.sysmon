package io.jrevolt.sysmon.cloud;

import io.jrevolt.sysmon.cloud.model.VirtualMachine;

import com.google.gson.Gson;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class CloudVM {

	private String id;
	private String hostname;
	private String environment;
	private int startLevel;
	private int stopLevel;

	private VirtualMachine virtualMachine;

	public CloudVM(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
		this.id = virtualMachine.getId();
		this.environment = virtualMachine.getTag("ENV");
		this.hostname = virtualMachine.getTag("fqdn");
		this.startLevel = Integer.parseInt(virtualMachine.getTag("start", "0"));
		this.stopLevel = Integer.parseInt(virtualMachine.getTag("stop", "0"));
	}

	public String getId() {
		return id;
	}

	public String getHostname() {
		return hostname;
	}

	public String getEnvironment() {
		return environment;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public int getStopLevel() {
		return stopLevel;
	}

	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}

	public String toString() {
		return new Gson().toJsonTree(this).toString();
	}

}
