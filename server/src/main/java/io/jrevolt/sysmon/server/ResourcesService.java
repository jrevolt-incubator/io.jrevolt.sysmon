package io.jrevolt.sysmon.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Path("resources")
public class ResourcesService {

	@GET @Path("index.html")	@Produces("text/html")
	public InputStream index() throws IOException {
		return getClass().getResource("index.html").openStream();
	}

	@GET @Path("index.jnlp")	@Produces("application/x-java-jnlp-file")
	public InputStream jnlp() throws Exception {
		InputStream in = getClass().getResource("index.jnlp").openStream();
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		Document doc = db.parse(in);
		return in;
	}

	@GET @Path("index.gif")	@Produces("image/gif")
	public InputStream splash() throws IOException {
		return getClass().getResource("index.gif").openStream();
	}

	@GET @Path("browser.jar") @Produces("application/java-archive")
	public InputStream resource() throws Exception {
		return new File("c:\\users\\patrik\\projects\\io.jrevolt\\io.jrevolt.sysmon\\.build\\io.jrevolt.sysmon.browser\\libs\\io.jrevolt.sysmon.browser-integration-SNAPSHOT.jar")
				.toURI().toURL().openStream();
	}

	static public class Classpath {
		List<URL> urls;

		public Classpath() {
		}

		public Classpath(List<URL> urls) {
			this.urls = urls;
		}

		public List<URL> getUrls() {
			return urls;
		}

		public void setUrls(List<URL> urls) {
			this.urls = urls;
		}
	}

//	@GET @Path("classpath") @Produces("application/json")
//	public Classpath classpath() {
//		final List<Archive> archives = new ArrayList<>();
//		MvnLauncher l = new MvnLauncher() {
//			@Override
//			protected List<Archive> getClassPathArchives(MvnArtifact mvnartifact, List<MvnArtifact> ext) throws Exception {
//				archives.addAll(super.getClassPathArchives(mvnartifact, ext));
//				return archives;
//			}
//		};
//		l.resolve(MvnArtifact.parse("io.jrevolt.sysmon:io.jrevolt.sysmon.client:integration-SNAPSHOT"), null);
//		try {
//			final List<URL> urls = new ArrayList<>();
//			for (Archive archive : archives) {
//				urls.add(archive.getUrl());
//			}
//			return new Classpath(urls);
//		} catch (MalformedURLException e) {
//			throw new UnsupportedOperationException(e);
//		}
//	}


//	@GET @Path("{path}")
//	public InputStream resource(@PathParam("path") String path) {
//
//	}


}
