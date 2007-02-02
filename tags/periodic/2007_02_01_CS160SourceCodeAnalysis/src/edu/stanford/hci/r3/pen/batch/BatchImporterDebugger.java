package edu.stanford.hci.r3.pen.batch;

import java.io.File;

import javax.swing.JFileChooser;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Point it to a file to "reimport" the data, as if you just plugged in the pen.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchImporterDebugger {

	public static void main(String[] args) {
		PaperToolkit.initializeLookAndFeel();
		// calls the BatchImporter with a path to a file, to simulate a pen synch...
		JFileChooser chooser = FileUtils.createNewFileChooser(new String[] { "xml" });
		final File r3Root = PaperToolkit.getToolkitRootPath();
		chooser.setCurrentDirectory(new File(r3Root, "penSynch/data/XML"));
		chooser.setDialogTitle("Import an XML File");
		chooser.setMultiSelectionEnabled(true);

		final int showDialogResult = chooser.showDialog(null, "Import");
		if (showDialogResult == JFileChooser.APPROVE_OPTION) {
			for (File f : chooser.getSelectedFiles()) {
				// DebugUtils.println(f.getAbsoluteFile());
				
				// Import the chosen file
				BatchImporter.main(new String[] { f.getAbsolutePath() });
			}
		}

	}
}
