package edu.stanford.hci.r3.units;

/**
 * <p>
 * Allows fluid conversion between length measurements in different units.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class Units implements Cloneable {

	/**
	 * Java handles everything in 1/72nd of an inch (1 point). We should do the same to be
	 * compatible.
	 */
	public static final double POINTS_PER_INCH = 72;

	private String unitName;

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
	protected Units(double val) {
		value = val;
		unitName = getClass().getSimpleName(); // use the java name for the units name
	}

	/**
	 * Used when you want to create (and name) an anonymous unit. This is useful in situations where
	 * you need a temporary unit.
	 * 
	 * @param val
	 * @param name
	 */
	public Units(double val, String name) {
		value = val;
		unitName = name; // use the custom units name
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	protected Units clone() {
		try {
			return (Units) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Check if the lengths match.
	 * 
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
	 *         (1) by 72 to achieve 72 points, which is the same physical length.
	 * @note we flipped the definition of this from Diamond's Edge.
	 */
	public double getConversionTo(Units destUnits) {
		return destUnits.getNumberOfUnitsInOneInch() / getNumberOfUnitsInOneInch();
	}

	/**
	 * Returns a copy of this unit. WARNING: If you have a unit that depends on other factors, you
	 * should override this method. This is particularly true for Screen Pixels and Printer Dots.
	 * 
	 * TODO (Implement override for Pixels and PrinterDots)
	 * 
	 * @return
	 */
	public Units getCopy() {
		// tricky, huh? =)
		return getUnitsObjectOfSameLengthIn(this);
	}

	/**
	 * @return how many of these units fit in one inch.
	 */
	protected abstract double getNumberOfUnitsInOneInch();

	/**
	 * @return Inches, Points, Pixels...
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * Gets a new Units object of the same type as destUnits, with the same physical length as this
	 * object.
	 * 
	 * @param destUnits
	 * @return
	 */
	public Units getUnitsObjectOfSameLengthIn(Units destUnits) {
		final Units dest = destUnits.clone();
		dest.value = getValueIn(dest);
		return dest;
	}

	/**
	 * Gets a new Units object of the same type as this unit, but with a new value.
	 * 
	 * @param val
	 * @return
	 */
	public Units getUnitsObjectOfSameTypeWithValue(double val) {
		final Units dest = this.clone();
		dest.value = val;
		return dest;
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
