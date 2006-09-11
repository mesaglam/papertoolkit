package edu.stanford.hci.r3.actions.types;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * Run a local file, that is either in the system PATH, or addressed by an absolute path.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RunAppAction implements R3Action {

	private String executablePath;

	public RunAppAction(File execFile) {
		executablePath = execFile.getPath();
	}

	/**
	 * Use this for running apps that are in the System PATH.
	 * 
	 * @param execName
	 */
	public RunAppAction(String execName) {
		executablePath = execName;
	}

	/**
	 * @see edu.stanford.hci.r3.actions.R3Action#invoke()
	 */
	public void invoke() {
		try {
			File f = new File(executablePath);
			String command = null;
			if (!f.exists()) {
				// this probably means it is in the path, instead of in the local directory
				command = f.getName();
			} else {
				// run this file!
				command = f.getAbsolutePath();
			}

			ProcessBuilder builder = new ProcessBuilder(command);
			Map<String, String> env = builder.environment();
			final String envPath = env.get("PATH");
			String append = null;
			if (envPath == null) {
				append = "";
			} else {
				append = System.getProperty("path.separator") + envPath;
			}
			env.put("PATH", System.getProperty("java.library.path") + append);
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
