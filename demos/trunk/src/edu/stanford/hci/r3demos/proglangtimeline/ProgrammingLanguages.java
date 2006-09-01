package edu.stanford.hci.r3demos.proglangtimeline;

import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.render.sheets.PDFSheetRenderer;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * A GIGAprint with a Programming Languages timeline by O'Reilly.
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ProgrammingLanguages {

	public static void main(String[] args) {
		final File file = new File("data/testFiles/private/ProgrammingLanguages.pdf");
		PDFSheet sheet = new PDFSheet(file);

		final double delta = 1.42;

		// define some regions
		for (int i = 0; i < 18; i++) {
			Region reg = new Region(13.34 + (i * delta), 15.43, 0.8, 1.2);
			reg.setActive(true);
			sheet.addRegion(reg);
		}

		Region reg = new Region(19.09, 3.19 - .085/* ~half of height */, 11.11, .22);
		reg.setActive(true);
		sheet.addRegion(reg);

		PDFSheetRenderer renderer = new PDFSheetRenderer(sheet);
		renderer.renderToPDF(new File("data/testFiles/output/ProgrammingLanguages_Patterned.pdf"));

	}
}
