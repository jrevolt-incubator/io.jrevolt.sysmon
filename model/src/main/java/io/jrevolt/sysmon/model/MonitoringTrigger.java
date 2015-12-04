package io.jrevolt.sysmon.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.PostConstruct;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MonitoringTrigger {

	public enum Severity { UNDEFINED, INFORMATION, WARNING, AVERAGE, HIGH, DISASTER }

	private String name;

	/**
	 * Simplified trigger expression (template and command references are injected automatically.
	 * E.g. {@code last()<>0} expands to {@code {TemplateName:ItemCommand.last()<>0}}.
	 */
	private String expression;
	private String description;
	private Severity severity = Severity.WARNING;

	private MonitoringItem item;

	public MonitoringTrigger() {
	}

	public MonitoringTrigger(MonitoringTrigger src) {
		this.name = src.name;
		this.expression = src.expression;
		this.description = src.description;
		this.severity = src.severity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public MonitoringItem getItem() {
		return item;
	}

	public void setItem(MonitoringItem item) {
		this.item = item;
	}

	///

	void init(MonitoringItem item) {
		setItem(item);
		setName(getName().replace("$item", getItem().getName()));
		setExpression(getExpression().replace("$item", format(
				"%s:%s", getItem().getTemplateName(), getItem().getCommand())));
	}



	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("expression", expression)
				.append("description", description)
				.append("severity", severity)
				.toString();
	}
}
