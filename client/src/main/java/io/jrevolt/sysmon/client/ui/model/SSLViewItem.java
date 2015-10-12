package io.jrevolt.sysmon.client.ui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class SSLViewItem {

	private StringProperty hostname = new SimpleStringProperty();
	private StringProperty ip = new SimpleStringProperty();
	private IntegerProperty port = new SimpleIntegerProperty();
	private StringProperty commonName = new SimpleStringProperty();
	private StringProperty subjectAlternativeName = new SimpleStringProperty();
	private StringProperty issuer = new SimpleStringProperty();
	private ObjectProperty<Date> validFrom = new SimpleObjectProperty<>();
	private ObjectProperty<Date> validTo = new SimpleObjectProperty<>();

	public String getHostname() {
		return hostname.get();
	}

	public StringProperty hostnameProperty() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname.set(hostname);
	}

	public String getIp() {
		return ip.get();
	}

	public StringProperty ipProperty() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip.set(ip);
	}

	public int getPort() {
		return port.get();
	}

	public IntegerProperty portProperty() {
		return port;
	}

	public void setPort(int port) {
		this.port.set(port);
	}

	public String getCommonName() {
		return commonName.get();
	}

	public StringProperty commonNameProperty() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName.set(commonName);
	}

	public String getSubjectAlternativeName() {
		return subjectAlternativeName.get();
	}

	public StringProperty subjectAlternativeNameProperty() {
		return subjectAlternativeName;
	}

	public void setSubjectAlternativeName(String subjectAlternativeName) {
		this.subjectAlternativeName.set(subjectAlternativeName);
	}

	public String getIssuer() {
		return issuer.get();
	}

	public StringProperty issuerProperty() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer.set(issuer);
	}

	public Date getValidFrom() {
		return validFrom.get();
	}

	public ObjectProperty<Date> validFromProperty() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom.set(validFrom);
	}

	public Date getValidTo() {
		return validTo.get();
	}

	public ObjectProperty<Date> validToProperty() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo.set(validTo);
	}
}
