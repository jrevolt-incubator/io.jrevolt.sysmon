package io.jrevolt.sysmon.common;

import java.time.Duration;
import java.time.Period;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class DurationConverter extends GenericConverter<Duration> {

	@Override
	protected Duration fromString(String s) {
		return Duration.parse(s);
	}

	@Override
	protected String toString(Duration period) {
		return period.toString();
	}
}
