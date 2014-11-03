package io.jrevolt.sysmon.agent;

import org.junit.Test;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
public class DnsTest {

	@Test
	public void test2() throws Exception {

		Resolver resolver = new SimpleResolver("10.231.79.10");
//		Lookup lookup = new Lookup("build.dcom.sk", Type.ANY);
//		Lookup lookup = new Lookup("60.79.231.10.in-addr.arpa", Type.ANY);
		Lookup lookup = new Lookup("100.79.231.10.in-addr.arpa", Type.ANY);

		lookup.setResolver(resolver);
		Record[] recs = lookup.run();
		System.out.println("Result: "+lookup.getResult());

		for (Record r : recs) {
			System.out.println(r);
		}

	}

}
