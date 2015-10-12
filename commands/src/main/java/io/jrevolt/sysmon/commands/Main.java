package io.jrevolt.sysmon.commands;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
public class Main {

	public static void main(String[] args) throws Exception {
		Runnable r = (Runnable) Class.forName(System.getProperty("sysmon.agent.command")).newInstance();
		r.run();
	}

}
