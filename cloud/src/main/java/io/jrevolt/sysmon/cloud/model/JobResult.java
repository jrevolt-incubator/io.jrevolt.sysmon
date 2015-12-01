package io.jrevolt.sysmon.cloud.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class JobResult extends ApiObject {

	VirtualMachine virtualmachine;

	public VirtualMachine getVirtualmachine() {
		return virtualmachine;
	}

	public void setVirtualmachine(VirtualMachine virtualmachine) {
		this.virtualmachine = virtualmachine;
	}
}
