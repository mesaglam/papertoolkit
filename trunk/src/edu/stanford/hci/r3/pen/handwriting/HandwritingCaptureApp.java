package edu.stanford.hci.r3.pen.handwriting;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pattern.coordinates.TiledPatternCoordinateConverter;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenAdapter;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;
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
public class HandwritingCaptureApp extends Application {

	private PenSample anchorPointBottomRight;

	private PenSample anchorPointTopLeft;

	private boolean calibrationHandlerExists = false;

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
	 */
	public HandwritingCaptureApp() {
		super("Handwriting Capture");
		addPen(getPen());
	}

	/**
	 * 
	 */
	public void addCalibrationHandler() {
		if (calibrationHandlerExists) {
			System.err.println("HandwritingCaptureApp: Calibration Button Already Pressed");
			return;
		}

		final Pen pen = getPen();
		pen.addLivePenListener(new PenAdapter() {
			public void penDown(PenSample sample) {
				if (anchorPointTopLeft == null) {
					anchorPointTopLeft = sample;
					DebugUtils.println("Top Left Point is now set to " + anchorPointTopLeft);
				} else if (anchorPointBottomRight == null) {
					anchorPointBottomRight = sample;
					DebugUtils.println("Bottom Right Point is now set to " + anchorPointBottomRight);
					addOneSheetAndOneRegionForHandwritingCapture();
				} else {
					final PenListener listener = this;
					// We must modify the listeners from an external thread, as we are
					// currently iterating through it
					// This will happen after we have released the lock
					new Thread(new Runnable() {
						public void run() {
							pen.removeLivePenListener(listener);
							calibrationHandlerExists = false;
						}
					}).start();
				}
			}
		});

		calibrationHandlerExists = true;
	}

	/**
	 * Add a sheet and a region at RUNTIME.
	 */
	private void addOneSheetAndOneRegionForHandwritingCapture() {
		Sheet sheet = getMainSheet();
		final Region region = new Region("Handwriting Capture", 0, 0, 1, 1);
		region.addEventHandler(new ClickHandler() {

			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked at " + e.getPercentageLocation());
			}

			@Override
			public void pressed(PenEvent e) {

			}

			@Override
			public void released(PenEvent e) {

			}
		});
		sheet.addRegion(region);
		PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping();
		mapping.setSheet(sheet);
		mapping.initializeMap(sheet.getRegions());
		final double tlX = anchorPointTopLeft.getX();
		final double tlY = anchorPointTopLeft.getY();
		mapping.setPatternInformationOfRegion(region, new TiledPatternCoordinateConverter(region.getName(),
				tlX, tlY, // top left corner
				MathUtils.rint(anchorPointBottomRight.getX() - tlX), // width
				MathUtils.rint(anchorPointBottomRight.getY() - tlY))); // height
		addSheet(sheet, mapping);
		DebugUtils.println(mapping);

		// now, we have to tell the already-running event engine to be aware of this new pattern mapping!
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
			addSheet(mainSheet);
		}
		return mainSheet;
	}

	/**
	 * @return
	 */
	public Pen getPen() {
		if (pen == null) {
			pen = new Pen("Main Pen");
			pen.addLivePenListener(new PenAdapter() {
				public void sample(PenSample sample) {
					if ((anchorPointTopLeft != null) && (anchorPointBottomRight != null)) {
						final double x = sample.getX();
						final double y = sample.getY();
						final double tlX = anchorPointTopLeft.getX();
						final double tlY = anchorPointTopLeft.getY();
						final double brX = anchorPointBottomRight.getX();
						final double brY = anchorPointBottomRight.getY();

						DebugUtils.println("X: " + (x - tlX) / (brX - tlX) + //
								"  Y: " + (y - tlY) / (brY - tlY));
					}
				}
			});
		}
		return pen;
	}

	public void setToolkitReference(PaperToolkit p) {
		toolkit = p;
	}
}
