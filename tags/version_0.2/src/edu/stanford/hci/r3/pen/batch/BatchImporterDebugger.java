package edu.stanford.hci.r3.pen.batch;

/**
 * <p>
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
		// calls the BatchImporter with a path to a file, to simulate a pen synch...
		// String filePath = "C:\\Documents and Settings\\Ron Yeh\\My
		// Documents\\Projects\\PaperToolkit\\penSynch\\data\\XML\\2006_09_17__03_38_17.xml";
		String filePath = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\PaperToolkit"
				+ "\\penSynch\\data\\XML\\2006_09_17__04_39_54.xml";
		BatchImporter.main(new String[] { filePath });
	}
}
