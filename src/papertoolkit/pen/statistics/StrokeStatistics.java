package papertoolkit.pen.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.synch.PenSynch;
import papertoolkit.pen.synch.PenSynchAnotoDissect;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * Reads in a bunch of synched files and calculates statistics!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StrokeStatistics {
	public static void main(String[] args) {

		// point it to a directory
		String path = "C:/Documents and Settings/Ron Yeh/Desktop/InkStrokeStatistics";

		// for each directory...
		List<File> dirs = FileUtils.listVisibleDirs(new File(path));

		int globalPageCount = 0;
		int globalStrokeCount = 0;
		
		for (File dir : dirs) {
			List<File> xmlFiles = FileUtils.listVisibleFiles(dir, "xml");

			int dirPageCount = 0;
			int dirStrokeCount = 0;
			
			for (File xmlFile : xmlFiles) {
				DebugUtils.println(xmlFile.getAbsolutePath());

				int pageCount = 0;
				int strokeCount = 0;
				
				List<Ink> importedInk = new ArrayList<Ink>();
				
				// read in an xml file
				if (xmlFile.getName().endsWith(".bin.xml")) {
					// anoto dissect xml file
					PenSynchAnotoDissect anotoDissect = new PenSynchAnotoDissect(xmlFile);
					importedInk = anotoDissect.getImportedInk();
				} else {
					// regular pen synch
					PenSynch regularSynch = new PenSynch(xmlFile);
					importedInk = regularSynch.getImportedInk();
				}
				for (Ink ink : importedInk) {
					pageCount++;
					// count the number of strokes
					strokeCount += ink.getNumStrokes();
				}
				DebugUtils.println("Stats: " + pageCount + " pages, and " + strokeCount + " strokes.");
				dirPageCount += pageCount;
				dirStrokeCount += strokeCount;
			}

			DebugUtils.println("This Directory Contained " + dirPageCount + " pages and " + dirStrokeCount + " strokes: " + dir.getName());
			DebugUtils.println("\n");
			
			// count the number of unique days
			// display the unique days... so we know where we can split it!
			
			globalPageCount += dirPageCount;
			globalStrokeCount += dirStrokeCount;
		}

		// count the number of "clusters that are separated by > 5 minute gaps"

		// spit out the statistics...

		DebugUtils.println("In total, we read in " + globalPageCount + " pages and " + globalStrokeCount + " ink strokes.");
	}
}
