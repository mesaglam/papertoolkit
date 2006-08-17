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
 */
public class Inches extends Units {

	/**
	 * Identity Element, representing one inch.
	 */
	public static final Units ONE = new Inches(1.0);

	public Inches() {
		value = 1;
	}

	/**
	 * @param d
	 */
	public Inches(double inches) {
		value = inches;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return 1.0;
	}
}
