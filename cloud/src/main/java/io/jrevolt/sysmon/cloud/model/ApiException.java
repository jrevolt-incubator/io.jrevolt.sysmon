package io.jrevolt.sysmon.cloud.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ApiException extends RuntimeException {

	ErrorResponse response;

	public ApiException(ErrorResponse response) {
		this.response = response;
	}

	public ErrorResponse getResponse() {
		return response;
	}
}
