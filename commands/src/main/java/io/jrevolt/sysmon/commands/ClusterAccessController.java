package io.jrevolt.sysmon.commands;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class ClusterAccessController {

	void run(boolean isAccessAllowed) {
		try {
			File fin = new File("/etc/ssh/sshd_config");
			List<String> src = Files.readAllLines(fin.toPath());

			Pattern pAllowGroups = Pattern.compile("#*\\s*(AllowGroups\\s.*)");
			Pattern pAllowUsers = Pattern.compile("#*\\s*(AllowUsers\\s.*)");

			StringBuilder sb = new StringBuilder();
			List<String> dst = src.stream().map(s -> {
				sb.setLength(0);
				Matcher m;
				if ((m = pAllowGroups.matcher(s)).matches()) {
					if (!isAccessAllowed) { sb.append("#"); }
					sb.append(m.group(1));
				} else if ((m = pAllowUsers.matcher(s)).matches()) {
					if (isAccessAllowed) { sb.append("#"); }
					sb.append(m.group(1));
				} else {
					sb.append(s);
				}
				return sb.toString();
			}).collect(Collectors.toList());

			File fout = new File(fin.getParentFile(), UUID.randomUUID().toString());
			Files.write(fout.toPath(), dst);

			File backup = new File(fin.getParentFile(), UUID.randomUUID().toString());
			fin.renameTo(backup);

			fout.renameTo(fin);

			CommandLine cmdline = CommandLine.parse("sudo")
					.addArgument("mvnlauncher.sh")
					.addArgument("io.jrevolt.sysmon:io.jrevolt.sysmon.command:develop-SNAPSHOT")
					.addArgument("--sysmon.agent.command=io.jrevolt.sysmon.commands.ClusterAccessController")
					.addArgument("--isAccessAllowed="+isAccessAllowed);
			DefaultExecutor executor = new DefaultExecutor();
			executor.execute(cmdline);

		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
