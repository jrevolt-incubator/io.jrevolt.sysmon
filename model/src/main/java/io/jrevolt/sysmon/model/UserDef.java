package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.Utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;

import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class UserDef extends DomainObject {

	private String userId;
	private String name;
	private String surname;
	private InternetAddress email;

	public UserDef() {
	}

	public UserDef(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public InternetAddress getEmail() {
		return email;
	}

	public void setEmail(InternetAddress email) {
		this.email = email;
	}

	///


	@PostConstruct
	void init() {
		// if name/surname is not provided but RFC email address contains usable personal part, extract it:
		if (isNull(name) && isNull(surname) && !isNull(email.getPersonal())) {
			String[] items = email.getPersonal().split(" ");
			name = Utils.get(items, 0);
			surname = Utils.get(items, 1);
		}
	}

	///


}
