package io.jrevolt.sysmon.common;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class VersionInfo {

	private String artifactUri;
	private String artifactVersion;
	private Instant timestamp;

	public VersionInfo(String artifactUri, String artifactVersion, Instant timestamp) {
		this.artifactUri = artifactUri;
		this.artifactVersion = artifactVersion;
		this.timestamp = timestamp;
	}

	public String getArtifactUri() {
		return artifactUri;
	}

	public String getArtifactVersion() {
		return artifactVersion;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	static public VersionInfo forClass(Class cls) {
		VersionInfo info = null;
		try {
			URL root = cls.getProtectionDomain().getCodeSource().getLocation();
			URL url;
			if (root == null) {
				String uri = cls.getName()
						.replace('.', '/')
						.replaceFirst("\\p{javaJavaIdentifierPart}+$", "")
						.replaceAll("\\p{javaJavaIdentifierPart}+", "..")
						.replaceFirst("$", "META-INF/MANIFEST.MF");
				url = cls.getResource(uri);
			} else {
				url = new URL(root, "META-INF/MANIFEST.MF");
			}
			if (url != null) {
				URLConnection con = url.openConnection();
				Manifest mf = new Manifest(con.getInputStream());
				Attributes attrs = mf.getMainAttributes();
				info = new VersionInfo(
						attrs.getValue("Artifact-URI"),
						attrs.getValue("Artifact-Version"),
						parse(attrs.getValue("Build-Timestamp"), attrs.getValue("Build-Timestamp-Format"))
				);
			}
		} catch (IOException e) {
			Log.debug(VersionInfo.class, e.toString());
		}
		if (info == null) {
			try {
				Instant timestamp = new Date(
						cls.getResource(cls.getSimpleName()+".class").openConnection().getLastModified()).toInstant();
				info = new VersionInfo(cls.getName(), "UNKNOWN", timestamp);
			} catch (IOException never) {
				throw new AssertionError(never);
			}
		}
		return info;
	}

	static private Instant parse(String date, String format) {
		if (date == null || format == null) { return null; }
		try {
			return new SimpleDateFormat(format).parse(date).toInstant();
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}




}
