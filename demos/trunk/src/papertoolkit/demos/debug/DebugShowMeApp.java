package papertoolkit.demos.debug;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.regions.ButtonRegion;
import papertoolkit.pen.Pen;
import papertoolkit.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * This is designed for Figure 9 in the UIST Paper. There are two large regions, one for ink
 * drawings, and another for handwriting. There are also three buttons near the bottom. The two on
 * the left detect marking gestures. The button on the lower right detects clicks.
 * </p>
 * <p>
 * When the application runs, we can right click the system tray (or window bar?) to select
 * DebugThis! This opens the event visualization and showMe() window. Whenever events are invoked,
 * they highlight (reducing the need for got here statements).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class DebugShowMeApp {
	public static void main(String[] args) {

		Sheet sheet = new Sheet(8.5, 11);

		Region inkRegion = new Region("Ink for ToDos", 1, 1, 6.5, 4);
		inkRegion.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				// DebugUtils.println("Ink Region Clicked at " + e.getPercentageLocation());

				PercentageCoordinates pctLoc = e.getPercentageLocation();
				showMe("Writing on Ink Region at \n [" + pctLoc.getPercentageInXDirection() + ", "
						+ pctLoc.getPercentageInYDirection() + "]");

				// test the showMe functionality...
				// DebuggingEnvironment.showMe(app, "Pen Listener Clicked");
			}
		});

		ButtonRegion buttonRegion = new ButtonRegion("Upload to Calendar", 5.5, 8.5, 2, 1.5) {
			protected void onClick(PenEvent e) {
				showMe("Send Clicked: Uploading your Ink!");
			}
		};

		sheet.addRegion(inkRegion);
		sheet.addRegion(buttonRegion);

		final Application app = new Application("A Simple Application");
		Pen pen = new Pen("Local Pen");
		// Pen pen = new Pen("Single Pen", "solaria.stanford.edu");
		app.addPenInput(pen);

		// no pattern info xml file loaded...
		// we are going to use the runtime binding of pattern info
		// TODO: Alternatively, auto binding? or bind w/ previously saved event streams?! Bind a
		// whole sheet at a time, and do the regions automatically? =)
		app.addSheet(sheet);

		PaperToolkit p = new PaperToolkit();
		p.startApplication(app);
	}
}
