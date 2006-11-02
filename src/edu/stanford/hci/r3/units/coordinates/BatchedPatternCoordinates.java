package edu.stanford.hci.r3.units.coordinates;

import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * Represents a location in the Anoto LOGICAL coordinate space. We can only get these coordinates through
 * docking the pen into the cradle. Each coordinate is actually bound to a PAD file which has translated the
 * physical coordinates into page addresses. Thus, we need to store physical addresses along with the Batched
 * Coordinate.
 * </p>
 * <p>
 * Batched coordinates look like 21.3.23.24 [306.625, 240.000]
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt"> BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchedPatternCoordinates extends Coordinates {

	private int book;

	private int page;

	private int segment;

	private int shelf;

	/**
	 * @param xCoord
	 * @param yCoord
	 */
	public BatchedPatternCoordinates(int theSegment, int theShelf, int theBook, int thePage,
			PatternDots xCoord, PatternDots yCoord) {
		super(xCoord, yCoord);
		segment = theSegment;
		shelf = theShelf;
		book = theBook;
		page = thePage;
	}

	public String getAddress() {
		return segment + "." + shelf + "." + book + "." + page;
	}

	public int getBook() {
		return book;
	}

	public int getPage() {
		return page;
	}

	public int getSegment() {
		return segment;
	}

	public int getShelf() {
		return shelf;
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
			System.err.println("BatchedPatternCoordinates: Incorrect type passed to setX(). ["
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
			System.err.println("BatchedPatternCoordinates: Incorrect type passed to setY(). ["
					+ yCoord.getClass() + "]");
		}
	}

	/**
	 * @see edu.stanford.hci.r3.units.coordinates.Coordinates#toString()
	 */
	public String toString() {
		return getAddress() + " [" + x + ", " + y + "]";
	}
}
