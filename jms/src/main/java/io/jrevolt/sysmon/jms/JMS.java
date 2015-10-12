package io.jrevolt.sysmon.jms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JMS {
	boolean topic() default false;
	long timeToLive() default 60*60*1000; // 1hr
}
