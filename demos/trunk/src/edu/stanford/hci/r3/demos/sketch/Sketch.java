package edu.stanford.hci.r3.demos.sketch;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.pen.Pen;
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
		// otherwise) You may want to play around to see how many times you want to call this
		// method.
		renderer.useSmallerPatternDots();

		renderer.renderToPDF(new File("data/Sketch/SketchUI_Patterned.pdf"));
		renderer.savePatternInformation();
		System.out.println("Done Rendering and Saving Configuration to Disk");
	}

	/**
	 * @param s
	 */
	private static void runApplication(PDFSheet s) {
		// add some event handlers to the regions...
		System.out.println("Regions: ");
		for (Region r : s.getRegions()) {
			System.out.println("\t" + r.getName());
		}
		System.out.println();

		Region region = s.getRegion("MainDrawingArea");
		System.out.println(region);

		Application app = new Application("Sketch!");
		app.addSheet(s);

		Pen pen = new Pen("Main Pen");
		pen.startLiveMode(); // the pen is attached to the local machine
		// if you are adventurous, you can add listeners DIRECTLY to the pen
		// however, that's too low level for us, so we'll add listeners to the regions instead

		app.addPen(pen);

		PaperToolkit r3 = new PaperToolkit();
		r3.startApplication(app);
	}
}
