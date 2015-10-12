package io.jrevolt.sysmon.client.ui;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class Log {

	static public void log(Object source, String message, Object ... args) {
		Class cls = source instanceof Class ? ((Class) source) : source.getClass();
		Logger logger = (Logger) LoggerFactory.getLogger(cls);
		LoggingEvent le = new LoggingEvent(cls.getName(), logger, Level.INFO, String.format(message, args), null, null);
		logger.callAppenders(le);
	}

}
