package edu.stanford.hci.r3.demos.grid;

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
		Application application = new Application("Simple Paper App");
		Sheet sheet = new Sheet(new Inches(8.5), new Inches(11));
		Region region = new Region("Button", 1, 1, 4, 2);
		region.addEventHandler(getClickHandler());
		sheet.addRegion(region);
		application.addSheet(sheet, new File("data/Grid/Simple Paper App.patternInfo.xml"));
		application.addPen(new Pen("Primary Pen"));
		PaperToolkit toolkit = new PaperToolkit(true /* app manager */);
		toolkit.loadApplication(application);
	}

	private static EventHandler getClickHandler() {
		return new ClickAdapter() {
			public void clicked(PenEvent e) {
				System.out.println("Clicked " + clickCount + " times.");
			}
		};
	}
}
