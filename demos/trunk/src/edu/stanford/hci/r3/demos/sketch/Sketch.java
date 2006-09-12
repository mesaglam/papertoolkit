package edu.stanford.hci.r3.demos.sketch;

import java.awt.Color;
import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.filters.InkContainer;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.events.handlers.GestureHandler;
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

	private static final Color BLACK = new Color(0, 0, 0, 220);

	private static final Color ORANGE = new Color(255, 153, 51, 220);

	private static final Color PURPLE = new Color(128, 0, 128, 220);

	/**
	 * There are two modes to the operation of this class. First, we would like to generate the
	 * patterned PDF file. Second, we would like to load in the information and run the application,
	 * along with all the event handlers. It seems like many Paper Apps will take this approach.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Sketch();
	}

	private Color currentColor = BLACK;

	/**
	 * Either sends us into the rendering mode, or application (runtime) mode.
	 * 
	 * TODO: In the future, the PaperToolkit's application manager will enable printing. Thus, this
	 * flag will not be necessary. An application author will subclass Application (which will have
	 * an empty renderToPDF function).
	 */
	private boolean renderPDFMode = false;

	private PDFSheet sheet;

	/**
	 * 
	 */
	public Sketch() {
		sheet = new PDFSheet(new File("data/Sketch/SketchUI.pdf"));
		sheet.addRegions(new File("data/Sketch/SketchUI.regions.xml"));

		if (renderPDFMode) {
			renderPDF(sheet);
		} else {
			runApplication(sheet);
		}
	}

	/**
	 * Warning, Spooling and Printing these PDF files might take a while! =\ For example, on my
	 * printer, it spooled from 10:03 to... 10:07 and rasterized from 10:07 to...10:14 for the
	 * Sketch! page. This was printed from Adobe Reader 7.08.
	 * 
	 * @param s
	 */
	private void renderPDF(PDFSheet s) {
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
	private void runApplication(PDFSheet s) {
		// add some event handlers to the regions...
		// System.out.println("Regions: ");
		// for (Region r : s.getRegions()) {
		// System.out.println("\t" + r.getName());
		// }
		// System.out.println();

		Region regionMain = s.getRegion("MainDrawingArea");
		final InkContainer inkContainer = new InkContainer();
		regionMain.addEventFilter(inkContainer);
		
		Region regionBlack = s.getRegion("BlackPalette");
		regionBlack.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked Black " + clickCount + " times in a row.");
				currentColor = BLACK;
			}
		});

		Region regionPurple = s.getRegion("PurplePalette");
		regionPurple.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked Purple " + clickCount + " times in a row.");
				currentColor = PURPLE;
			}
		});

		Region regionOrange = s.getRegion("OrangePalette");
		regionOrange.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked Orange " + clickCount + " times in a row.");
			}
		});

		// wouldn't it be cool if you could copy a region in acrobat
		// and paste it here, and it would become the code??? (FUTURE TODO)
		Region regionSendToScreen = s.getRegion("SendToScreen");
		regionSendToScreen.addEventHandler(new GestureHandler() {
			@Override
			public void handleMark(PenEvent e, GestureDirection dir) {
				System.out.println("Mark Direction: " + dir);
			}
		});

		Application app = new Application("Sketch!");
		app.addSheet(s);

		Pen pen = new Pen("Main Pen");
		pen.startLiveMode(); // the pen is attached to the local machine
		// if you are adventurous, you can add listeners DIRECTLY to the pen
		// however, that's too low level for us, so we'll add listeners to the regions instead

		app.addPen(pen);

		// this should add a task to the start bar, so that a user can turn off the application if
		// necessary...
		PaperToolkit r3 = new PaperToolkit();
		// r3.useApplicationManager(false); // only if you want to hide it...
		r3.startApplication(app);
	}
}
