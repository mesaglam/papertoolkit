package edu.stanford.hci.r3.demos.powerpoint;

import java.awt.event.KeyEvent;
import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.actions.types.RobotAction;
import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.events.handlers.GestureHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.util.DebugUtils;

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
public class PPTAdvancer {

	private static EventHandler getClickHandler() {
		return new ClickHandler() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked " + clickCount + " times...");
			}

			public void pressed(PenEvent e) {
			}

			public void released(PenEvent e) {
			}
		};
	}

	private static EventHandler getMarkHandler() {
		return new GestureHandler() {
			public void handleMark(PenEvent e, GestureDirection dir) {
				DebugUtils.println(dir);
				switch (dir) {
				case NE:
				case E:
				case SE:
				default:
					final RobotAction robot = new RobotAction();
					robot.keyType(KeyEvent.VK_RIGHT);
					robot.invoke();
					break;
				}
			}
		};
	}

	public static void main(String[] args) {
		Application app = new Application("Advancer");
		Sheet s = new Sheet(new Inches(8.5), new Inches(11));
		Region r = new Region("Button", 1, 1, 4, 2);
		r.addEventHandler(getMarkHandler());
		r.addEventHandler(getClickHandler());
		s.addRegion(r);
		app.addSheet(s, new File("data/Grid/App.patternInfo.xml"));
		app.addPen(new Pen("Primary Pen"));
		PaperToolkit toolkit = new PaperToolkit();
		toolkit.startApplication(app);
	}

}
