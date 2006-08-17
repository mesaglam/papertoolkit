/**
 * 
 */
package edu.stanford.hci.r3.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents a screen based unit. This depends on the Pixels Per Inch of the screen, of course.
 */
public class Pixels extends Units {

	/**
	 * The Identity Element representing one Pixel on a "default" screen at a default pixelsPerInch.
	 */
	public static final Units ONE = new Pixels(1);

	// on the HCI Group Laptops, we have 900 Pixels for 8.5 Inches (PPI = 105.88)
	// on the DiamondTouch Table, we have 413 pixels for 11 Inches (PPI = 37.545)
	private double pixelsPerInch = 105.88;

	/**
	 * 
	 */
	public Pixels() {
		value = 0;
	}

	/**
	 * @param numPixels
	 */
	public Pixels(double numPixels) {
		value = numPixels;
	}

	/**
	 * @param ppi
	 */
	public void setPixelsPerInch(double ppi) {
		pixelsPerInch = ppi;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return pixelsPerInch;
	}

}
