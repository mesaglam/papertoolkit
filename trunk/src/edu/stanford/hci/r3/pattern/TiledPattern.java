package edu.stanford.hci.r3.pattern;

import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * A TiledPatternGenerator can create a TiledPattern object, which allows you to iterate through the
 * object to find the dot pattern.
 */
public class TiledPattern {

	private int numDotsXPerFullTile;

	private int numDotsXRightMost;

	private int numDotsYBottomMost;

	private int numDotsYPerFullTile;

	private int numTilesX;

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
	 * @param thePatternPackage
	 * @param numTilesNeededX
	 * @param numTilesNeededY
	 * @param numDotsXFromRightMostTiles
	 * @param numDotsYFromBottomMostTiles
	 */
	public TiledPattern(PatternPackage thePatternPackage, int numTilesNeededX, int numTilesNeededY,
			int numDotsXFromRightMostTiles, int numDotsYFromBottomMostTiles) {

		patternPackage = thePatternPackage;

		numDotsXPerFullTile = patternPackage.getNumPatternColsPerFile();
		numDotsYPerFullTile = patternPackage.getNumPatternRowsPerFile();

		numTilesX = numTilesNeededX;
		numTilesY = numTilesNeededY;

		numDotsXRightMost = numDotsXFromRightMostTiles;
		numDotsYBottomMost = numDotsYFromBottomMostTiles;

		numTotalColumns = (numTilesX - 1) * numDotsXPerFullTile + numDotsXFromRightMostTiles;
		numTotalRows = (numTilesY - 1) * numDotsYPerFullTile + numDotsYFromBottomMostTiles;

		loadPattern();
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
		PatternDots origX = new PatternDots(0);
		PatternDots origY = new PatternDots(0);
		PatternDots dotsW = new PatternDots(numDotsXPerFullTile);
		PatternDots dotsH = new PatternDots(numDotsYPerFullTile);
		PatternDots dotsWRightMost = new PatternDots(numDotsXRightMost);
		PatternDots dotsHBottomMost = new PatternDots(numDotsYBottomMost);

		// go through each tile and read in the strings...
		int patternFileNumber = 0;
		for (int tileRow = 0; tileRow < numTilesY; tileRow++) {
			for (int tileCol = 0; tileCol < numTilesX; tileCol++) {

				// by default, we want every dot from a pattern file
				PatternDots width = dotsW;
				PatternDots height = dotsH;

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
				String[] patternTile = patternPackage.readPatternFromFile(patternFileNumber, origX,
						origY, width, height);

				// copy the patternTile into our pattern variable
				// figure out which actual pattern row we start from...
				int actualPatternRow = tileRow * numDotsYPerFullTile;
				for (String patternTileRow : patternTile) {
					pattern[actualPatternRow].append(patternTileRow);
					// next row
					actualPatternRow++;
				}

				// go to the next file
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
