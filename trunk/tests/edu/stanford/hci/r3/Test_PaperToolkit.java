package edu.stanford.hci.r3;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_PaperToolkit {
	/**
	 * @return
	 */
	private static Application constructApplication() {
		// a new paper application
		Application paperApp = new Application("Handwriting Recognition");

		// handle one sheet
		paperApp.addSheet(constructSheet());

		// a single live pen
		paperApp.addPen(constructPen());

		return paperApp;
	}

	/**
	 * @return
	 */
	private static Pen constructPen() {
		Pen pen = new Pen();
		pen.startLiveMode();
		return pen;
	}

	/**
	 * @return
	 */
	private static Sheet constructSheet() {
		Sheet sheet = new Sheet(new Inches(5), new Inches(4));

		// define some regions
		Region region = new Region("Test", 0, 0, 5, 4);
		region.setActive(true);
		// region.addEventListener(getPenTapListener());

		sheet.addRegion(region);
		return sheet;
	}

	public static void main(String[] args) {
		PaperToolkit toolkit = new PaperToolkit();

		// toolkit.print(constructSheet());
		toolkit.startApplication(constructApplication());
	}

}
