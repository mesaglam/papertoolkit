package papertoolkit.demos.batched;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pen.ink.InkStroke;

/**
 * <p>
 * This is the Hello World application to demonstrate how batched and real-time
 * input work in PaperToolkit. The idea is that you program both applications
 * the same way. How you _use_ the application will determine what happens next.
 * 
 * If you run this application, it will look for a streaming pen. If one is
 * available, it will dispatch events from that pen to the event handler. If no
 * streaming pen is available, it will just wait for batched input. When batched
 * input is delivered, pen events will be injected into the event stream, and
 * will trigger the same handler.
 * 
 * You can distinguish between batched and real-time events by checking the
 * isRealtime() flag in the PenEvent object.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a>
 *         (ronyeh(AT)cs.stanford.edu)
 */
public class HelloBatched {

	public static void main(String[] args) {
		// bootstrap and run a paper + digital application
		Application app = PaperToolkit.createApplication(); // default name
		Sheet sheet = app.createSheet(); // default size is 8.5 x 11

		// default name; default size is size of sheet
		Region region = sheet.createRegion();
		region.addEventHandler(getInkHandler());
		PaperToolkit.runApplication(app);
	}

	private static EventHandler getInkHandler() {
		return new InkHandler() {
			// renamed from InkCollector; has nicer methods for
			// handling/collecting ink

			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				// all handlers have a method called handleX, which gets a
				// PenEvent; handlers also provide their subclasses access to
				// internal variables

				System.out.println("We have collected "
						+ getNumStrokesCollected() + " ink strokes. \t: "
						+ Thread.currentThread().getStackTrace()[1]);

				System.out.print("This handler was triggered in ");
				if (event.isRealTime()) {
					System.out.println("Real-time Mode..");
				} else {
					System.out.println("Batched Mode.");
				}
			}
		};
	}
}
