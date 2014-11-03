package io.jrevolt.sysmon.client.ui;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Utils {

	static public Duration friendlify(Duration duration) {
		return
				duration.toDays() > 3 ? Duration.of(duration.toDays(), ChronoUnit.DAYS) :
				duration.toHours() > 3 ? Duration.of(duration.toHours(), ChronoUnit.HOURS) :
				duration.toMinutes() > 3 ? Duration.of(duration.toMinutes(), ChronoUnit.MINUTES) :
				duration.toMillis() > 10000 ? Duration.of(duration.toMillis(), ChronoUnit.MILLIS) :
				duration
				;
	}

	static public Instant friendlify(Instant instant) {
		Instant now = Instant.now();
		return
				instant.until(now, ChronoUnit.DAYS) > 3 ? instant.truncatedTo(ChronoUnit.DAYS) :
				instant.until(now, ChronoUnit.HOURS) > 48 ? instant.truncatedTo(ChronoUnit.HOURS) :
				instant.until(now, ChronoUnit.MINUTES) > 60 ? instant.truncatedTo(ChronoUnit.MINUTES) :
				instant.until(now, ChronoUnit.SECONDS) > 60 ? instant.truncatedTo(ChronoUnit.SECONDS) :
				instant;
	}

}
