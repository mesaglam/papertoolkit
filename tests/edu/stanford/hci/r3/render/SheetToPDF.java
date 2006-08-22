package edu.stanford.hci.r3.render;

import java.io.File;

import edu.stanford.hci.r3.core.Sheet;
import edu.stanford.hci.r3.core.SheetTest;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Turns a Sheet object into a PDF file.
 */
public class SheetToPDF {
	public static void main(String[] args) {
		Sheet sheet = SheetTest.createAndPopulateSheet();

		SheetRenderer renderer = new SheetRenderer(sheet);
		renderer.setRenderActiveRegionsWithPattern(false);

		renderer.renderToPDF(new File("testData/Test.pdf"));
	}
}
