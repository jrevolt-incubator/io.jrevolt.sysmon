package io.jrevolt.sysmon.zabbix;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public enum GetMode {
	GET,
	CREATE,
	UPDATE,
	;

	public boolean isGet() { return equals(GET); }
	public boolean isCreate() { return equals(CREATE) || equals(UPDATE); }
	public boolean isUpdate() { return equals(UPDATE); }

}
