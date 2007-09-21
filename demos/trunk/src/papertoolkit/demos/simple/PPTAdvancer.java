package papertoolkit.demos.simple;

import java.awt.event.KeyEvent;
import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.actions.types.RobotAction;
import papertoolkit.application.Application;
import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.MarkingGestureHandler;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.PenAdapter;
import papertoolkit.units.Inches;
import papertoolkit.units.PatternDots;
import papertoolkit.util.DebugUtils;


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
		PaperToolkit toolkit = new PaperToolkit();
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
						final PatternToSheetMapping mapping = new PatternToSheetMapping(
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
			addSheet(s);
			// addSheet(s, new File("data/Powerpoint/PPT.patternInfo.xml"));
		}

	}

	/**
	 * @return
	 */
	private EventHandler getClickHandler() {
		return new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked " + getConsecutiveClickCount() + " times...");
			}
		};
	}

	/**
	 * @return
	 */
	private EventHandler getMarkHandler() {
		return new MarkingGestureHandler() {
			public void handleMark(PenEvent e, MarkDirection dir) {
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
