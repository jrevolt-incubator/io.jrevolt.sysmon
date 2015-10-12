package io.jrevolt.sysmon.jms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JMSSelector {
	String value();
}
