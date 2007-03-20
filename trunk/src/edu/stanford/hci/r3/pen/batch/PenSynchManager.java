package edu.stanford.hci.r3.pen.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;
import edu.stanford.hci.r3.util.files.SortDirection;

/**
 * <p>
 * Helps us figure out what has been synched, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PenSynchManager {

	private File penSynchDataPath;
	private List<File> xmlFiles;

	public PenSynchManager() {
		// get the XML data path, and check to see if there are any new files since we last checked.
		// All we do is maintain a modification time of the last file we have processed, and we
		// process any xml file that is newer than this lastModified date.
		penSynchDataPath = PaperToolkit.getPenSynchDataPath();

		// get list of XML files in the penSynch directory
		xmlFiles = FileUtils.listVisibleFiles(penSynchDataPath, new String[] { "XML" });

		// DebugUtils.println(xmlFiles);
	}

	/**
	 * Return this in a sorted list, from oldest to newest.
	 * 
	 * @param lastModifiedTimestamp
	 * @return
	 */
	public List<File> getFilesNewerThan(long lastModifiedTimestamp) {
		List<File> newFiles = new ArrayList<File>();
		for (File f : xmlFiles) {
			if (f.lastModified() > lastModifiedTimestamp) {
				// DebugUtils.println(f.lastModified());
				newFiles.add(f);
				FileUtils.sortByLastModified(newFiles, SortDirection.OLD_TO_NEW);
			}
		}
		return newFiles;
	}
}
