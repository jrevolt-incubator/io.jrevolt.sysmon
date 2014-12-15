package io.jrevolt.sysmon.common;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class UtilsTest {

	@Test
	public void host() {
		Assert.assertEquals("hostname", Utils.resolveHost(URI.create("https://hostname:443/path")));
	}

	@Test
	public void jdbcUriHost() {
		Assert.assertEquals("hostname", Utils.resolveHost(URI.create("jdbc:sqlserver://hostname:12345/database=dbname")));
	}

	@Test
	public void port() {
		Assert.assertEquals(443, Utils.resolvePort(URI.create("https://hostname:443/path")));
	}

	@Test
	public void defaultPort() {
		Assert.assertEquals(443, Utils.resolvePort(URI.create("https://hostname/path")));
		Assert.assertEquals(80, Utils.resolvePort(URI.create("http://hostname/path")));
	}

	@Test
	public void jdbcUriPort() {
		Assert.assertEquals(12345, Utils.resolvePort(URI.create("jdbc:sqlserver://hostname:12345/database=dbname")));
	}

}
