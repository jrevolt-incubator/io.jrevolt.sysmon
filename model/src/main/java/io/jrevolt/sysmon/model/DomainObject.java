package io.jrevolt.sysmon.model;

import com.google.gson.Gson;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public abstract class DomainObject {
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
