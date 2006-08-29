package edu.stanford.hci.r3.render.sheets;

import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PDFSheetRendererTest {
	/**
	 * 
	 */
	private static void addRegionToExistingPDF() {
		// PDFSheet sheet = new PDFSheet(new File("data/testFiles/ButterflyNetCHI2006.pdf"));
		PDFSheet sheet = new PDFSheet(new File("data/testFiles/private/AvianFluTimeline.pdf"));
		// define some regions
		Region reg = new Region(77, 26.3, 9, 7.3);
		reg.setActive(true);
		sheet.addRegion(reg);

		PDFSheetRenderer renderer = new PDFSheetRenderer(sheet);
		renderer.renderToPDF(new File("data/testFiles/output/AvianFluTimeline.pdf"));
	}

	public static void main(String[] args) {
		addRegionToExistingPDF();
	}

	private static void createJPEGFromPDF() {
		// render a jpeg from a PDF!
		PDFSheet sheet = new PDFSheet(new File("data/testFiles/private/AvianFluMessMap.pdf"));

		// define some regions
		Region reg = new Region(0, 0, 3, 3);
		reg.setActive(true);
		sheet.addRegion(reg);

		PDFSheetRenderer renderer = new PDFSheetRenderer(sheet);
		renderer.renderToPDF(new File("data/testFiles/output/AvianFluMessMap.pdf"));
		renderer.renderToJPEG(new File("data/testFiles/output/AvianFluMessMap.jpg"));
	}
}
