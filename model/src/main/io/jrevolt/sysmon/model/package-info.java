@XmlJavaTypeAdapters({
		@XmlJavaTypeAdapter(type=Instant.class, value=InstantConverter.class),
		@XmlJavaTypeAdapter(type=Period.class, value=PeriodConverter.class),
		@XmlJavaTypeAdapter(type=Duration.class, value=DurationConverter.class)
})
package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.DurationConverter;
import io.jrevolt.sysmon.common.InstantConverter;
import io.jrevolt.sysmon.common.PeriodConverter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;