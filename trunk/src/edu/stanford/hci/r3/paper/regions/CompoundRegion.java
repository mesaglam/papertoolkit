package edu.stanford.hci.r3.paper.regions;

import java.util.HashMap;
import java.util.Map;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.Coordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Put more than one region together.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CompoundRegion extends Region {

	private Map<Region, Coordinates> regionsAndRelativeLocations;

	/**
	 * 
	 */
	public CompoundRegion(Units xOrigin, Units yOrigin) {
		// assume 1x1 inch until we calculate the actual size
		super(xOrigin, yOrigin, new Inches(1), new Inches(1));
		regionsAndRelativeLocations = new HashMap<Region, Coordinates>();
	}
	
	public void addRegion(Region r, Coordinates relativeCoord) {
		DebugUtils.println("Unimplemented");
	}
}
