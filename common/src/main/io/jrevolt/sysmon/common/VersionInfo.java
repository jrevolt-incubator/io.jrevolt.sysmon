package io.jrevolt.sysmon.common;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class VersionInfo {

	private String artifactUri;
	private String artifactVersion;

	public VersionInfo(String artifactUri, String artifactVersion) {
		this.artifactUri = artifactUri;
		this.artifactVersion = artifactVersion;
	}

	public String getArtifactUri() {
		return artifactUri;
	}

	public String getArtifactVersion() {
		return artifactVersion;
	}

	static public VersionInfo forClass(Class cls) {
		try {
			String uri = cls.getName()
					.replace('.', '/')
					.replaceFirst("\\p{javaJavaIdentifierPart}+$", "")
					.replaceAll("\\p{javaJavaIdentifierPart}+", "..")
					.replaceFirst("$", "META-INF/MANIFEST.MF");
			URL url = cls.getResource(uri);
			VersionInfo info;
			if (url != null) {
				Manifest mf = new Manifest(url.openStream());
				info = new VersionInfo(
						mf.getMainAttributes().getValue("Artifact-URI"),
						mf.getMainAttributes().getValue("Artifact-Version"));
			} else {
				info = new VersionInfo(cls.getName(), "UNKNOWN");
			}
			return info;
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}


}
