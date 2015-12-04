package io.jrevolt.sysmon.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class HostDef extends DomainObject {

	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Monitoring getMonitoring() {
		return null;
	}
}
