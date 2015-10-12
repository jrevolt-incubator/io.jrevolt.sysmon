package io.jrevolt.sysmon.model;

import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class SSLInfo {

	static public enum Status { OK, UNTRUSTED, INVALID, EXPIRED, ERROR }

	private String hostname;
	private int port;
	private X509Certificate cert;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public X509Certificate getCert() {
		return cert;
	}

	public void setCert(X509Certificate cert) {
		this.cert = cert;
	}
}
