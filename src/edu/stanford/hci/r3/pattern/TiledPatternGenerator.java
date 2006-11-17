package edu.stanford.hci.r3.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.config.Configuration;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Creates arbitrary-sized pattern blocks, assuming you have enough pattern files to supply it with.
 * </p>
 * <p>
 * This object keeps track of what pattern blocks you have used. It stores it in a map of each page
 * (basically a Rectangle2D bounds). Once a request comes in that is not servicable by this SINGLE
 * page, it will increment the patternFile Number, and allow you to get pattern from the next page.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TiledPatternGenerator {

	/**
	 * Number of dots we pad between pattern requests, so that no two pattern requests are touching
	 * each other.
	 */
	private static final int BUFFER = 30;

	public static final String CONFIG_PATH_KEY = "tiledpatterngenerator.patternpath";

	public static final String CONFIG_PATH_VALUE = "/pattern/";

	/**
	 * The name of the default pattern package (stored in pattern/default/).
	 */
	public static final String DEFAULT_PATTERN_PACKAGE_NAME = "default";

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = getPatternPath();

	/**
	 * @return the location of pattern data, from the configuration files.
	 */
	private static File getPatternPath() {
		return Configuration.getConfigFile(CONFIG_PATH_KEY);
	}

	/**
	 * Packages indexed by name.
	 */
	private Map<String, PatternPackage> availablePackages;

	/**
	 * Allows us to track usage within a page.
	 */
	private int lastDotUsedX = 0;

	/**
	 * Track pattern usage within a page. Reset every time we go to a new page.
	 */
	private int lastDotUsedY = 0;

	private int maxOfRecentHeightsInDots = 0;

	/**
	 * For debugging purposes.
	 */
	private int numTimesGetPatternCalled = 0;

	/**
	 * Where we should start getting our pattern from. This is incremented by some amount every time
	 * we call getPattern(...), so that the pattern returned will be unique.
	 */
	private int patternFileNumber = 0;

	/**
	 * Currently selected pattern package.
	 */
	private PatternPackage patternPackage;

	/**
	 * Customize this to reflect where you store your pattern definition files.
	 */
	private File patternPath;

	/**
	 * Default Pattern Path Location (PaperToolkit\data\pattern), automatically copied by eclipse to
	 * the export folder: PaperToolkit\bin\pattern. This will have to change at some point, when we
	 * move to the .jar deployment.
	 */
	public TiledPatternGenerator() {
		this(PATTERN_PATH);
	}

	/**
	 * Customize the location of pattern definition files.
	 */
	public TiledPatternGenerator(File patternPathLocation) {
		patternPath = patternPathLocation;
		availablePackages = PatternPackage.getAvailablePatternPackages(patternPath);
		setPackage(DEFAULT_PATTERN_PACKAGE_NAME);
	}

	/**
	 * Prints out some information on the tiling...
	 */
	public void displayTilingInformation(Units horizontal, Units vertical) {
		long numDotsX = Math.round(horizontal.getValueInPatternDots());
		long numDotsY = Math.round(vertical.getValueInPatternDots());

		// System.out.println(numDotsX + " " + numDotsY);

		int numTilesNeededX = 0;
		int numTilesNeededY = 0;

		int numDotsRemainingX = (int) numDotsX;
		int numDotsRemainingY = (int) numDotsY;

		final int numPatternColsPerFile = patternPackage.getNumPatternColsPerFile();
		final int numPatternRowsPerFile = patternPackage.getNumPatternRowsPerFile();

		while (numDotsRemainingX > 0) {
			// use up one tile, and subtract an appropriate number of columns...
			numDotsRemainingX -= numPatternColsPerFile;
			numTilesNeededX++;
		}
		final int numDotsXFromRightMostTiles = numDotsRemainingX + numPatternColsPerFile;

		while (numDotsRemainingY > 0) {
			// use up one tile, and subtract an appropriate number of rows...
			numDotsRemainingY -= numPatternRowsPerFile;
			numTilesNeededY++;
		}
		final int numDotsYFromBottomMostTiles = numDotsRemainingY + numPatternRowsPerFile;

		// the tiling is...
		// numTilesNeededX, numTilesNeededY
		// numDotsXFromRightMostTiles, numDotsYFromBottomMostTiles
		System.out.println("Tiling Information (" + horizontal + ", " + vertical + ") {");
		System.out.println("\t" + numTilesNeededX + " Tile(s) in X, with "
				+ numDotsXFromRightMostTiles + " horizontal dots from the rightmost tiles.");
		System.out.println("\t" + numTilesNeededY + " Tile(s) in Y, with "
				+ numDotsYFromBottomMostTiles + " vertical dots from the bottommost tiles.");
		System.out.println("}");
	}


	/**
	 * @return the current pattern package.
	 */
	public PatternPackage getCurrentPatternPackage() {
		return patternPackage;
	}

	/**
	 * Returned pattern that is tiled appropriately, and automatically selected from the pattern
	 * package. By default, this pattern generator class will keep track of which pattern it has
	 * given you, and will give you unique pattern (if possible) every time you call this method.
	 * 
	 * @param width
	 *            the amount of pattern we need
	 * @param height
	 * 
	 * @return
	 */
	public TiledPattern getPattern(Units width, Units height) {
		DebugUtils.println("getPattern Called " + ++numTimesGetPatternCalled + " times...");

		final long numDotsX = Math.round(width.getValueInPatternDots());
		final long numDotsY = Math.round(height.getValueInPatternDots());

		// System.out.println(numDotsX + " " + numDotsY);

		int numTilesNeededX = 0;
		int numTilesNeededY = 0;

		int numDotsRemainingX = (int) numDotsX;
		int numDotsRemainingY = (int) numDotsY;

		final int numPatternColsPerFile = patternPackage.getNumPatternColsPerFile();
		final int numPatternRowsPerFile = patternPackage.getNumPatternRowsPerFile();

		// figure out how many horizontal dots we need
		while (numDotsRemainingX > 0) {
			// use up one tile, and subtract an appropriate number of columns...
			numDotsRemainingX -= numPatternColsPerFile;
			numTilesNeededX++;
		}
		final int numDotsXFromRightMostTiles = numDotsRemainingX + numPatternColsPerFile;

		// figure out how many vertical dots we need
		while (numDotsRemainingY > 0) {
			// use up one tile, and subtract an appropriate number of rows...
			numDotsRemainingY -= numPatternRowsPerFile;
			numTilesNeededY++;
		}
		final int numDotsYFromBottomMostTiles = numDotsRemainingY + numPatternRowsPerFile;

		// if we only need one tile....
		// we would like to stay on the same pattern file if we still have space
		// calculate this from numDotsXFromRightMostTiles and numDotsYFromBottomMostTiles...
		// DebugUtils.println("NumTilesNeeded: " + numTilesNeededX + ", " + numTilesNeededY);
		if (numTilesNeededX == 1 && numTilesNeededY == 1) {
			// DebugUtils.println("DotsNeeded: x: " + numDotsXFromRightMostTiles + ", "
			// + numDotsYFromBottomMostTiles);
			// if we WILL exceed the horizontal bounds....
			if (lastDotUsedX + numDotsXFromRightMostTiles + BUFFER >= numPatternColsPerFile) {

				// wrap back to the left side of the page
				lastDotUsedX = 0;
				lastDotUsedY += maxOfRecentHeightsInDots + BUFFER;
			}

			// if we WILL exceed the height bounds...
			if (lastDotUsedY + numDotsYFromBottomMostTiles + BUFFER >= numPatternRowsPerFile) {
				lastDotUsedY = 0;
				// next time, get pattern from a new file!
				patternFileNumber++;
			}
		} else {
			// we need more than one tile...
			// CROSS OUR FINGERS for now. =)
		}

		// create and return the tiled pattern
		final TiledPattern pattern = new TiledPattern(patternPackage, patternFileNumber, //
				lastDotUsedX, lastDotUsedY, //
				numTilesNeededX, numTilesNeededY, //
				numDotsXFromRightMostTiles, numDotsYFromBottomMostTiles); //
		patternFileNumber = pattern.getLastPatternFileUsed();

		// try to shift everything right
		// we are still tiling horizontally
		// maintain our tallest Y to date
		lastDotUsedX += numDotsXFromRightMostTiles + BUFFER;
		maxOfRecentHeightsInDots = Math.max(maxOfRecentHeightsInDots, numDotsYFromBottomMostTiles
				+ BUFFER);

		return pattern;
	}

	/**
	 * @param name
	 * @return a PatternPackage, indexed by name.
	 */
	public PatternPackage getPatternPackageByName(String name) {
		return availablePackages.get(name);
	}

	/**
	 * @return
	 * @return the set of name of available pattern packages.
	 */
	public List<String> listAvailablePatternPackageNames() {
		return new ArrayList<String>(availablePackages.keySet());
	}

	/**
	 * Resets the tracked history in this object. The next call to getPattern(...) will start over
	 * at the default state after calling this function.
	 */
	public void resetUniquePatternTracker() {
		patternFileNumber = 0;
		lastDotUsedY = 0;
		lastDotUsedX = 0;
	}

	/**
	 * Choose the package of pattern data. 
	 * @param packageName
	 */
	private void setPackage(String packageName) {
		PatternPackage pkg = availablePackages.get(packageName);
		if (pkg == null) {
			pkg = new ArrayList<PatternPackage>(availablePackages.values()).get(0);
			System.err.println("Warning: " + packageName
					+ " does not exist. Setting Pattern Package to the first one available ("
					+ pkg.getName() + ").");
		}
		patternPackage = pkg;
	}

	/**
	 * Explicitly manipulate the page number which will be the source of pattern for the next call
	 * to getPattern(...)
	 * 
	 * @param num
	 */
	public void setPatternFileNumber(int num) {
		resetUniquePatternTracker();
		patternFileNumber = num;
	}

}
