package edu.stanford.hci.r3.units.coordinates;

import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * A point in 2D space.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Coordinates {

	private Units x;

	private Units y;

	/**
	 * @param x
	 * @param y
	 */
	public Coordinates(Units xCoord, Units yCoord) {
		x = xCoord;
		y = yCoord;
	}

	/**
	 * @return
	 */
	public Units getX() {
		return x;
	}

	/**
	 * @return
	 */
	public Units getY() {
		return y;
	}

	public String toString() {
		return "Coordinates: x=[" + x.toString() + "]  y=[" + y.toString() + "]";
	}
}
