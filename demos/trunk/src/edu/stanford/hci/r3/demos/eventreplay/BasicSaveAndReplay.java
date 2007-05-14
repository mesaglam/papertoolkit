package edu.stanford.hci.r3.demos.eventreplay;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.demos.simple.PPTAdvancer;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pattern.coordinates.conversion.FlexiblePatternCoordinateConverter;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Simple demo of event saving and replay...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BasicSaveAndReplay extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit toolkit = new PaperToolkit(true, true, false);
		toolkit.loadApplication(new PPTAdvancer(false));
	}

	private Region floatingRegion;

	private PatternLocationToSheetLocationMapping mapping;

	private Pen pen;

	private Sheet sheet;

	/**
	 * 
	 */
	public BasicSaveAndReplay() {
		super("Basic Save and Replay");
		addSheet(getSheet(), getPatternMap());
		addPenInput(getPen());
	}

	/**
	 * @return
	 */
	private Region getFloatingRegion() {
		if (floatingRegion == null) {
			floatingRegion = new Region("Floating Region", //
					new Inches(0), new Inches(0), new Inches(8.5), new Inches(11));
			floatingRegion.addEventHandler(new ClickAdapter() {
				@Override
				public void clicked(PenEvent e) {
					DebugUtils.println("Clicked at: " + e.getPercentageLocation());
				}
			});
			// floatingRegion.addContentFilter(new HandwritingRecognizer());
		}
		return floatingRegion;
	}

	/**
	 * @return
	 */
	private PatternLocationToSheetLocationMapping getPatternMap() {
		if (mapping == null) {
			// make the region float by having a flexible pattern map
			mapping = new PatternLocationToSheetLocationMapping(getSheet());

			// tie the pattern bounds to this region object
			mapping.setPatternInformationOfRegion(getFloatingRegion(),
					new FlexiblePatternCoordinateConverter(getFloatingRegion()));
		}

		return mapping;
	}

	/**
	 * @return
	 */
	private Pen getPen() {
		if (pen == null) {
			pen = new Pen();
		}
		return pen;
	}

	/**
	 * A "floating" sheet is one that contains "floating" regions, which can accept pen data from pretty much
	 * any patterned paper.
	 * 
	 * @return
	 */
	private Sheet getSheet() {
		if (sheet == null) {
			sheet = new Sheet(); // 8.5 x 11 default
			sheet.addRegion(getFloatingRegion());
		}
		return sheet;
	}
}
