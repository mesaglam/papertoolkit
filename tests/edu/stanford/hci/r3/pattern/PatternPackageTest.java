package edu.stanford.hci.r3.pattern;

import java.util.List;

import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.ArrayUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PatternPackageTest {

	/**
	 * Display some tiling information.
	 * 
	 * @return
	 */
	private static TiledPatternGenerator displayTiling() {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		List<String> availablePatternPackages = generator.listAvailablePatternPackageNames();
		System.out.println(availablePatternPackages);
		PatternPackage pkg = generator.getPatternPackageByName("default");
		System.out.println(pkg.getNumPatternRowsPerFile());
		System.out.println(pkg.getNumPatternColsPerFile());
		generator.displayTilingInformation(new Inches(8.5), new Inches(11));
		generator.displayTilingInformation(new Inches(8), new Inches(8));
		return generator;
	}

	/**
	 * Get pattern from a file.
	 */
	private static void getPattern() {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		PatternPackage pkg = generator.getCurrentPatternPackage();
		System.out.println(pkg.getNumPatternRowsPerFile());
		System.out.println(pkg.getNumPatternColsPerFile());

		String[] pattern = pkg.readPatternFromFile(0, new PatternDots(0), new PatternDots(0),
				new Inches(8.5), new Inches(11));

		// String[] pattern = pkg.readPatternFromFile(0, new PatternDots(2), new PatternDots(2),
		// new PatternDots(10), new PatternDots(10));

		ArrayUtils.printArray(pattern);
		System.out.println(pattern.length);
		System.out.println(pattern[2].length());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getPattern();
	}
}
