package papertoolkit.render;

import java.io.File;
import java.util.List;

import papertoolkit.paper.Bundle;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.TiledPatternGenerator;
import papertoolkit.util.DebugUtils;


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
		final List<Sheet> sheets = bundle.getSheets();
		final TiledPatternGenerator sharedPatternGenerator = new TiledPatternGenerator();
		int n = 0;
		for (Sheet s : sheets) {
			final File destFile = new File(parentPath, fileNameWithoutExtension + "_" + n + ".pdf");
//			DebugUtils.println("Rendering " + destFile);
			final SheetRenderer sr = new SheetRenderer(s, sharedPatternGenerator);
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
