package edu.stanford.hci.r3.demos.debug;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ButtonRegion;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.tools.debug.DebuggingEnvironment;
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

		Region r = new Region("Ink", 1, 1, 6, 4);
		r.addEventHandler(new ClickHandler() {

			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Ink Region Clicked");
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

		ButtonRegion buttonRegion = new ButtonRegion("Send", 6, 9, 2, 1.3) {
			@Override
			protected void onClick(PenEvent e) {
				DebugUtils.println("Send Region clicked");
			}
		};

		sheet.addRegion(r);
		sheet.addRegion(buttonRegion);

		final Application app = new Application("A Simple Application");
		Pen pen = new Pen("Local Pen");
		// Pen pen = new Pen("Single Pen", "solaria.stanford.edu");
		app.addPen(pen);

		// pen.addLivePenListener(new PenListener() {
		// @Override
		// public void penDown(PenSample sample) {
		//
		// }
		//
		// @Override
		// public void penUp(PenSample sample) {
		// // DebugUtils.println("Pen Listener Clicked");
		//
		// }
		//
		// @Override
		// public void sample(PenSample sample) {
		//
		// }
		// });

		// no pattern info xml file loaded...
		// should allow runtime binding of pattern info
		// or auto binding?
		// or bind w/ previously saved event streams! =)
		app.addSheet(sheet);

		PaperToolkit p = new PaperToolkit(true, false, false);
		p.startApplication(app);
	}
}
