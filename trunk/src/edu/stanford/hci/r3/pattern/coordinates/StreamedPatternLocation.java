package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.units.PatternDots;

/**
 * <p>
 * Represents
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt"> BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StreamedPatternLocation {

	private PatternDots x;

	private PatternDots y;

	/**
	 * @param xCoord
	 * @param yCoord
	 */
	public StreamedPatternLocation(PatternDots xCoord, PatternDots yCoord) {
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
	public PatternDots getY() {
		return y;
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
	public double getYVal() {
		return y.getValue();
	}
}
