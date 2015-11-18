package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MonitoringItem {

	public enum Type { AGENT }

	public enum ValueType { FLOAT, CHAR, LOG, INTEGER, TEXT }

	public enum DataType { DECIMAL, OCTAL, HEX, BOOLEAN }

	private String tag;

	private String name;
	private Type type = Type.AGENT;
	private String command; // key
	private ValueType valueType = ValueType.INTEGER;
	private DataType dataType = DataType.DECIMAL; // for valueType=INTEGER only
	private String application;
	private MonitoringTrigger trigger;

	private MonitoringTemplate template;

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

	///

	void init(MonitoringTemplate template) {
		setTemplate(template);
		if (trigger != null) { trigger.init(this); }
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("name", name)
				.toString();
	}
}
