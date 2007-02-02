package edu.stanford.hci.r3.pattern;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.coordinates.StreamedPatternCoordinates;
import edu.stanford.hci.r3.util.ArrayUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_PatternPackage {

	@Test
	public void determineOrigin() {
		int startingFile = 1;
		TiledPatternGenerator generator = new TiledPatternGenerator();
		PatternPackage pkg = generator.getCurrentPatternPackage();
		StreamedPatternCoordinates coord = pkg.getPatternCoordinateOfOriginOfFile(startingFile);
		System.out.println(coord);
	}

	/**
	 * Display some tiling information.
	 * 
	 * @return
	 */
	@Test
	public void displayTiling() {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		List<String> availablePatternPackages = generator.listAvailablePatternPackageNames();
		System.out.println("Available Pattern Packages: " + availablePatternPackages);
		PatternPackage pkg = generator.getPatternPackageByName("default");
		System.out.println("Num Rows: " + pkg.getNumPatternRowsPerFile());
		System.out.println("Num Cols: " + pkg.getNumPatternColsPerFile());
		generator.displayTilingInformation(new Inches(8.5), new Inches(11));
		generator.displayTilingInformation(new Inches(8), new Inches(8));
	}

	/**
	 * Get pattern from a file.
	 */
	@Test
	public void getPattern() {
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

	
	public static void main(String[] args) {
		new PatternPackage(new File("data/pattern/Mead5x8"));
	}
}
