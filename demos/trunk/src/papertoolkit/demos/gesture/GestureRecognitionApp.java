package papertoolkit.demos.gesture;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.GestureHandler;
import papertoolkit.events.handlers.MarkingGestureHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.Pen;
import papertoolkit.pen.gesture.dollar.DollarRecognizer.RecognitionResult;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Uses the Application infrastructure to test Gesture Recognition on regions...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class GestureRecognitionApp {

	public GestureRecognitionApp() {
		Application app = PaperToolkit.createApplication();
		app.addPenInput(new Pen());

		Sheet sheet = app.createSheet(5, 8);
		Region region = sheet.createRegion(0, 0, 5, 8);
		region.addEventHandler(new GestureHandler() {
			public void gestureArrived(PenEvent lastSample, RecognitionResult result, InkStroke stroke) {
				DebugUtils.println(result);
			}
		});

		Sheet sheet2 = app.createSheet(5, 8);
		Region region2 = sheet2.createRegion();
		region2.addEventHandler(new MarkingGestureHandler() {
			public void handleMark(PenEvent e, MarkDirection dir) {
				DebugUtils.println(dir);
				switch (dir) {
				case E:
					break;
				case W:
					break;
				default:
					break;
				}
			}
		});

		app.run();
	}

	public static void main(String[] args) {
		new GestureRecognitionApp();
	}
}
