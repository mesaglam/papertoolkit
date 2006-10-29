package edu.stanford.hci.r3.render;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.paper.Bundle;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Renders bundles of sheets.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BundleRenderer {

	private Bundle bundle;

	public BundleRenderer(Bundle b) {
		bundle = b;
	}

	/**
	 * Names them X1.pdf, X2.pdf, X3.pdf, etc...
	 * 
	 * @param parentPath
	 * @param fileNameWithoutExtension
	 */
	public void renderToIndividualPDFs(File parentPath, String fileNameWithoutExtension) {
		List<Sheet> sheets = bundle.getSheets();
		TiledPatternGenerator sharedPatternGenerator = new TiledPatternGenerator();
		int n = 0;
		for (Sheet s : sheets) {
			final File destFile = new File(parentPath, fileNameWithoutExtension + "_" + n + ".pdf");
			DebugUtils.println(destFile);
			SheetRenderer sr = new SheetRenderer(s, sharedPatternGenerator);
			sr.renderToPDF(destFile);
			n++;
		}
	}

	/**
	 * Renders a Multi-Page PDF!
	 * 
	 * @param destPDFFile
	 */
	public void renderToPDF(File destPDFFile) {
		
	}

}
