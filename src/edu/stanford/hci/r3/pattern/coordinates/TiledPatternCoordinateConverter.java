package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;

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
public class TiledPatternCoordinateConverter {

	private double bottomBoundary;

	private double rightBoundary;

	private double xOrigin;

	private double yOrigin;

	private int startingTile;

	private int numTilesAcross;

	private int numTilesDown;

	private int dotsPerTileHorizontal;

	private int dotsPerTileVertical;

	private int numTilesOwned;

	private double numTotalDotsAcross;

	private double numTotalDotsDown;

	private double numHorizontalDotsBetweenTiles;

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
	 * 
	 * TODO: Update this Method to handle tiling...
	 */
	private TiledPatternCoordinateConverter(StreamedPatternCoordinates theOrigin, PatternDots w,
			PatternDots h) {
		setBoundaries(theOrigin, w, h);
	}

	/**
	 * @param startTile
	 * @param numTilesHoriz
	 * @param numTilesVert
	 * @param dotsPerTileHoriz
	 * @param dotsPerTileVert
	 * @param leftMostPatternX
	 * @param topMostPatternY
	 * @param tilesOwned
	 * @param numDotsAcross
	 * @param numDotsDown
	 * @param numHorizDotsBetweenTiles
	 */
	public TiledPatternCoordinateConverter(int startTile, int numTilesHoriz, int numTilesVert,
			int dotsPerTileHoriz, int dotsPerTileVert, double leftMostPatternX,
			double topMostPatternY, int tilesOwned, double numDotsAcross, double numDotsDown,
			double numHorizDotsBetweenTiles) {
		// the number of the first (top-left) tile; this is largely arbitrary, but _may_ correlate
		// with a pattern file number N.pattern --> N as a starting tile number. This makes
		// calculations easier for certain operations, such as finding out which page of a notebook
		// your user has written on.
		startingTile = startTile;

		// the number of tiles owned by this converter. Usually, this converter will map to a region
		// on a sheet. This means that the tiledPatternConverter will need to know how many tiles of
		// pattern the region contains. It will then help us find out where on the region we are.
		numTilesAcross = numTilesHoriz;
		numTilesDown = numTilesVert;

		// how wide and tall are these tiles? We assume uniform tiles (except for the rightmost and
		// bottommost tiles)
		dotsPerTileHorizontal = dotsPerTileHoriz;
		dotsPerTileVertical = dotsPerTileVert;

		// what is the physical coordinate of the top-left corner of the top-left tile?
		xOrigin = leftMostPatternX;
		yOrigin = topMostPatternY;

		// how many tiles do we own in total?
		numTilesOwned = tilesOwned;

		// how wide and tall is the whole region?
		numTotalDotsAcross = numDotsAcross;
		numTotalDotsDown = numDotsDown;

		// what is the x offset between the origins of two adjacent tiles?
		numHorizontalDotsBetweenTiles = numHorizDotsBetweenTiles;
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
	public boolean contains(StreamedPatternCoordinates location) {
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
	private void setBoundaries(StreamedPatternCoordinates theOrigin, PatternDots w, PatternDots h) {
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

	/**
	 * @param coord
	 * @return the tile number of this coordinate.
	 * 
	 * <blockquote><code>
	 *  [0][1][2]<br>
	 *  [3][4][5]
	 * </code></blockquote>
	 * 
	 * This assumes that the pattern space is wide (depends only on the X coordinate) and squat
	 * (same y coordinate for each page).
	 */
	public int getTileNumber(StreamedPatternCoordinates coord) {
		
		// START FROM HERE:
		// IS SHORTCUT THIS VALID?
		// Do we need a **good** contains method?
		
		// truncate/floor it, so we can get the tile number counting from our initial offset
		return (int) ((coord.getXVal() - xOrigin) / numHorizontalDotsBetweenTiles);
	}

}
