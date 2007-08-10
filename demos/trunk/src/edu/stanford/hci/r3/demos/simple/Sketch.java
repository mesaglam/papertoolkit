package edu.stanford.hci.r3.demos.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.events.handlers.GestureHandler;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.pen.Pen;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.render.sheets.PDFSheetRenderer;
import papertoolkit.tools.components.InkPanel;
import papertoolkit.util.WindowUtils;


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

	/**
	 * Either sends us into the rendering mode, or application (runtime) mode.
	 * 
	 * TODO: In the future, the PaperToolkit's application manager will enable printing. Thus, this
	 * flag will not be necessary. An application author will subclass Application (which will have
	 * an empty renderToPDF function).
	 */
	private static final int MODE_DESIGN_PDF = 0;

	private static final int MODE_RENDER_PDF = 1;

	private static final int MODE_RUN_APP = 2;

	private static final Color ORANGE = new Color(255, 153, 51, 220);

	private static final Color PURPLE = new Color(128, 0, 128, 220);

	/**
	 * There are two modes to the operation of this class. First, we would like to generate the
	 * patterned PDF file. Second, we would like to load in the information and run the application,
	 * along with all the event handlers. tIt seems like many Paper Apps will take this approach.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// new Sketch(MODE_DESIGN_PDF);
		// new Sketch(MODE_RENDER_PDF);
		new Sketch(MODE_RUN_APP);
	}

	private JPanel colorPanel;

	private Color currentColor = BLACK;

	private JFrame inkDisplay;

	/**
	 * Renders Ink
	 */
	private InkPanel inkPanel;

	/**
	 * Captures ink from the streaming pen.
	 */
	private InkHandler inkWell;

	private JPanel mainPanel;

	/**
	 * A Sheet object that encapsulates an existing PDF file (designed in some other tool).
	 */
	private PDFSheet sheet;

	private JPanel statusPanel;

	private JLabel statusText;

	/**
	 * 
	 */
	public Sketch(int mode) {
		// three branches.... we do this, for now...
		if (mode == MODE_DESIGN_PDF) {
			PaperToolkit.startAcrobatDesigner();
		} else {
			sheet = new PDFSheet(new File("data/Sketch/SketchUI.pdf"));

			// regions should be added before you run the app (i.e., add the sheet to an
			// application, etc...)
			sheet.addRegions(new File("data/Sketch/SketchUI.regions.xml"));

			if (mode == MODE_RENDER_PDF) {
				renderPDF();
			} else if (mode == MODE_RUN_APP) {
				runApplication();
			}
		}
	}

	/**
	 * @param sheet
	 */
	private void addEventHandlersToRegions() {
		// System.out.println("Regions: ");
		// for (Region r : s.getRegions()) {
		// System.out.println("\t" + r.getName());
		// }
		// System.out.println();

		// address regions by name (stored in the xml file)
		// add some event handlers to the regions...
		Region regionMain = sheet.getRegion("MainDrawingArea");
		inkWell = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				statusText.setText("Writing... " + inkWell.getNumStrokesCollected()
						+ " strokes have been collected");

				// DEMO:
				// TASK: Modify this method so that ink is updated immediately....

			}
		};
		regionMain.addEventHandler(inkWell);

		Region regionBlack = sheet.getRegion("BlackPalette");
		regionBlack.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked Black " + clickCount + " times in a row.");
				currentColor = BLACK;
				colorPanel.setBackground(currentColor);
			}
		});

		Region regionPurple = sheet.getRegion("PurplePalette");
		regionPurple.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked Purple " + clickCount + " times in a row.");
				currentColor = PURPLE;
				colorPanel.setBackground(currentColor);
			}
		});

		Region regionOrange = sheet.getRegion("OrangePalette");
		regionOrange.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked Orange " + clickCount + " times in a row.");
				currentColor = ORANGE;
				colorPanel.setBackground(currentColor);
			}
		});

		// wouldn't it be cool if you could copy a region in acrobat
		// and paste it here, and it would become the code??? (FUTURE TODO)
		Region regionSendToScreen = sheet.getRegion("MarkingMenu");
		regionSendToScreen.addEventHandler(new GestureHandler() {
			@Override
			public void handleMark(PenEvent e, GestureDirection dir) {
				System.out.println("Mark Direction: " + dir);
				switch (dir) {
				case E: // save to file...
					System.out.println("Saving....");
					inkWell.saveInkToXMLFile(new File("data/Sketch/InkOutput.xml"));
					break;
				case N: // send to screen...
				case NW: // whenever you stroke up
				case NE: // or close to it
					updateInkToScreen();
					break;
				case S: // clear the screen!
				case SE:
					System.out.println("Clearing....");
					inkPanel.clear();
					inkWell.clear();
					break;
				case W: // undo the last stroke
				case SW:
					System.out.println("Undoing....");
					inkPanel.removeLastBatchOfInk();
					break;
				}
			}

		});
	}

	/**
	 * @return
	 */
	private JFrame getInkDisplay() {
		if (inkDisplay == null) {
			inkDisplay = new JFrame("Sketch! Display");
			inkDisplay.setContentPane(getMainPanel());
			inkDisplay.setSize(690, 740);
			inkDisplay.setLocation(WindowUtils.getWindowOrigin(inkDisplay,
					WindowUtils.DESKTOP_CENTER));
			inkDisplay.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			inkDisplay.setVisible(true);
		}
		return inkDisplay;
	}

	/**
	 * @return
	 */
	private Container getInkPanel() {
		if (inkPanel == null) {
			inkPanel = new InkPanel();
		}
		return inkPanel;
	}

	/**
	 * @return
	 */
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getInkPanel(), BorderLayout.CENTER);
			mainPanel.add(getStatusPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	/**
	 * @return
	 */
	private Component getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new JPanel();
			statusPanel.setLayout(new BorderLayout());
			colorPanel = new JPanel();
			colorPanel.setBackground(currentColor);
			colorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			statusText = new JLabel("Start Sketching!");
			statusText.setFont(new Font("Tahoma", Font.PLAIN, 18));
			statusText.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			statusPanel.add(colorPanel, BorderLayout.WEST);
			statusPanel.add(statusText, BorderLayout.CENTER);
		}
		return statusPanel;
	}

	/**
	 * Warning, Spooling and Printing these PDF files might take a while! =\ For example, on my
	 * printer, it spooled from 10:03 to... 10:07 and rasterized from 10:07 to...10:14 for the
	 * Sketch! page. This was printed from Adobe Reader 7.08.
	 * 
	 * @param s
	 */
	private void renderPDF() {
		// print the sheet!
		// when printing, we also render a configuration file for the Paper UI
		PDFSheetRenderer renderer = new PDFSheetRenderer(sheet);

		// for my laser printer... calling this once makes it look better (although it still works
		// otherwise) You may want to play around to see how many times you want to call this
		// method.
		renderer.useSmallerPatternDots();

		renderer.renderToPDF(new File("data/Sketch/SketchUI_Patterned.pdf"));
		renderer.savePatternInformation();
		System.out.println("Done Rendering and Saving Configuration to Disk");
	}

	/**
	 * @param sheet
	 */
	private void runApplication() {
		// Our main paper application
		// an application contains one or more sheets
		Application app = new Application("Sketch!");
		app.addSheet(sheet);

		// the application has to know about this pen
		Pen pen = new Pen("Main Pen");
		app.addPenInput(pen);

		// we need to add code that actually does stuff to each region
		addEventHandlersToRegions();

		// show the ink display
		getInkDisplay();

		// Ask the toolkit to load and start the application
		PaperToolkit r3 = new PaperToolkit();
		r3.startApplication(app);
	}

	/**
	 * 
	 */
	private void updateInkToScreen() {
		Ink ink = inkWell.getNewInkOnly();
		if (ink.getNumStrokes() > 0) {
			ink.setColor(currentColor);
			inkPanel.addInk(ink);
		} else {
			System.out.println("Zero Strokes");
		}
	}
}
