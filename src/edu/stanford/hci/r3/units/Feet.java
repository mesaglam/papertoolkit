package edu.stanford.hci.r3.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Yay for Americans.
 */
public class Feet extends Units {

	/**
	 * Identity Element, representing one foot.
	 */
	public static final Units ONE = new Feet(1.0);

	/**
	 * 
	 */
	public Feet() {
		super(1);
	}

	/**
	 * @param ft
	 */
	public Feet(double ft) {
		super(ft);
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	protected double getNumberOfUnitsInOneInch() {
		return 1 / 12.0;
	}

}
