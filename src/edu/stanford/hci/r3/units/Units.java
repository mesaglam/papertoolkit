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
	 * The Unit is immutable. To protect it, we will prevent access to the value. You can only set
	 * it upon construction of the object.
	 */
	private double value;

	/**
	 * The only time you can set the value. A Units object should be immutable.
	 * 
	 * @param val
	 */
	public Units(double val) {
		value = val;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Units) {
			final Units other = (Units) o;

			// if we get our value in the other units, is it equal to the value of the other unit?
			return getValueIn(other) == other.getValue();
		}
		// not a units object!
		return false;
	}

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
		return destUnits.getNumberOfUnitsInOneInch() / getNumberOfUnitsInOneInch();
	}

	/**
	 * @return how many of these units fit in one inch.
	 */
	protected abstract double getNumberOfUnitsInOneInch();

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
	 * @return
	 */
	public double getValueInCentimeters() {
		return getValueIn(Centimeters.ONE);
	}

	/**
	 * This is a CONVENIENCE method. Yes, Yes... it's probably poor programming style to have the
	 * Units class be aware of its subclass (Inches). However, it makes code a lot more readable.
	 * Plus, if you implement your own Units class, you can use the generic method
	 * getValueIn(Units).
	 * 
	 * @return the double value of this unit, converted to Inches.
	 */
	public double getValueInInches() {
		return getValueIn(Inches.ONE);
	}

	/**
	 * @return
	 */
	public double getValueInMillimeters() {
		return getValueIn(Millimeters.ONE);
	}

	/**
	 * CONVENIENCE method for converting this unit to PatternDots.
	 * 
	 * @return
	 */
	public double getValueInPatternDots() {
		return getValueIn(PatternDots.ONE);
	}

	/**
	 * CONVENIENCE method for converting to Pixels.
	 * 
	 * @return the value after converting to pixels.
	 */
	private double getValueInPixels() {
		return getValueIn(Pixels.ONE);
	}

	/**
	 * CONVENIENCE method for converting this unit to Points.
	 * 
	 * @return
	 */
	public double getValueInPoints() {
		return getValueIn(Points.ONE);
	}

	/**
	 * Get an Inches object representing the same physical length.
	 * 
	 * @return
	 */
	public Inches toInches() {
		return new Inches(getValueInInches());
	}

	/**
	 * @return
	 * 
	 */
	public Pixels toPixels() {
		return new Pixels(getValueInPixels());
	}

	/**
	 * Get a Points object representing the same physical length.
	 * 
	 * @return
	 */
	public Points toPoints() {
		return new Points(getValueInPoints());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + " " + getUnitName();
	}
}
