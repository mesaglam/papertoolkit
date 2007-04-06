package edu.stanford.hci.r3.units.coordinates;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * Represents a location in the Anoto PHYSICAL coordinate space. We can only get these coordinates through
 * streaming. Batched coordinates will have to operate differently, as they will be bound to a PAD file which
 * has translated the physical coordinates into page addresses.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt"> BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StreamedPatternCoordinates extends Coordinates {

	/**
	 * @param xCoord
	 * @param yCoord
	 */
	public StreamedPatternCoordinates(PatternDots xCoord, PatternDots yCoord) {
		super(xCoord, yCoord);
	}

	/**
	 * Constructs one of these from the streaming pen sample.
	 * 
	 * @param sample
	 */
	public StreamedPatternCoordinates(PenSample sample) {
		this(new PatternDots(sample.getX()), new PatternDots(sample.getY()));
	}

	/**
	 * @return
	 */
	public PatternDots getX() {
		return (PatternDots) x;
	}

	/**
	 * @return The x value of this streamed coordinate.
	 */
	public double getXVal() {
		return x.getValue();
	}

	/**
	 * @return The y value of this streamed coordinate.
	 */
	public PatternDots getY() {
		return (PatternDots) y;
	}

	/**
	 * @return
	 */
	public double getYVal() {
		return y.getValue();
	}

	/**
	 * @param xCoord
	 */
	public void setX(PatternDots xCoord) {
		x = xCoord;
	}

	/**
	 * @see edu.stanford.hci.r3.units.coordinates.Coordinates#setX(edu.stanford.hci.r3.units.Units)
	 */
	public void setX(Units xCoord) {
		if (xCoord instanceof PatternDots) {
			x = xCoord;
		} else {
			System.err.println("StreamedPatternCoordinates: Incorrect type passed to setX(). ["
					+ xCoord.getClass() + "]");
		}
	}

	/**
	 * @param yCoord
	 */
	public void setY(PatternDots yCoord) {
		y = yCoord;
	}

	/**
	 * @see edu.stanford.hci.r3.units.coordinates.Coordinates#setY(edu.stanford.hci.r3.units.Units)
	 */
	public void setY(Units yCoord) {
		if (yCoord instanceof PatternDots) {
			y = yCoord;
		} else {
			System.err.println("StreamedPatternCoordinates: Incorrect type passed to setY(). ["
					+ yCoord.getClass() + "]");
		}
	}

	/**
	 * @see edu.stanford.hci.r3.units.coordinates.Coordinates#toString()
	 */
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
