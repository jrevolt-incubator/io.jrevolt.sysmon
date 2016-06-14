package io.jrevolt.sysmon.model;

import org.springframework.beans.factory.InitializingBean;

import com.google.gson.Gson;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public abstract class DomainObject implements InitializingBean {
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
