package papertoolkit.units;

import java.text.DecimalFormat;

/**
 * <p>
 * Unit representing the length from pattern dot to pattern dot.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 * @created Jun 9, 2006
 */
public class PatternDots extends Units {

	/**
	 * Prettifies printouts.
	 */
	public static final DecimalFormat FORMATTER = new DecimalFormat("#.000");

	/**
	 * Identity Element representing one Pattern Dot.
	 */
	public static final Units ONE = new PatternDots(1);

	/**
	 * I picked a number in between the dots per X inches and dots per Y inches. Hopefully this number will
	 * work for most calculations.
	 * 
	 * 84.77540106951871657754010695187 is the average of X & Y from earlier GIGAprints experiments.
	 * 84.77540106951871657754010695187 is the average from reading the R3 pattern files.
	 * 
	 * I picked a number slightly closer to the vertical value, because it makes tiling better (it won't spill
	 * over by one dot in the vertical direction)
	 * 
	 * Hrmm.... The old value I used is 84.7727. Now, using the "0.3 mm" as in the documentation, I get
	 * 84.66667... Let's give it a try to see how it does. However, in practice, it seems that 85.8 works
	 * better... as the number of dots fills out a 1 meter width nicely...
	 */
	private static final double PATTERN_UNITS_PER_INCH = 85.8;

	/**
	 * Default Constructor for Doing unit conversions, etc...
	 */
	public PatternDots() {
		super(1);
	}

	/**
	 * @param dots
	 */
	public PatternDots(double dots) {
		super(dots);
	}

	/**
	 * How many pattern dots per inch?
	 * 
	 * @see papertoolkit.units.Units#getNumberOfUnitsInOneInch()
	 */
	protected double getNumberOfUnitsInOneInch() {
		return PATTERN_UNITS_PER_INCH;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return FORMATTER.format(getValue()) + " " + getUnitName();
	}

}
