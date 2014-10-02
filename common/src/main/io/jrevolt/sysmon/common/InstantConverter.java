package io.jrevolt.sysmon.common;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.time.Instant;
import java.util.Date;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class InstantConverter extends GenericConverter<Instant> {

	@Override
	protected Instant fromString(String s) {
		return Instant.parse(s);
	}

	@Override
	protected String toString(Instant instant) {
		return instant.toString();
	}
}
