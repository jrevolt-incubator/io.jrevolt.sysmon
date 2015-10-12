package io.jrevolt.sysmon.common;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class SysmonException extends RuntimeException {

	public SysmonException(Throwable cause, String message, Object ... params) {
		super(String.format(message, params), cause);
	}

}
