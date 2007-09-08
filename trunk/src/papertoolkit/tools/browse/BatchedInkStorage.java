package papertoolkit.tools.browse;

import java.io.File;


/**
 * <p>
 * Allows you to read Ink objects from the XML files stored on disk, in the penSynch data directory.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class BatchedInkStorage {
	
	
	private static final String xmlPath = "penSynch\\data\\XML";
	
	public static void main(String[] args) {
		File f = new File(xmlPath);
		// DebugUtils.println(f.getAbsolutePath());
	}
	
}
