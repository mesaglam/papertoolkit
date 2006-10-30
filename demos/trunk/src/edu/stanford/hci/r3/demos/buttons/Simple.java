package edu.stanford.hci.r3.demos.buttons;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.units.Inches;

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
public class Simple {
	
	public static void main(String[] args) {
		Application app = new Application("Simple App");
		Sheet s = new Sheet(new Inches(8.5), new Inches(11));
		Region r = new Region("Button", 1, 1, 4, 2);
		r.addEventHandler(getClickHandler());
		s.addRegion(r);
		app.addSheet(s, new File("data/Grid/App.patternInfo.xml"));
		app.addPen(new Pen("Primary Pen"));
		PaperToolkit toolkit = new PaperToolkit(true /* app manager */);
		toolkit.loadApplication(app);
	}

	private static EventHandler getClickHandler() {
		return new ClickAdapter() {
			public void clicked(PenEvent e) {
				System.out.println("Clicked " + clickCount + " times.");
			}
		};
	}
}
