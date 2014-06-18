package io.jrevolt.sysmon.client.ui;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import io.jrevolt.sysmon.api.RestService;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import org.apache.commons.io.FileUtils;
import io.jrevolt.sysmon.core.AppCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ClientFrame extends Base<BorderPane> {

	@Autowired
	StandardEnvironment env;

	@Autowired
	AppCfg app;

	@Autowired
	RestService rest;

	@Override
	protected void initialize() {
		super.initialize();

		pane.setCenter(new TextArea() {{
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter(sb);
//			f.format("version: %s%n", rest.version());
			for (PropertySource<?> src : env.getPropertySources()) {
				try {
					String[] names = ((MapPropertySource) src).getPropertyNames();
					for (String name : names) {
						f.format("%s : %s%n", name, src.getProperty(name));
					}
				} catch (Exception e) {
					System.out.println(e + " : " + src);
				}
			}
			setText(sb.toString());
		}});


		try {
			JSch ssh = new JSch();
//			ssh.getHostKeyRepository().add();
//			HostKey key = new HostKey("gubuntu", null);
//			String fingerPrint = key.getFingerPrint(ssh);
			ssh.setKnownHosts("c:/users/patrik/.ssh/known_hosts");
			ssh.addIdentity("patrik@greenhorn.sk",
								 FileUtils.readFileToByteArray(new File("c:/users/patrik/.ssh/id_rsa")),
								 FileUtils.readFileToByteArray(new File("c:/users/patrik/.ssh/id_rsa.pub")),
								 null
			);
			Session session = ssh.getSession("gubuntu");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			session.setPortForwardingL(3128, "localhost", 3128);
			System.out.println("Connected. Tunnel open.");
//			ChannelExec ch = (ChannelExec) session.openChannel("exec");
//			ch.setCommand("ls -l;");
//			ch.run();
//			BufferedReader br = new BufferedReader(new InputStreamReader(ch.getInputStream()));
//			for (String s; (s = br.readLine()) != null;){
//				System.out.println(s);
//			}
			Thread.sleep(TimeUnit.HOURS.toMillis(1));
		} catch (JSchException e) {
			throw new UnsupportedOperationException(e);
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		} catch (InterruptedException e) {
			throw new UnsupportedOperationException(e);
		}

	}
}
