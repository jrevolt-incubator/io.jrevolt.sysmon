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
		URI uri = URI.create("jdbc:sqlserver://user:password@lbdbcl12.intra.dcom.sk:62905;database=egov");
		Assert.assertEquals("lbdbcl12.intra.dcom.sk", Utils.resolveHost(uri));
	}

	@Test
	public void port() {
		Assert.assertEquals(123, Utils.resolvePort(URI.create("https://hostname:123/path")));
		Assert.assertEquals(123, Utils.resolvePort(URI.create("jar:https://hostname:123/path")));
		Assert.assertEquals(123, Utils.resolvePort(URI.create("tcp://hostname:123/path")));
		Assert.assertEquals(123, Utils.resolvePort(URI.create("tcp://hostname:123")));
//		Assert.assertEquals(123, Utils.resolvePort(URI.create("tcp:hostname:123"))); // todo unsupported yet
	}

	@Test
	public void defaultPort() {
		Assert.assertEquals(443, Utils.resolvePort(URI.create("https://hostname/path")));
		Assert.assertEquals(80, Utils.resolvePort(URI.create("http://hostname/path")));
	}

	@Test
	public void jdbcUriPort() {
		URI uri = URI.create("jdbc:sqlserver://user:password@lbdbcl12.intra.dcom.sk:62905;database=egov");
		Assert.assertEquals(62905, Utils.resolvePort(uri));
	}

}
