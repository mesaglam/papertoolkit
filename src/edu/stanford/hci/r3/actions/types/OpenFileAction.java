package edu.stanford.hci.r3.actions.types;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class OpenFileAction implements R3Action {

	/**
	 * The file to open. If this object is transmitted over the wire, an equivalent file must exist.
	 * Thus, you can use relative paths (new File("./MyFile.txt")) to get around the problem of
	 * different machine configurations.
	 */
	private File file;

	/**
	 * @param fileToOpen
	 */
	public OpenFileAction(File fileToOpen) {
		file = fileToOpen;
	}

	/**
	 * @see edu.stanford.hci.r3.actions.R3Action#invoke()
	 */
	public void invoke() {
		if (file == null || !file.exists()) {
			DebugUtils.println("Cannot open the File. Returning from invocation.");
			return;
		}
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugUtils.println("File Opened: " + file.getName());
	}

}
