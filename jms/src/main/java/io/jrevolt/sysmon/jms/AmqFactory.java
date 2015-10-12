//package io.jrevolt.sysmon.jms;
//
//import org.apache.activemq.transport.Transport;
//import org.apache.activemq.transport.amqp.AmqpTransportFactory;
//import org.apache.activemq.wireformat.WireFormat;
//
//import java.util.Map;
//
///**
// * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
// * @version $Id$
// */
//public class AmqFactory extends AmqpTransportFactory {
//	@Override
//	public Transport compositeConfigure(Transport transport, WireFormat format, Map options) {
//		options.remove("wireFormat.host");
//		return super.compositeConfigure(transport, format, options);
//	}
//}
