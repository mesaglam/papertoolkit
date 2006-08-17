package edu.stanford.hci.r3.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 * @created Jun 9, 2006
 */
public class PatternDots extends Units {

	/**
	 * Identity Element representing one Pattern Dot.
	 */
	public static final Units ONE = new PatternDots(1);

	/**
	 * I picked a number in between the dots per X inches and dots per Y inches. Hopefully this
	 * number will work for most calculations.
	 * 
	 * 84.77540106951871657754010695187 is the average of X & Y from earlier GIGAprints experiments.
	 * 84.77540106951871657754010695187 is the average from reading the R3 pattern files.
	 * 
	 * I picked a number slightly closer to the vertical value, because it makes tiling better (it
	 * won't spill over by one dot in the vertical direction)
	 */
	private static final double PATTERN_UNITS_PER_INCH = 84.7727;

	/**
	 * Default Constructor for Doing unit conversions, etc...
	 */
	public PatternDots() {
		value = 0;
	}

	/**
	 * @param dots
	 */
	public PatternDots(double dots) {
		value = dots;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return PATTERN_UNITS_PER_INCH;
	}

}
