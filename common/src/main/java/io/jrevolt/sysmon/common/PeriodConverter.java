package io.jrevolt.sysmon.common;

import java.time.Instant;
import java.time.Period;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class PeriodConverter extends GenericConverter<Period> {

	@Override
	protected Period fromString(String s) {
		return Period.parse(s);
	}

	@Override
	protected String toString(Period period) {
		return period.toString();
	}
}
