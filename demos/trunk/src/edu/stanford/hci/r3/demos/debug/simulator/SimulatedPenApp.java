package edu.stanford.hci.r3.demos.debug.simulator;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ButtonRegion;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSimulator;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SimulatedPenApp {
	public static void main(String[] args) {

		Sheet sheet = new Sheet(8.5, 11);

		Region r = new Region("Ink", 1, 1, 6, 4);
		r.addEventHandler(new ClickHandler() {

			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked");
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
				DebugUtils.println("I got clicked");
			}
		};

		sheet.addRegion(r);
		sheet.addRegion(buttonRegion);

		Application app = new Application("A Simple Debug Application");
		app.addPenInput(new PenSimulator());
		app.addSheet(sheet); // no pattern info xml file loaded...

		PaperToolkit p = new PaperToolkit(true, false, false);
		p.startApplication(app);
	}
}
