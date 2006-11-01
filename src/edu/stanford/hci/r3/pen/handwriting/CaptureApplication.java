package edu.stanford.hci.r3.pen.handwriting;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.events.filters.HandwritingRecognizer;
import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.PenAdapter;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This paper application demonstrates:
 * <ul>
 * <li>Handwriting Recognition as a Content Filter
 * <li>Creating Regions and Sheets at RUNTIME, from arbitrary patterned paper
 * </ul>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CaptureApplication extends Application {

	/**
	 * The bottom right calibration point.
	 */
	private PenSample anchorPointBottomRight;

	/**
	 * The top left calibration point.
	 */
	private PenSample anchorPointTopLeft;

	/**
	 * Allows us to update the GUI.
	 */
	private HandwritingCaptureDebugger gui;

	/**
	 * The Content Filter attached to the inkable region.
	 */
	private HandwritingRecognizer handwritingRecognizer;

	/**
	 * An Ink Collector attached to the SAME inkable region.
	 */
	private InkCollector inkCollector;

	/**
	 * We will use one sheet at a time to test handwriting recognition.
	 */
	private Sheet mainSheet;

	/**
	 * We will only allow one pen in this testing environment.
	 */
	private Pen pen;

	/**
	 * Start the App with Zero Sheets. Add them interactively.
	 * 
	 * @param debugger
	 */
	public CaptureApplication(HandwritingCaptureDebugger debugger) {
		super("Handwriting Capture");
		addPen(getPen());
		gui = debugger;
	}

	/**
	 * Create a Region by tapping two points.
	 */
	public void addCalibrationHandler() {
		anchorPointTopLeft = null;
		anchorPointBottomRight = null;

		final Pen pen = getPen();
		pen.addLivePenListener(new PenAdapter() {
			public void penUp(PenSample sample) {
				if (anchorPointTopLeft == null) {
					anchorPointTopLeft = sample;
					DebugUtils.println("Top Left Point is now set to " + anchorPointTopLeft);
					gui.showTopLeftPointConfirmation();
				} else if (anchorPointBottomRight == null) {
					anchorPointBottomRight = sample;
					DebugUtils
							.println("Bottom Right Point is now set to " + anchorPointBottomRight);
					gui.showBottomRightPointConfirmation();
					scaleInkPanelToFit();
					addOneSheetAndOneRegionForHandwritingCapture();

					// after setting the bottom right point, we should remove this pen listener...
					final PenListener listener = this;
					// We must modify the listeners from an external thread, as we are
					// currently iterating through it
					// This will happen after we have released the lock
					new Thread(new Runnable() {
						public void run() {
							pen.removeLivePenListener(listener);
						}
					}).start();
				}
			}

		});
	}

	/**
	 * Add a sheet and a region at RUNTIME.
	 */
	private void addOneSheetAndOneRegionForHandwritingCapture() {
		// ask the event engine to remove our sheet and our mappings, if they exist
		if (mainSheet != null) {
			DebugUtils.println("Removing old region...");

			// remove the sheet from this application
			removeSheet(mainSheet);
			mainSheet = null;
		}

		// add a new sheet
		final Sheet sheet = getMainSheet();
		final Region region = setupCaptureRegion();
		sheet.addRegion(region);

		// determine the bounds of the region in pattern space
		// this information was provided by the user
		final double tlX = anchorPointTopLeft.getX();
		final double tlY = anchorPointTopLeft.getY();
		final double brX = anchorPointBottomRight.getX();
		final double brY = anchorPointBottomRight.getY();
		final double width = brX - tlX;
		final double height = brY - tlY;

		// create this custom mapping object
		final PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping(
				sheet);

		// tie the pattern bounds to this region object
		mapping.setPatternInformationOfRegion(region, //
				new PatternDots(tlX), new PatternDots(tlY), // 
				new PatternDots(width), new PatternDots(height));

		addSheet(sheet, mapping);
		// DebugUtils.println(mapping);
	}

	public void clearInk() {
		if (inkCollector != null) {
			inkCollector.clear();
		}
		if (handwritingRecognizer != null) {
			handwritingRecognizer.clear();
		}
	}

	/**
	 * Create and return the main Sheet object.
	 * 
	 * @return
	 */
	private Sheet getMainSheet() {
		if (mainSheet == null) {
			mainSheet = new Sheet(8.5, 11);
		}
		return mainSheet;
	}

	/**
	 * @return
	 */
	public Pen getPen() {
		if (pen == null) {
			pen = new Pen("Main Pen");
		}
		return pen;
	}

	/**
	 * 
	 */
	public void saveInkToDisk() {
		File file = new File(System.currentTimeMillis() + "_ink.xml");
		DebugUtils.println("Saving as: " + file.getAbsolutePath());
		inkCollector.saveInkToXMLFile(file);
	}

	/**
	 * Fit to WIDTH or HEIGHT, whichever is larger. The defined region should fit "perfectly" in our
	 * panel.
	 */
	private void scaleInkPanelToFit() {
		final double width = anchorPointBottomRight.x - anchorPointTopLeft.x;
		final double height = anchorPointBottomRight.y - anchorPointTopLeft.y;
		double newScale = 1.0;
		if (width > height) {
			// constrain to the width
			final PatternDots numDots = new PatternDots(width);
			final double definedWidth = numDots.getValueInPixels();
			final double guiWidth = gui.getInkPanel().getSize().getWidth();
			newScale = guiWidth / definedWidth;
		} else {
			// constrain to the height
			final PatternDots numDots = new PatternDots(height);
			final double definedHeight = numDots.getValueInPixels();
			final double guiHeight = gui.getInkPanel().getSize().getHeight();
			newScale = guiHeight / definedHeight;
		}
		// DebugUtils.println("New Scale: " + newScale);
		gui.getInkPanel().setScale(newScale);
	}

	/**
	 * Add an inkcollector to display ink, and a handwriting recognizer to do the recognition.
	 * 
	 * @return
	 */
	private Region setupCaptureRegion() {
		// the actual "physical" size of this region doesn't matter, as we never print it!
		final Region region = new Region("Handwriting Capture", 0, 0, 1, 1);

		// for displaying ink
		inkCollector = new InkCollector() {
			@Override
			public void contentArrived() {
				// DebugUtils.println(getInk().getNumStrokes());
				gui.getInkPanel().addInk(getNewInkOnly());
			}
		};

		// for recognizing the strokes
		handwritingRecognizer = new HandwritingRecognizer() {
			@Override
			public void contentArrived() {
				final List<String> topTen = recognizeHandwritingWithAlternatives();
				String text = topTen.get(0);
				DebugUtils.println("Handwritten Content: " + text);
				gui.setInfoText(text);
				gui.setAlternatives(topTen);
			}
		};

		region.addContentFilter(inkCollector);
		region.addContentFilter(handwritingRecognizer);
		return region;
	}
}
