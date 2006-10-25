package edu.stanford.hci.r3.pen.handwriting;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pattern.coordinates.TiledPatternCoordinateConverter;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenAdapter;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CaptureApplication extends Application {

	private PenSample anchorPointBottomRight;

	private PenSample anchorPointTopLeft;

	private HandwritingCaptureDebugger gui;

	/**
	 * We will use one sheet at a time to test handwriting recognition.
	 */
	private Sheet mainSheet;

	/**
	 * We will only allow one pen in this testing environment.
	 */
	private Pen pen;

	private PaperToolkit toolkit;

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
			public void penDown(PenSample sample) {
				if (anchorPointTopLeft == null) {
					anchorPointTopLeft = sample;
					DebugUtils.println("Top Left Point is now set to " + anchorPointTopLeft);
				} else if (anchorPointBottomRight == null) {
					anchorPointBottomRight = sample;
					DebugUtils
							.println("Bottom Right Point is now set to " + anchorPointBottomRight);
					scaleInkPanelToFit();
					addOneSheetAndOneRegionForHandwritingCapture();
				} else {
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
			toolkit.getEventEngine().unregisterPatternMapForEventHandling(
					mainSheet.getPatternLocationToSheetLocationMapping());
			mainSheet = null;
		}

		// add a new sheet
		final Sheet sheet = getMainSheet();
		final Region region = setupCaptureRegion();
		sheet.addRegion(region);
		final PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping();
		mapping.setSheet(sheet);
		mapping.initializeMap(sheet.getRegions());
		final double tlX = anchorPointTopLeft.getX();
		final double tlY = anchorPointTopLeft.getY();
		mapping.setPatternInformationOfRegion(region, new TiledPatternCoordinateConverter(region
				.getName(), tlX, tlY, // top left corner
				MathUtils.rint(anchorPointBottomRight.getX() - tlX), // width
				MathUtils.rint(anchorPointBottomRight.getY() - tlY))); // height
		addSheet(sheet, mapping);
		DebugUtils.println(mapping);

		// now, we have to tell the already-running event engine to be aware of this new pattern
		// mapping!
		toolkit.getEventEngine().registerPatternMapForEventHandling(mapping);
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
	 * So we can access the toolkit that started this application.
	 * 
	 * TODO: Should the toolkit always do this automatically? I figure it would be nice... Evaluate
	 * if this should be moved into the main Application class.
	 * 
	 * @param p
	 */
	public void setToolkitReference(PaperToolkit p) {
		toolkit = p;
	}

	/**
	 * @return
	 */
	private Region setupCaptureRegion() {
		final Region region = new Region("Handwriting Capture", 0, 0, 1, 1);
		region.addContentFilter(new InkCollector() {
			@Override
			public void contentArrived() {
				// DebugUtils.println(getInk().getNumStrokes());
				gui.getInkPanel().addInk(getNewInkOnly());
			}
		});
		return region;
	}
}
