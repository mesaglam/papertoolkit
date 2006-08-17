package edu.stanford.hci.r3.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Centimeters extends Units {

	/**
	 * Identity Element, representing one centimeter.
	 */
	public static final Units ONE = new Centimeters(1.0);

	/**
	 * 
	 */
	public Centimeters() {
		value = 1;
	}

	/**
	 * @param cm
	 */
	public Centimeters(double cm) {
		value = cm;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return 2.54;
	}
}
