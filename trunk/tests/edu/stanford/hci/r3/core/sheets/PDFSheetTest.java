package edu.stanford.hci.r3.core.sheets;

import java.io.File;

import edu.stanford.hci.r3.core.Sheet;
import edu.stanford.hci.r3.core.regions.ImageRegion;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * When calling renderToJPEG(...), this example takes at least 512MB of RAM (allocated as VM heap
 * space). Use the -Xmx512M argument to your java VM. The PDF rendering calls are much more
 * efficient. That is probably because we didn't implement them. ;)
 */
public class PDFSheetTest {

	public static void main(String[] args) {
		// a 13 foot long PDF!
		File pdfFile = new File("testData/BobHorn-AvianFlu.pdf");
		Sheet sheet = new PDFSheet(pdfFile);
		System.out.println(sheet.getSize());
		sheet.addRegion(new ImageRegion(new File("testData/dragon.jpg"), new Inches(1),
				new Centimeters(3)));

		SheetRenderer sr = new SheetRenderer(sheet);
		sr.renderToPDF(new File("testData/Test.pdf"));
	}
}
