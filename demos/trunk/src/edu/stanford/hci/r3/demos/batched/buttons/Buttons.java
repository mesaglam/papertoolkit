package edu.stanford.hci.r3.demos.batched.buttons;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.application.Application;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Four Buttons, with event handlers. You can use them in Batched Mode or Real-time Mode, because Batched
 * Event Handling happens in more or less the same way.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Buttons extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit tk = new PaperToolkit(true);
		tk.startApplication(new Buttons());
	}

	private Sheet sheet;

	/**
	 * Design Flaw: Why do we need to call addSheet(Sheet), render the sheet, and then edit the code to use
	 * addSheet(Sheet, File) instead? This should be fixed somehow...
	 */
	public Buttons() {
		super("Buttons");
		addSheet(getSheet(), new File("data/Batched/Buttons.patternInfo.xml"));
		addPenInput(getPen());
	}

	/**
	 * @return
	 */
	private Region getBottomLeftRegion() {
		final Region bl = new Region("BottomLeft", 2, 6, 2, 2);
		bl.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on " + bl.getName());
			}
		});
		return bl;
	}

	/**
	 * @return
	 */
	private Region getBottomRightRegion() {
		final Region br = new Region("BottomRight", 5, 6, 2, 2);
		br.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on " + br.getName());
			}
		});
		return br;
	}

	/**
	 * @return
	 */
	private Pen getPen() {
		return new Pen();
	}

	/**
	 * @return
	 */
	private Sheet getSheet() {
		sheet = new Sheet(8.5, 11);
		sheet.addRegion(getTopLeftRegion());
		sheet.addRegion(getTopRightRegion());
		sheet.addRegion(getBottomLeftRegion());
		sheet.addRegion(getBottomRightRegion());
		return sheet;
	}

	/**
	 * @return
	 */
	private Region getTopLeftRegion() {
		final Region tl = new Region("TopLeft", 2, 1, 2, 2);
		tl.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on " + tl.getName());
			}
		});
		return tl;
	}

	/**
	 * @return
	 */
	private Region getTopRightRegion() {
		final Region tr = new Region("TopRight", 5, 1, 2, 2);
		tr.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Clicked on " + tr.getName());
			}
		});
		return tr;
	}
}
