@XmlJavaTypeAdapters(
		@XmlJavaTypeAdapter(type=Instant.class, value=InstantConverter.class)
)
package io.jrevolt.sysmon.model;

import io.jrevolt.sysmon.common.InstantConverter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.Instant;