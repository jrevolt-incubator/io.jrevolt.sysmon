package io.jrevolt.sysmon.cloud.model;

import java.util.List;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ListVirtualMachinesResponse extends ApiObject {

	int count;
	List<VirtualMachine> virtualmachine;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<VirtualMachine> getVirtualmachine() {
		return virtualmachine;
	}

	public void setVirtualmachine(List<VirtualMachine> virtualmachine) {
		this.virtualmachine = virtualmachine;
	}
}
