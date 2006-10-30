package edu.stanford.hci.r3.paper.sheets;

import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Meters;

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
public class Test_PDFSheet {

	public static void main(String[] args) {
		// a 13 foot long PDF!
		File pdfFile = new File("data/testFiles/private/BobHorn-AvianFlu.pdf");
		Sheet sheet = new PDFSheet(pdfFile);
		System.out.println("PDF Size: " + sheet.getSize());

		ImageRegion imageRegion = new ImageRegion("Dragon", new File("data/testFiles/dragon.jpg"),
				new Meters(1), new Centimeters(30));
		sheet.addRegion(imageRegion);

		Region rectRegion = new Region("Rect1", 1, 1, 5, 5);
		sheet.addRegion(rectRegion);
		rectRegion.setActive(true);

		Region rectRegion2 = new Region("Rect2", 2, 2, 2, 2);
		sheet.addRegion(rectRegion2);
		rectRegion2.setActive(true);

		// sheet renderers do not know about PDFSheets
		SheetRenderer sr = new SheetRenderer(sheet);
		sr.renderToPDF(new File("data/testFiles/Test.pdf"));
	}
}
