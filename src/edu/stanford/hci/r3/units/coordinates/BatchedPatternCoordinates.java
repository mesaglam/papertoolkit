package edu.stanford.hci.r3.units.coordinates;

import edu.stanford.hci.r3.pattern.coordinates.PageAddress;
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

	/**
	 * Which page is this coordinate located on?
	 */
	private PageAddress pageAddress;

	/**
	 * @param xCoord
	 * @param yCoord
	 */
	public BatchedPatternCoordinates(int theSegment, int theShelf, int theBook, int thePage,
			double xCoordDots, double yCoordDots) {
		this(new PageAddress(theSegment, theShelf, theBook, thePage), new PatternDots(xCoordDots),
				new PatternDots(yCoordDots));
	}

	/**
	 * @param address
	 * @param x
	 * @param y
	 */
	public BatchedPatternCoordinates(PageAddress address, PatternDots x, PatternDots y) {
		super(x, y);
		pageAddress = address;
	}

	/**
	 * @param pageAddress
	 * @param xCoord
	 * @param yCoord
	 */
	public BatchedPatternCoordinates(String pageAddr, double xCoordDots, double yCoordDots) {
		this(new PageAddress(pageAddr), new PatternDots(xCoordDots), new PatternDots(yCoordDots));
	}

	/**
	 * @return
	 */
	private int getBook() {
		return pageAddress.getBook();
	}

	/**
	 * @return
	 */
	private int getPage() {
		return pageAddress.getPage();
	}

	/**
	 * @return
	 */
	public PageAddress getPageAddress() {
		return pageAddress;
	}

	/**
	 * @return
	 */
	public String getPageAddressString() {
		return getSegment() + "." + getShelf() + "." + getBook() + "." + getPage();
	}

	/**
	 * @return
	 */
	private int getSegment() {
		return pageAddress.getSegment();
	}

	/**
	 * @return
	 */
	private int getShelf() {
		return pageAddress.getShelf();
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
		return getPageAddressString() + " [" + x + ", " + y + "]";
	}
}
