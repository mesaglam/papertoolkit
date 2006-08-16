package edu.stanford.hci.r3.units;

/**
 * Represents 1/72 of an inch. This is also what Java2D uses as a default unit for printing to the
 * printer.
 * 
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 */
public class Points extends Units {

	/**
	 * The Identity Element; One Point, representing 1/72nd of an inch.
	 */
	public static final Units ONE = new Points(1);

	public Points() {
		value = 1;
	}

	/**
	 * @param pts
	 */
	public Points(double pts) {
		value = pts;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	public double getNumberOfUnitsInOneInch() {
		return 72;
	}

	/**
	 * @return
	 */
	public Inches toInches() {
		return new Inches(value / POINTS_PER_INCH);
	}

}
