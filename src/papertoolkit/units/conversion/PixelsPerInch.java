package papertoolkit.units.conversion;

/**
 * <p>
 * Basically, it wraps a Double, and is used to interpret the size of images.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PixelsPerInch {

	/**
	 * Defaults to 72, because web browsers and Java2D consider images 72 dpi.
	 */
	private double value = 72;

	/**
	 * 
	 */
	public PixelsPerInch() {
		// 72 ppi
	}

	/**
	 * @param ppi
	 */
	public PixelsPerInch(double ppi) {
		setValue(ppi);
	}

	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param ppi
	 */
	public void setValue(double ppi) {
		value = ppi;
	}
}
