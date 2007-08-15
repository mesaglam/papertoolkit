package papertoolkit.units;

/**
 * <p>
 * Represents a rectangular size, whose width and height are in arbitrary units.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Size implements Cloneable {

	/**
	 * The height value.
	 */
	private Units height;

	/**
	 * The width value, in some units.
	 */
	private Units width;

	/**
	 * 8.5 x 11 inches, is our default size, since Letter-sized pages are so common.
	 */
	public Size() {
		width = new Inches(8.5);
		height = new Inches(11);
	}

	/**
	 * @param w
	 * @param h
	 */
	public Size(Units w, Units h) {
		width = w;
		height = h;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Size clone() {
		return new Size(width.clone(), height.clone());
	}

	/**
	 * @return
	 */
	public Units getHeight() {
		return height;
	}

	/**
	 * @return
	 */
	public Units getWidth() {
		return width;
	}

	/**
	 * @param w
	 * @param h
	 */
	public void setSize(Units w, Units h) {
		width = w;
		height = h;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[" + width + " x " + height + "]";
	}
}
