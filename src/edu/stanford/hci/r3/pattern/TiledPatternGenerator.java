package edu.stanford.hci.r3.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * The name of the default pattern package (stored in pattern/default/).
	 */
	public static final String DEFAULT_PATTERN_PACKAGE_NAME = "default";

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = new File("pattern");

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
	 * @param horizontal
	 * @param vertical
	 */
	public TiledPattern createTiledPattern(Units horizontal, Units vertical) {

		// TODO
		// Finish This

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

		return new TiledPattern();
	}

}
