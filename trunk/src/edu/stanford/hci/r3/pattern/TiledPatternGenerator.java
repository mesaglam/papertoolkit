package edu.stanford.hci.r3.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.config.Configuration;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class TiledPatternGenerator {

	public static final String CONFIG_PATH = "tiledpatterngenerator.patternpath";

	/**
	 * The name of the default pattern package (stored in pattern/default/).
	 */
	public static final String DEFAULT_PATTERN_PACKAGE_NAME = "default";

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = getPatternPath();

	/**
	 * @return
	 */
	private static File getPatternPath() {
		return Configuration.getConfigFile(CONFIG_PATH);
	}

	/**
	 * Packages indexed by name.
	 */
	private Map<String, PatternPackage> availablePackages;

	/**
	 * Currently selected pattern package.
	 */
	private PatternPackage patternPackage;

	/**
	 * Customize this to reflect where you store your pattern definition files.
	 */
	private File patternPath;

	/**
	 * Where we should start getting our pattern from. This is incremented by some amount every time
	 * we call getPattern(...), so that the pattern returned will be unique.
	 */
	private int patternFileNumber = 0;

	/**
	 * Default Pattern Path Location.
	 */
	public TiledPatternGenerator() {
		patternPath = PATTERN_PATH;
		availablePackages = getAvailablePatternPackages();
		setPackage(DEFAULT_PATTERN_PACKAGE_NAME);
	}

	/**
	 * Customize the location of pattern definition files.
	 */
	public TiledPatternGenerator(File patternPathLocation) {
		patternPath = patternPathLocation;
		availablePackages = getAvailablePatternPackages();
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
	 * @return the Pattern Packages that are available to the system. Packages are stored in the
	 *         directory (pattern/). We return a Map<String, PatternPackage> so you can address the
	 *         package by name.
	 */
	private Map<String, PatternPackage> getAvailablePatternPackages() {
		final HashMap<String, PatternPackage> packages = new HashMap<String, PatternPackage>();

		// list the available directories
		final List<File> visibleDirs = FileUtils.listVisibleDirs(patternPath);
		// System.out.println(visibleDirs);

		// create new PatternPackage objects from the directories
		for (File f : visibleDirs) {
			PatternPackage patternPackage = new PatternPackage(f);
			packages.put(patternPackage.getName(), patternPackage);
		}

		// return the list of packages
		return packages;
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
	 * @param height
	 * 
	 * @return
	 */
	public TiledPattern getPattern(Units width, Units height) {
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

		// create and return the tiled pattern
		// for now, always increment the file number so that we get new pattern!
		final TiledPattern pattern = new TiledPattern(patternPackage, patternFileNumber, 0, //
				numTilesNeededX, numTilesNeededY, //
				numDotsXFromRightMostTiles, numDotsYFromBottomMostTiles); //

		// next time, get new pattern from a new file!
		patternFileNumber = pattern.getLastPatternFileUsed() + 1;
		
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

}
