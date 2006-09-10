package edu.stanford.hci.r3.demos.sketch;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.render.sheets.PDFSheetRenderer;

/**
 * <p>
 * A Hello World application, that allows a user to sketch on a piece of paper and choose various
 * colors.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Sketch {

	/**
	 * 
	 */
	private static boolean renderPDFMode = false;

	/**
	 * There are two modes to the operation of this class. First, we would like to generate the
	 * patterned PDF file. Second, we would like to load in the information and run the application,
	 * along with all the event handlers. It seems like many Paper Apps will take this approach.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		PDFSheet s = new PDFSheet(new File("data/Sketch/SketchUI.pdf"));
		s.addRegions(new File("data/Sketch/SketchUI.regions.xml"));

		if (renderPDFMode) {
			renderPDF(s);
		} else {
			runApplication(s);
		}
	}

	/**
	 * Warning, Spooling and Printing these PDF files might take a while! =\ For example, on my
	 * printer, it spooled from 10:03 to... 10:07 and rasterized from 10:07 to...10:14 for the
	 * Sketch! page. This was printed from Adobe Reader 7.08.
	 * 
	 * @param s
	 */
	private static void renderPDF(PDFSheet s) {
		// print the sheet!
		// when printing, we also render a configuration file for the Paper UI
		PDFSheetRenderer renderer = new PDFSheetRenderer(s);

		// for my laser printer... calling this once makes it look better (although it still works
		// otherwise) You may want to play around to see how many times you want to call this method.
		renderer.useSmallerPatternDots();
		
		renderer.renderToPDF(new File("data/Sketch/SketchUI_Patterned.pdf"));
		renderer.savePatternInformation();
		System.out.println("Done Rendering and Saving Configuration to Disk");
		System.exit(0);
	}

	/**
	 * @param s
	 */
	private static void runApplication(PDFSheet s) {
		// check out their pattern mappings...

		// they should automatically load the .patternInfo.xml if it exists next to the PDF file

		// add some event handlers to the regions...
		for (Region r : s.getRegions()) {
			System.out.println(r);
		}
		
		Application app = new Application("Sketch!");
		app.addSheet(s);
	}
}
