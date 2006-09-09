package edu.stanford.hci.r3.pattern;

import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A TiledPatternGenerator can create a TiledPattern object, which allows you to iterate through the
 * object to find the dot pattern.
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
	 * Where to start getting pattern from on the first page. We must ensure that it is a valid
	 * number. If it is an invalid number, we will just start from the 0th horizontal dot.
	 */
	private int initialDotXOffset;

	/**
	 * Where to start looking. Default to 0, but we should be able to set the offset so that our
	 * different regions don't have the same pattern.
	 */
	private int initialPatternFileNum = 0;

	/**
	 * Which was the last one we used? Make sure to use the next one if you want unique pattern.
	 */
	private int lastPatternFileUsed = 0;

	/**
	 * For regular tiles, how many horizontal dots will we need?
	 */
	private int numDotsXPerFullTile;

	/**
	 * For the right border tiles, we will tend to need fewer than the max.
	 */
	private int numDotsXRightMost;

	private int numDotsYBottomMost;

	private int numDotsYPerFullTile;

	/**
	 * The number of horizontal tiles.
	 */
	private int numTilesX;

	/**
	 * The number of vertical tiles.
	 */
	private int numTilesY;

	private int numTotalColumns;

	private int numTotalRows;

	/**
	 * Stores the pattern in strings. One String per row. We use StringBuilder because it is useful
	 * for reading in the pattern from disk.
	 */
	private StringBuilder[] pattern;

	/**
	 * Where we get our pattern from.
	 */
	private PatternPackage patternPackage;

	/**
	 * The creator of this object has to calculate exactly how many dots it needs. This class
	 * doesn't have smarts. It will give you exactly what you ask for.
	 * 
	 * @param thePatternPackage
	 * @param initialPatternFileN
	 * @param initialDotHorizOffset
	 * @param numTilesNeededX
	 * @param numTilesNeededY
	 * @param numDotsXFromRightMostTiles
	 * @param numDotsYFromBottomMostTiles
	 */
	public TiledPattern(PatternPackage thePatternPackage, // where to get pattern from
			int initialPatternFileN, int initialDotHorizOffset, // where to start
			int numTilesNeededX, int numTilesNeededY, // how many we'll need
			int numDotsXFromRightMostTiles, int numDotsYFromBottomMostTiles) {

		// this information is used for the indexing into the first pattern file...
		initialPatternFileNum = initialPatternFileN;
		initialDotXOffset = initialDotHorizOffset;

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
		if (initialDotXOffset < 0) {
			initialDotXOffset = 0;
		} else if (initialDotXOffset > numDotsXPerFullTile) {
			initialDotXOffset = 0;
			// but move to the next file!
			initialPatternFileNum++;
		}

		// how many columns and rows of pattern we will need, given all the above information
		numTotalColumns = (numTilesX - 1) * numDotsXPerFullTile // all columns before the rightmost
				+ numDotsXFromRightMostTiles; // the rightmost tiles
		numTotalRows = (numTilesY - 1) * numDotsYPerFullTile // all rows before the bottom
				+ numDotsYFromBottomMostTiles; // the bottom row

		// read in the pattern information
		loadPattern();
	}

	/**
	 * @return which was the last file we took pattern from.
	 */
	public int getLastPatternFileUsed() {
		return lastPatternFileUsed;
	}

	/**
	 * @return the number of rows of pattern this object represents
	 */
	public int getNumRows() {
		return pattern.length;
	}

	/**
	 * @param row
	 * @return
	 */
	public String getPatternOnRow(int row) {
		return pattern[row].toString();
	}

	/**
	 * @param patternPackage
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
				DebugUtils.println("PatternFileNumber: " + patternFileNumber);
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
				+ "\tDotsX: " + numTotalColumns + " DotsY: " + numTotalRows + "\n"
				+ "\t"
				+ numTilesX
				+ " Tile(s) in X, with "
				+ numDotsXRightMost
				+ " horizontal dots from the rightmost tiles.\n" //
				+ "\t" + numTilesY + " Tile(s) in Y, with "
				+ numDotsYBottomMost
				+ " vertical dots from the bottommost tiles.\n" //
				+ "}";
	}
}
