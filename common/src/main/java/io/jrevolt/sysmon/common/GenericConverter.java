package io.jrevolt.sysmon.common;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public abstract class GenericConverter<T> extends XmlAdapter<String, T> implements Converter  {

	/// XmlAdapter

	@Override
	public T unmarshal(String v) throws Exception {
		if (v == null) { return null; }
		else { return fromString(v); }
	}

	@Override
	public String marshal(T v) throws Exception {
		if (v == null) { return null; }
		else { return toString(v); }
	}


	/// Converter

	@Override
	public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext ctx) {
		if (o == null) { return; }
		else { writer.setValue(toString((T) o)); }
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext ctx) {
		String s = reader.getValue();
		if (s == null) { return null; }
		else { return fromString(s); }
	}

	@Override
	public boolean canConvert(Class cls) {
		return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].equals(cls);
	}

	///

	abstract protected T fromString(String s);

	abstract protected String toString(T t);

}
