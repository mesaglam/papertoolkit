package edu.stanford.hci.r3.pattern;

import java.util.List;

import edu.stanford.hci.r3.units.Inches;

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
	 * Default
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		List<String> availablePatternPackages = generator.listAvailablePatternPackageNames();
		System.out.println(availablePatternPackages);
		PatternPackage pkg = generator.getPatternPackageByName("default");
		System.out.println(pkg.getNumPatternRowsPerFile());
		System.out.println(pkg.getNumPatternColsPerFile());
		generator.displayTilingInformation(new Inches(8.5), new Inches(11));
		generator.displayTilingInformation(new Inches(8), new Inches(8));
	}
}
