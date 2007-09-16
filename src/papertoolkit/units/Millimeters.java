package papertoolkit.units;

/**
 * <p>
 * For our metric friends. =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Millimeters extends Units {

	/**
	 * Identity Element, representing one centimeter.
	 */
	public static final Units ONE = new Millimeters(1.0);

	/**
	 * One Millimeter (quite short, you see).
	 */
	public Millimeters() {
		super(1);
	}

	/**
	 * Specify your own length in millimeters.
	 * 
	 * @param mm
	 */
	public Millimeters(double mm) {
		super(mm);
	}

	/**
	 * @see papertoolkit.units.Units#getNumberOfUnitsInOneInch()
	 */
	protected double getNumberOfUnitsInOneInch() {
		return 25.4;
	}
}
