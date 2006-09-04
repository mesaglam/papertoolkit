package edu.stanford.hci.r3.units;

/**
 * <p>
 * A percentage of a maximum value. If the maximum value is set to 8.5 inches, 50% will give us an
 * equivalent of 4.25 inches.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Percentage extends Units {

	/**
	 * "Identity Element", representing one hundred percent of 8.5 inches. Width of a US
	 * Letter-sized sheet.
	 */
	public static final Units ONE = new Percentage(100, new Inches(8.5));

	/**
	 * Pre-multiplied by the percentage value.
	 */
	private Units actualValue;

	/**
	 * This is used to evaluate the percentage unit.
	 */
	private Units maximumValue;

	/**
	 * Represents a percentage value of a maximum. Sometimes, it's easier to express a location of
	 * x,y=50%,50% on a sheet, especially if the sheet is later resized.
	 * 
	 * @param percentage
	 * @param maxValue
	 */
	public Percentage(double percentage, Units maxValue) {
		super(percentage);
		setMaximumValue(maxValue);
		actualValue = maxValue.getUnitsObjectOfSameTypeWithValue(maxValue.getValue() * percentage
				/ 100.0);
	}

	/**
	 * @return
	 */
	public Units getActualValue() {
		return actualValue;
	}

	/**
	 * @return
	 */
	public Units getMaximumValue() {
		return maximumValue;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return actualValue.getNumberOfUnitsInOneInch() * 100.0 / maximumValue.getValue();
	}

	/**
	 * @param maxVal
	 */
	private void setMaximumValue(Units maxVal) {
		maximumValue = maxVal;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#toString()
	 */
	public String toString() {
		return getValue() + " Percent of " + maximumValue + " (" + actualValue + ")";
	}
}
