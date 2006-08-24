package edu.stanford.hci.r3.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents a rectangular size, whose width and height are in arbitrary units.
 */
public class Size implements Cloneable {

	/**
	 * The Height of this Sheet.
	 */
	private Units height;

	/**
	 * The Width of this Sheet.
	 */
	private Units width;

	/**
	 * 
	 */
	public Size() {
		width = Inches.ONE;
		height = Inches.ONE;
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
