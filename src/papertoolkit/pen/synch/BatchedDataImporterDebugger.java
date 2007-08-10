package papertoolkit.pen.synch;

import java.io.File;

import javax.swing.JFileChooser;

import papertoolkit.PaperToolkit;
import papertoolkit.util.files.FileUtils;


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
public class BatchedDataImporterDebugger {

	public static void main(String[] args) {
		PaperToolkit.initializeLookAndFeel();
		
		// calls the BatchedDataImporter with a path to a file, to simulate a pen synch...
		// normally, the C# Pen Monitor does this...
		// manually choose an xml file
		final JFileChooser chooser = FileUtils.createNewFileChooser(new String[] { "xml" });
		final File r3Root = PaperToolkit.getToolkitRootPath();
		
		// look in the default data directory
		chooser.setCurrentDirectory(new File(r3Root, "penSynch/data/XML"));
		chooser.setDialogTitle("Import an XML File");
		chooser.setMultiSelectionEnabled(true);

		final int showDialogResult = chooser.showDialog(null, "Import");
		if (showDialogResult == JFileChooser.APPROVE_OPTION) {
			for (File f : chooser.getSelectedFiles()) {
				// DebugUtils.println(f.getAbsoluteFile());
				
				// Import the chosen file
				BatchedDataImporter.main(new String[] { f.getAbsolutePath() });
			}
		}

	}
}
