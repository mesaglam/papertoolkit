package edu.stanford.hci.r3.tools;

import java.io.File;
import java.io.IOException;

import edu.stanford.hci.r3.PaperToolkit;

/**
 * <p>
 * If you run the PaperToolkit.main, you will invoke the ToolExplorer, which helps you to figure out what the
 * toolkit offers.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class ToolExplorer {

	public ToolExplorer() {
		// start the Flash Relay Server, and register our listeners...

		// Start the Apollo GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File toolExplorerApollo = new File(r3RootPath, "flash/bin/ToolExplorer.exe");

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(toolExplorerApollo.getAbsolutePath(),
					"port:" + 8989);
			processBuilder.start();
			// Process proc = processBuilder.start();
			// InputStream inputStream = proc.getInputStream();
			// BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			// InputStream errorStream = proc.getErrorStream();
			// BufferedReader err = new BufferedReader(new InputStreamReader(errorStream));
			//
			// String line;
			// DebugUtils.println("Output: ");
			// while ((line = in.readLine()) != null) {
			// DebugUtils.println(line);
			// }
			//
			// DebugUtils.println("----------------------------");
			// DebugUtils.println("Errors: ");
			// while ((line = err.readLine()) != null) {
			// DebugUtils.println(line);
			// }

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
