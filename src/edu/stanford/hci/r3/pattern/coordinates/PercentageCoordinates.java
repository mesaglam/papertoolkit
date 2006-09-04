package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.units.Percentage;

/**
 * <p>
 * Can be used for specifying where we are on a sheet, region, etc. Instead of specifying absolute
 * coordinates (number of pixels), we specify in a fraction relative to the upper left corner of the
 * document (0,0).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PercentageCoordinates {

	private Percentage x;

	private Percentage y;

	/**
	 * @param x
	 * @param y
	 */
	public PercentageCoordinates(Percentage pctInX, Percentage pctInY) {
		x = pctInX;
		y = pctInY;
	}
	
	public Percentage getX() {
		return x;
	}
	
	public Percentage getY() {
		return y;
	}
}
