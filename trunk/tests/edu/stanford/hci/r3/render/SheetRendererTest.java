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
public class SheetRendererTest {
	public static void main(String[] args) {
		sheetToJPEG();
		// sheetToPDF();
	}

	private static void sheetToJPEG() {
		Sheet sheet = SheetTest.createAndPopulateSheet();

		SheetRenderer renderer = new SheetRenderer(sheet);
		renderer.setRenderActiveRegionsWithPattern(false);

		renderer.renderToJPEG(new File("testData/Test.jpg"));
	}

	private static void sheetToPDF() {
		Sheet sheet = SheetTest.createAndPopulateSheet();

		SheetRenderer renderer = new SheetRenderer(sheet);
		renderer.setRenderActiveRegionsWithPattern(false);

		renderer.renderToPDFWithIText(new File("testData/Test.pdf"));
	}
}
