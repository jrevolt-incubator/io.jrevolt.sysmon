package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MonitoringItem extends DomainObject {

	public enum Type {
		AGENT, SNMP1, TRAPPER, SIMPLE, SNMP2, INTERNAL, SNMP3, AGENT_ACTIVE, AGGREGATE, WEB, EXTERNAL, DB, IPMI,
		SSH, TELNET, CALCULATED, JMX, SNMP_TRAP
	}

	public enum ValueType { FLOAT, CHAR, LOG, INTEGER, TEXT }

	public enum DataType { DECIMAL, OCTAL, HEX, BOOLEAN }

	private String tag;

	private String name;
	private Type type = Type.AGENT;
	private String command; // key
	private ValueType valueType = ValueType.INTEGER;
	private DataType dataType = DataType.DECIMAL; // for valueType=INTEGER only
	private String params;
	private String units;
	private BigDecimal formula;
	private String application;
	private MonitoringTrigger trigger;

	private transient MonitoringTemplate template;
	private transient HostDef hostDef;

	public MonitoringItem() {
	}

	public MonitoringItem(MonitoringItem src) {
		this.name = src.name;
		this.type = src.type;
		this.command = src.command;
		this.dataType = src.dataType;
		this.application = src.application;
		this.trigger = nonNull(src.trigger) ? new MonitoringTrigger(src.trigger) : null;
	}


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public BigDecimal getFormula() {
		return formula;
	}

	public void setFormula(BigDecimal formula) {
		this.formula = formula;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public MonitoringTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(MonitoringTrigger trigger) {
		this.trigger = trigger;
	}

	public MonitoringTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MonitoringTemplate template) {
		this.template = template;
	}

	public HostDef getHostDef() {
		return hostDef;
	}

	public void setHostDef(HostDef hostDef) {
		this.hostDef = hostDef;
	}

	///

	public String getTemplateName() {
		return getTemplate() != null ? getTemplate().getName()
				: getHostDef() != null ? getHostDef().getName()
				: null;
	}

	///

	void init(MonitoringTemplate template, HostDef hostDef) {
		setTemplate(template);
		setHostDef(hostDef);

		if (trigger != null) { trigger.init(this); }

		if ("%".equals(units) && isNull(formula) && valueType.equals(ValueType.FLOAT)) {
			formula = new BigDecimal(100);
		}
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("name", name)
				.toString();
	}
}
