package io.jrevolt.sysmon.cloud.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ErrorResponse extends ApiObject {

	int errorcode;
	int cserrorcode;
	String errortext;

	public int getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}

	public int getCserrorcode() {
		return cserrorcode;
	}

	public void setCserrorcode(int cserrorcode) {
		this.cserrorcode = cserrorcode;
	}

	public String getErrortext() {
		return errortext;
	}

	public void setErrortext(String errortext) {
		this.errortext = errortext;
	}
}
