package io.jrevolt.sysmon.common;

import java.time.Duration;
import java.time.temporal.Temporal;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public abstract class TemporalConverter<T extends Temporal> extends GenericConverter<T> {
	@Override
	protected String toString(T t) {
		return t.toString();
	}
}
