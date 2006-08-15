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
	 * @return the number of destination Units that fit into this Unit. Alternatively, the number n
	 *         that we would need to multiply the current value to in order to achieve the same
	 *         length in the destination unit. For example, if we are one inch, and the destination
	 *         is Points, then the number we need is 72, because we would have to multiply our value
	 *         (1) by 72 to achieve 72 points, which is the same length.
	 * @note we flipped the definition of this from Diamond's Edge.
	 */
	public double getConversionTo(Units destUnits) {
		final double retVal = destUnits.getNumberOfUnitsInOneInch() / getNumberOfUnitsInOneInch();
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
	 * @param destUnits
	 * @return
	 */
	public Units getUnitsObjectIn(Units destUnits) {
		Units units = null;
		try {
			units = destUnits.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		units.value = getValueIn(destUnits);
		return units;
	}

	/**
	 * @return the numerical value of this unit measure.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * For example, you have a Units object that represents 2 Inches. You want to see what the value
	 * will be in Points. getConversionTo(new Points()) will return 72. 2 * 72 is 144.
	 * 
	 * @param destUnits
	 * @return
	 */
	public double getValueIn(Units destUnits) {
		return value * getConversionTo(destUnits);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + " " + getUnitName();
	}
}
