package edu.stanford.hci.r3.pattern.coordinates.conversion;

import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;

/**
 * <p>
 * Represents a 2D region of pattern. It can convert an incoming coordinate into a relative location on
 * this patch of pattern.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface PatternCoordinateConverter {

	public boolean contains(StreamedPatternCoordinates coord);

	public String getRegionName();

	public PercentageCoordinates getRelativeLocation(StreamedPatternCoordinates coord);

	/**
	 * @return the area, in pattern dots squared, of this patch.
	 */
	public double getArea();
}
