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
public abstract class Units {

	/**
	 * Java handles everything in 1/72nd of an inch (1 point). We should do the same to be
	 * compatible.
	 */
	public static final double POINTS_PER_INCH = 72;

	/**
	 * All subclasses can access the value of this unit.
	 */
	protected double value;

	/**
	 * @param destUnits
	 * @return the number of this Unit that fits in the destination Unit.
	 */
	public double getConversionTo(Units destUnits) {
		final double retVal = getNumberOfUnitsInOneInch() / destUnits.getNumberOfUnitsInOneInch();
		// System.out.println("Units :: Conversion is: " + retVal);
		return retVal;
	}

	/**
	 * @return how many of these units fit in one inch.
	 */
	public abstract double getNumberOfUnitsInOneInch();

	/**
	 * @return Inches, Points, Pixels...
	 */
	public String getUnitName() {
		return getClass().getSimpleName();
	}

	/**
	 * @return the numerical value of this unit measure.
	 */
	public double getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + " " + getUnitName();
	}
}
