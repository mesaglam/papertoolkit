package edu.stanford.hci.r3.pattern.coordinates;

import java.util.HashMap;
import java.util.List;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * <p>
 * This class stores mappings from regions of pattern (and their coordinates in Anoto space) to
 * Sheets and locations on those sheets. This mapping works both ways. Given a location on the
 * sheet, we should be able to find the pattern coordinate. Given a coordinate, we should be able to
 * find the location on the sheet.
 * </p>
 * <p>
 * An instance of this object should be built when a PDF is rendered with pattern. At that moment,
 * regions on a sheet are bound to specific physical coordinates. Each application should store this
 * mapping per sheet.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PatternCoordinateToSheetLocationMapping {

	/**
	 * The mapping is bound to one sheet.
	 */
	private Sheet sheet;

	/**
	 * Binds regions to pattern bounds, specified in logical (batched) and physical (streamed)
	 * coordinates.
	 */
	private HashMap<Region, StreamedPatternBounds> regionToPatternBounds = new HashMap<Region, StreamedPatternBounds>();

	/**
	 * One mapping object per sheet. Create this object after you have added all the regions that
	 * you need to the sheet. This class will maintain a mapping of Regions to physical (stremaing)
	 * and logical (batched) pen coordinates.
	 * 
	 * @param s
	 */
	public PatternCoordinateToSheetLocationMapping(Sheet s) {
		sheet = s;
		initializeMap(s.getRegions());
	}

	/**
	 * @param regions
	 */
	private void initializeMap(List<Region> regions) {
		for (Region r : regions) {
			regionToPatternBounds.put(r, null);
		}
	}

}
