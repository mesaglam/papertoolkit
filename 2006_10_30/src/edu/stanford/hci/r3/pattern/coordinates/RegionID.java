package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * Allows us to save and load to/from xml files, because we can identify regions more or less uniquely this
 * way.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RegionID {

	private Units height;

	private String name;

	private Units originX;

	private Units originY;

	private Units width;

	public RegionID(Region r) {
		name = r.getName();
		originX = r.getOriginX();
		originY = r.getOriginY();
		width = r.getWidth();
		height = r.getHeight();
	}

	/**
	 * Same name, origin, and dimensions. Good enough for now!
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof RegionID) {
			RegionID r = (RegionID) o;
			return name.equals(r.name) && originX.equals(r.originX) && originY.equals(r.originY)
					&& width.equals(r.width) && height.equals(r.height);
		}
		return false;
	}

	/**
	 * Makes HashMaps work.
	 * 
	 * WARNING: Did this contribute to the Region Naming bug? TODO: Make it a nicer hashcode function.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) (name.hashCode() + originX.getValue() + originY.getValue() + width.getValue() + height
				.getValue());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Region ID: {" + name + ", " + originX + ", " + originY + ", " + width + ", " + height + "}";
	}
}
