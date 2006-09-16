package edu.stanford.hci.r3.demos.avianflu;

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
 * A GIGAprint with a Programming Languages timeline by O'Reilly. See <a
 * href="http://www.oreilly.com/pub/a/oreilly/news/languageposter_0504.html">the O'Reilly Poster</a>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class MessMapGIGAprint {

	public static void main(String[] args) {
		final File file = new File("data/AvianFlu/AvianFluMessMap.pdf");
		PDFSheet sheet = new PDFSheet(file);

		Region reg = new Region("Table", 36, 2, 4, 2);
		reg.setActive(true);
		sheet.addRegion(reg);

		PDFSheetRenderer renderer = new PDFSheetRenderer(sheet);
		renderer.renderToPDF(new File("data/AvianFlu/AvianFluMessMap_Output.pdf"));
	}
}
