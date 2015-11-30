package io.jrevolt.sysmon.cloud.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.Gson;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public abstract class ApiObject {

	public String toString() {
		return new Gson().toJsonTree(this).toString();
	}

}
