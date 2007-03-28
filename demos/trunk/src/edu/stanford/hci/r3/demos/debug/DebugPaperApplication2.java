package edu.stanford.hci.r3.demos.debug;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ButtonRegion;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.util.DebugUtils;

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
public class DebugPaperApplication2 {
	public static void main(String[] args) {

		Sheet sheet = new Sheet(8.5, 11);

		Region r = new Region("Ink", 1, 1, 6.5, 4);
		r.addEventHandler(new ClickHandler() {

			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Ink Region Clicked at " + e.getPercentageLocation());
				// test the showMe functionality...
				// DebuggingEnvironment.showMe(app, "Pen Listener Clicked");
			}

			@Override
			public void pressed(PenEvent e) {

			}

			@Override
			public void released(PenEvent e) {

			}
		});

		ButtonRegion buttonRegion = new ButtonRegion("Send", 5.5, 8.5, 2, 1.5) {
			@Override
			protected void onClick(PenEvent e) {
				DebugUtils.println("Send Region clicked at " + e.getPercentageLocation());
			}
		};

		sheet.addRegion(r);
		sheet.addRegion(buttonRegion);

		final Application app = new Application("A Simple Application");
		Pen pen = new Pen("Local Pen");
		// Pen pen = new Pen("Single Pen", "solaria.stanford.edu");
		app.addPen(pen);

		// no pattern info xml file loaded...
		// we are going to use the runtime binding of pattern info
		// TODO: Alternatively, auto binding? or bind w/ previously saved event streams?! =)
		app.addSheet(sheet);

		PaperToolkit p = new PaperToolkit(true, false, false);
		p.startApplication(app);
	}
}