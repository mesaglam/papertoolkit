package edu.stanford.hci.r3.demos.powerpoint;

import java.awt.event.KeyEvent;
import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.actions.types.RobotAction;
import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.events.handlers.GestureHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.PenAdapter;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Shows how to create a Powerpoint advancer tool with pen-based marking gestures.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PPTAdvancer extends Application {

	public static void main(String[] args) {
		PaperToolkit toolkit = new PaperToolkit(true, true, false);
		toolkit.startApplication(new PPTAdvancer(false));
	}

	/**
	 * @param configureApp
	 */
	public PPTAdvancer(boolean configureApp) {
		super("Advancer");

		final Sheet s = new Sheet(new Inches(8.5), new Inches(11));
		final Region r = new Region("Button", 1, 1, 4, 2);
		r.addEventHandler(getMarkHandler());
		r.addEventHandler(getClickHandler());
		s.addRegion(r);
		final Pen pen = new Pen("Primary Pen");
		addPenInput(pen);

		if (configureApp) {
			// get two points from the pen...
			pen.addLivePenListener(new PenAdapter() {
				private int pointCount = 0;

				private PenSample sample1;

				private PenSample sample2;

				public void penDown(PenSample sample) {
					if (pointCount == 0) {
						sample1 = sample;
					} else if (pointCount == 1) {
						sample2 = sample;
					}
					pointCount++;
					if (pointCount == 2) {
						final double minX = Math.min(sample1.getX(), sample2.getX());
						final double minY = Math.min(sample1.getY(), sample2.getY());
						final double width = Math.abs(sample1.getX() - sample2.getX());
						final double height = Math.abs(sample1.getY() - sample2.getY());

						// create this custom mapping object
						final PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping(
								s);
						// tie the pattern bounds to this region object
						mapping.setPatternInformationOfRegion(r, //
								new PatternDots(minX), new PatternDots(minY), // 
								new PatternDots(width), new PatternDots(height));
						// save it out to the file
						mapping.saveConfigurationToXML(new File(
								"data/Powerpoint/PPT.patternInfo.xml"));
					}
				}
			});

		} else {
			// this one works with page 13 of the Mead 5x8 notebook...
			addSheet(s, new File("data/Powerpoint/PPT.patternInfo.xml"));
		}

	}

	/**
	 * @return
	 */
	private EventHandler getClickHandler() {
		return new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked " + clickCount + " times...");
			}
		};
	}

	/**
	 * @return
	 */
	private EventHandler getMarkHandler() {
		return new GestureHandler() {
			public void handleMark(PenEvent e, GestureDirection dir) {
				DebugUtils.println(dir);
				final RobotAction robot = new RobotAction();
				switch (dir) {
				case N:
				case S:
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyType(KeyEvent.VK_TAB);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.invoke();
					break;
				case NW:
				case W:
				case SW:
					robot.keyType(KeyEvent.VK_LEFT);
					robot.invoke();
					break;
				case NE:
				case E:
				case SE:
				default:
					robot.keyType(KeyEvent.VK_RIGHT);
					robot.invoke();
					break;
				}
			}
		};
	}

}
