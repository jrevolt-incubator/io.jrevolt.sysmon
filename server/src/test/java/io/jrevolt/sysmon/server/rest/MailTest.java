package io.jrevolt.sysmon.server.rest;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import io.jrevolt.sysmon.model.DomainDef;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class MailTest {

//	@Test
	public void test() throws Exception {
		Session session = Session.getDefaultInstance(new Properties());
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("psm.beno@dev.dcom.sk"));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress("patrik.beno@posam.sk"));
		msg.setSubject("Test");
		msg.setText("Test");
		Transport.send(msg);

	}


}
