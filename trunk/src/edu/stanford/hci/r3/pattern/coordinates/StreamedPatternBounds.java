package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternLocation;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * <p>
 * Stores the bounds in physical (streaming) coordinates.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StreamedPatternBounds {

	private double bottomBoundary;

	private double rightBoundary;

	private double xOrigin;

	private double yOrigin;

	/**
	 * For when you need to set values later.
	 */
	public StreamedPatternBounds() {
		setBoundaries(0, 0, 0, 0);
	}

	/**
	 * <p>
	 * This object deals with physical coordinates (the type that you get when you stream
	 * coordinates from the Nokia SU-1B). They are all huge numbers, but we store them in
	 * PatternDots objects.
	 * </p>
	 * <p>
	 * Although we can convert the PatternDots objects into other Units, it doesn't really make
	 * sense, as the dots are specified in the world of Anoto's gargantuan pattern space. For
	 * example, if you converted the xOrigin to inches, you would get a beast of a number.
	 * </p>
	 * <p>
	 * For performance, we precompute the boundaries and store just those numbers.
	 * </p>
	 */
	public StreamedPatternBounds(StreamedPatternLocation theOrigin, PatternDots w, PatternDots h) {
		setBoundaries(theOrigin, w, h);
	}

	/**
	 * For performance, we precompute the boundaries and store just those numbers. This method's
	 * likely faster than the other contains test, especially if you already have the x and y values
	 * and do not need to create a StreamedPatternLocation object.
	 * 
	 * @param xValPatternDots
	 *            x value of the location, in PatternDots (physical/streamed coordinates)
	 * @param yValPatternDots
	 *            y value of the location, in PatternDots (physical/streamed coordinates)
	 * @return
	 */
	private boolean contains(final double xValPatternDots, final double yValPatternDots) {
		return xValPatternDots >= xOrigin // to the right of leftmost boundary
				&& xValPatternDots < rightBoundary // to the left of rightmost boundary
				&& yValPatternDots >= yOrigin // below top boundary
				&& yValPatternDots < bottomBoundary; // above bottom boundary
	}

	/**
	 * For performance, we precompute the boundaries and store just those numbers.
	 * 
	 * @param location
	 * @return whether the bounds contains this location
	 */
	public boolean contains(StreamedPatternLocation location) {
		final double xTestVal = location.getXVal();
		final double yTestVal = location.getYVal();
		return contains(xTestVal, yTestVal);
	}

	/**
	 * @param xVal
	 * @param yVal
	 * @param rightVal
	 * @param bottomVal
	 */
	private void setBoundaries(double xVal, double yVal, double rightVal, double bottomVal) {
		xOrigin = xVal;
		yOrigin = yVal;
		rightBoundary = rightVal;
		bottomBoundary = bottomVal;
	}

	/**
	 * Sets the origin, width, and height of the pattern bounds.
	 * 
	 * @param theOrigin
	 * @param w
	 * @param h
	 */
	private void setBoundaries(StreamedPatternLocation theOrigin, PatternDots w, PatternDots h) {
		setBoundaries(theOrigin.getXVal(), theOrigin.getYVal(), xOrigin + w.getValue(), yOrigin
				+ h.getValue());
	}

	/**
	 * Returns the Left, Top, Right, Bottom Boundaries.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[" + xOrigin + "," + yOrigin + " --> " + rightBoundary + "," + bottomBoundary + "]";
	}
}
