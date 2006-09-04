package edu.stanford.hci.r3.units.coordinates;

import edu.stanford.hci.r3.units.PatternDots;

/**
 * <p>
 * Represents a location in the anoto physical coordinate space. We can only get these coordinates
 * through streaming. Batched coordinates will have to operate differently, as they will be bound to
 * a PAD file which has translated the physical coordinates into page addresses.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt"> BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StreamedPatternCoordinates {

	private PatternDots x;

	private PatternDots y;

	/**
	 * @param xCoord
	 * @param yCoord
	 */
	public StreamedPatternCoordinates(PatternDots xCoord, PatternDots yCoord) {
		x = xCoord;
		y = yCoord;
	}

	/**
	 * @return
	 */
	public PatternDots getX() {
		return x;
	}

	/**
	 * @return
	 */
	public double getXVal() {
		return x.getValue();
	}

	/**
	 * @return
	 */
	public PatternDots getY() {
		return y;
	}

	/**
	 * @return
	 */
	public double getYVal() {
		return y.getValue();
	}
}
