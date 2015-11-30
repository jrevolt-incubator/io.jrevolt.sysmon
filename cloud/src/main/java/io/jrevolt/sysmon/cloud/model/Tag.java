package io.jrevolt.sysmon.cloud.model;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Tag extends ApiObject {

	public enum Name { ENV, fqdn, hostname, start, stop }

	String key;
	String value;

	public Tag() {
	}

	public Tag(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Tag tag = (Tag) o;

		if (!key.equals(tag.key)) return false;
		return value.equals(tag.value);

	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
