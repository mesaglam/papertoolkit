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
	 * I picked a number in between the dots per X inches and dots per Y inches. Hopefully this
	 * number will work for most calculations.
	 * 
	 * 84.77540106951871657754010695187 is the average of X & Y (see Pattern.java)
	 */
	private static final double PATTERN_UNITS_PER_INCH = 84.77540106951871657754010695187;

	/**
	 * Default Constructor for Doing unit conversions, etc...
	 */
	public PatternDots() {
		value = 0;
	}

	/**
	 * @param originX
	 */
	public PatternDots(double dots) {
		value = dots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.diamondsedge.printing.api.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	public double getNumberOfUnitsInOneInch() {
		return PATTERN_UNITS_PER_INCH;
	}

}
