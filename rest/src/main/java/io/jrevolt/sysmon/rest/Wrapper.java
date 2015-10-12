package io.jrevolt.sysmon.rest;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Wrapper<T> {

	static public <T> Wrapper<T> wrap(T object) {
		return new Wrapper<>(object);
	}

	private T object;

	public Wrapper() {
	}

	public Wrapper(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}
