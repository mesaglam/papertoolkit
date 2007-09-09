package papertoolkit.demos.eventreplay;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.demos.simple.PPTAdvancer;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pattern.coordinates.conversion.FlexiblePatternCoordinateConverter;
import papertoolkit.pen.Pen;
import papertoolkit.units.Inches;
import papertoolkit.util.DebugUtils;

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
public class EventSaveAndReplay extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit toolkit = new PaperToolkit();
		toolkit.loadApplication(new PPTAdvancer(false));
	}

	private Region floatingRegion;

	private PatternToSheetMapping mapping;

	private Pen pen;

	private Sheet sheet;

	/**
	 * 
	 */
	public EventSaveAndReplay() {
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
				public void clicked(PenEvent e) {
					DebugUtils.println("Clicked at: " + e.getPercentageLocation());
				}
			});
		}
		return floatingRegion;
	}

	/**
	 * @return
	 */
	private PatternToSheetMapping getPatternMap() {
		if (mapping == null) {
			// make the region float by having a flexible pattern map
			mapping = new PatternToSheetMapping(getSheet());

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
