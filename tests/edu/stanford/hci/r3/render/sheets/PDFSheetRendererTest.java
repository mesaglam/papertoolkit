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
	public static void main(String[] args) {
		// PDFSheet sheet = new PDFSheet(new File("data/testFiles/ButterflyNetCHI2006.pdf"));
		PDFSheet sheet = new PDFSheet(new File("data/testFiles/private/BobHorn-AvianFlu.pdf"));
		// define some regions
		Region reg = new Region(77, 26.3, 9, 7.3);
		reg.setActive(true);
		sheet.addRegion(reg);
		
		PDFSheetRenderer renderer = new PDFSheetRenderer(sheet);
		renderer.renderToPDF(new File("data/testFiles/output/ExistingPDF.pdf"));
	}
}
