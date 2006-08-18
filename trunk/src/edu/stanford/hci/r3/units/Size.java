package edu.stanford.hci.r3.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents a rectangular size.
 */
public class Size {
	
	/**
	 * The Height of this Sheet.
	 */
	private Units height;

	/**
	 * The Width of this Sheet.
	 */
	private Units width;

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
}
