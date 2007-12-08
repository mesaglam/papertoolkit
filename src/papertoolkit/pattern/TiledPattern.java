package papertoolkit.pattern;

import papertoolkit.units.PatternDots;
import papertoolkit.units.coordinates.StreamedPatternCoordinates;

/**
 * <p>
 * A TiledPatternGenerator can create a TiledPattern object, which allows you to iterate through the object to
 * find the dot pattern.
 * </p>
 * <p>
 * This class will also know the physical and logical coordinates of the pattern that it reads in. This allows
 * other classes to determine this information later on, for coordinate transformations, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TiledPattern {

	/**
	 * Where to start getting pattern from on the first page. We must ensure that it is a valid number. If it
	 * is an invalid number, we will just start from the 0th horizontal dot.
	 */
	private int initialDotXOffset;

	/**
	 * 
	 */
	private int initialDotYOffset;

	/**
	 * Where to start looking. Default to 0, but we should be able to set the offset so that our different
	 * regions don't have the same pattern.
	 */
	private int initialPatternFileNum = 0;

	/**
	 * Which was the last one we used? Make sure to use the next one if you want unique pattern.
	 */
	private int lastPatternFileUsed;

	/**
	 * For regular tiles, how many horizontal dots will we need?
	 */
	private int numDotsXPerFullTile;

	/**
	 * For the right border tiles, we will tend to need fewer than the max.
	 */
	private int numDotsXRightMost;

	/**
	 * 
	 */
	private int numDotsYBottomMost;

	/**
	 * How many vertical dots for each regular tile?
	 */
	private int numDotsYPerFullTile;

	/**
	 * The number of horizontal tiles.
	 */
	private int numTilesX;

	/**
	 * The number of vertical tiles.
	 */
	private int numTilesY;

	/**
	 * Grand Total: How many columns does this pattern contain?
	 */
	private int numTotalColumns;

	/**
	 * Grand Total: How many rows does this pattern contain?
	 */
	private int numTotalRows;

	/**
	 * Stores the pattern in strings. One String per row. We use StringBuilder because it is useful for
	 * reading in the pattern from disk.
	 */
	private StringBuilder[] pattern;

	/**
	 * 
	 */
	private StreamedPatternCoordinates patternCoordinateOfOrigin;

	/**
	 * Where we get our pattern from.
	 */
	private PatternPackage patternPackage;

	/**
	 * The creator of this object has to calculate exactly how many dots it needs. This class doesn't have
	 * smarts. It will give you exactly what you ask for.
	 * 
	 * @param thePatternPackage
	 * @param initialPatternFileN
	 * @param initialDotHorizOffset
	 * @param initialDotVertOffset
	 * @param numTilesNeededX
	 * @param numTilesNeededY
	 * @param numDotsXFromRightMostTiles
	 * @param numDotsYFromBottomMostTiles
	 */
	public TiledPattern(PatternPackage thePatternPackage, // where to get pattern from
			int initialPatternFileN, int initialDotHorizOffset, int initialDotVertOffset, //
			int numTilesNeededX, int numTilesNeededY, // how many we'll need
			int numDotsXFromRightMostTiles, int numDotsYFromBottomMostTiles) {

		// this information is used for the indexing into the first pattern file...
		initialPatternFileNum = initialPatternFileN;
		initialDotXOffset = initialDotHorizOffset;
		initialDotYOffset = initialDotVertOffset;

		patternPackage = thePatternPackage;

		// every full tile will have this many dots
		numDotsXPerFullTile = patternPackage.getNumPatternColsPerFile();
		numDotsYPerFullTile = patternPackage.getNumPatternRowsPerFile();

		// how many tiles were requested in each direction
		numTilesX = numTilesNeededX;
		numTilesY = numTilesNeededY;

		// number of dots spilled over into the rightmost and bottom most columns...
		numDotsXRightMost = numDotsXFromRightMostTiles;
		numDotsYBottomMost = numDotsYFromBottomMostTiles;

		// error check some values
		boolean advanceToNextPatternPage = false;

		if (initialDotXOffset < 0) {
			initialDotXOffset = 0;
		} else if (initialDotXOffset > numDotsXPerFullTile) {
			advanceToNextPatternPage = true;
		}
		if (initialDotYOffset < 0) {
			initialDotYOffset = 0;
		} else if (initialDotYOffset > numDotsYPerFullTile) {
			advanceToNextPatternPage = true;
		}

		if (advanceToNextPatternPage) {
			// DebugUtils.println("Going to the next Pattern File.");
			// move to the next file! because we were out of bounds
			initialDotXOffset = 0;
			initialDotYOffset = 0;
			initialPatternFileNum++;
		}

		// how many columns and rows of pattern we will need, given all the above information
		numTotalColumns = (numTilesX - 1) * numDotsXPerFullTile // all columns before the rightmost
				+ numDotsXFromRightMostTiles; // the rightmost tiles
		numTotalRows = (numTilesY - 1) * numDotsYPerFullTile // all rows before the bottom
				+ numDotsYFromBottomMostTiles; // the bottom row

		// get the origin of the first pattern file
		patternCoordinateOfOrigin = patternPackage.getPatternCoordinateOfOriginOfFile(initialPatternFileNum);

		// adjust the x coordinate for the offset
		patternCoordinateOfOrigin.setX(new PatternDots(patternCoordinateOfOrigin.getXVal()
				+ initialDotXOffset));
		// adjust the y coordinate to account for the offset
		patternCoordinateOfOrigin.setY(new PatternDots(patternCoordinateOfOrigin.getYVal()
				+ initialDotYOffset));

		// DebugUtils.println("Origin for this patch of pattern: " + patternCoordinateOfOrigin);

		// read in the pattern information
		loadPattern();
	}

	/**
	 * @return which file we will use to start looking for pattern.
	 */
	public int getInitialPatternFileNumber() {
		return initialPatternFileNum;
	}

	/**
	 * @return which was the last file we took pattern from.
	 */
	public int getLastPatternFileUsed() {
		return lastPatternFileUsed;
	}

	public int getNumDotsXFromRightMostTiles() {
		return numDotsXRightMost;
	}

	/**
	 * @return
	 */
	public int getNumDotsXPerFullTile() {
		return numDotsXPerFullTile;
	}

	public int getNumDotsYFromBottomMostTiles() {
		return numDotsYBottomMost;
	}

	/**
	 * @return
	 */
	public int getNumDotsYPerFullTile() {
		return numDotsYPerFullTile;
	}

	/**
	 * @return
	 */
	public double getNumHorizDotsBetweenTiles() {
		return patternPackage.getNumDotsHorizontalBetweenPages();
	}

	/**
	 * @return
	 */
	public int getNumTilesX() {
		return numTilesX;
	}

	/**
	 * @return
	 */
	public int getNumTilesY() {
		return numTilesY;
	}

	/**
	 * @return
	 */
	public int getNumTotalColumns() {
		return numTotalColumns;
	}

	/**
	 * @return the number of rows of pattern this object represents
	 */
	public int getNumTotalRows() {
		return numTotalRows;
	}

	/**
	 * @return
	 */
	public double getNumVertDotsBetweenTiles() {
		return patternPackage.getNumDotsVerticalBetweenPages();
	}

	/**
	 * @return X coordinate of the top of the tile.
	 */
	public double getOriginXInDots() {
		return patternCoordinateOfOrigin.getXVal();
	}

	/**
	 * @return Y coordinate of the top of the tile.
	 */
	public double getOriginYInDots() {
		return patternCoordinateOfOrigin.getYVal();
	}

	/**
	 * @param row
	 * @return
	 */
	public String getPatternOnRow(int row) {
		return pattern[row].toString();
	}

	/**
	 * Called by the constructor to load in the pattern. Any field set by this method can be safely accessed
	 * by external classes.
	 */
	private void loadPattern() {
		// System.out.println(this);

		// each string will be numTotalColumns long
		pattern = new StringBuilder[numTotalRows];
		for (int row = 0; row < numTotalRows; row++) {
			pattern[row] = new StringBuilder();
		}

		// the rectangle of pattern to read from each file goes from
		// 0,0 to numDotsXPerFullTile,numDotsYPerFullTile in dots
		final PatternDots originX = new PatternDots(0);
		final PatternDots originY = new PatternDots(0);
		final PatternDots originXLeftTopMost = new PatternDots(initialDotXOffset);
		final PatternDots originYLeftTopMost = new PatternDots(initialDotYOffset);
		final PatternDots dotsW = new PatternDots(numDotsXPerFullTile);
		final PatternDots dotsH = new PatternDots(numDotsYPerFullTile);
		final PatternDots dotsWRightMost = new PatternDots(numDotsXRightMost);
		final PatternDots dotsHBottomMost = new PatternDots(numDotsYBottomMost);

		// go through each tile and read in the strings...
		// start looking at the correct pattern file number
		// this enables us to get tiled pattern from different parts of our pattern space.
		int patternFileNumber = initialPatternFileNum;

		for (int tileRow = 0; tileRow < numTilesY; tileRow++) {
			for (int tileCol = 0; tileCol < numTilesX; tileCol++) {

				// by default, we start from the leftmost and topmost dot
				PatternDots origX = originX;
				PatternDots origY = originY;

				// by default, we want every dot from a pattern file
				PatternDots width = dotsW;
				PatternDots height = dotsH;

				// if leftmost and topmost tile, we want to start from where we were asked to
				if (tileCol == 0 && tileRow == 0) {
					origX = originXLeftTopMost;
					origY = originYLeftTopMost;
				}

				// if rightmost, we only want the dots that spill over into the rightmost column
				if (tileCol == numTilesX - 1) {
					width = dotsWRightMost;
				}

				// if bottommost, we only want the dots that spill into the bottommost row
				if (tileRow == numTilesY - 1) {
					height = dotsHBottomMost;
				}

				// if we are on the rightmost or bottommost tile, we need to use alternate bounds
				// DebugUtils.println("PatternFileNumber: " + patternFileNumber);
				final String[] patternTile = patternPackage.readPatternFromFile(patternFileNumber, //
						origX, origY, width, height);

				// copy the patternTile into our pattern variable
				// figure out which actual pattern row we start from...
				int actualPatternRow = tileRow * numDotsYPerFullTile;
				for (String patternTileRow : patternTile) {
					pattern[actualPatternRow].append(patternTileRow);
					// next row
					actualPatternRow++;
				}

				// go to the next file, but keep track of which file we last used.
				lastPatternFileUsed = patternFileNumber;
				patternFileNumber++;
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Tiling Information {\n" //
				+ "\t" + patternCoordinateOfOrigin + "\n" + "\tDotsX: " + numTotalColumns
				+ " DotsY: "
				+ numTotalRows + "\n" + "\t" + numTilesX
				+ " Tile(s) in X, with "
				+ numDotsXRightMost
				+ " horizontal dots from the rightmost tiles.\n" //
				+ "\t" + numTilesY + " Tile(s) in Y, with "
				+ numDotsYBottomMost
				+ " vertical dots from the bottommost tiles.\n" //
				+ "}";
	}
}
