package edu.stanford.hci.r3.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Given input dimensions, this class will generate a postscript file of tiled Anoto pattern.
 */
public class PostscriptPatternGenerator {

	/**
	 * The name of the default pattern package (stored in pattern/default/).
	 */
	public static final String DEFAULT_PATTERN_PACKAGE_NAME = "default";

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = new File("pattern");

	/**
	 * Customize this to reflect where you store your pattern definition files.
	 */
	private File patternPath;

	/**
	 * Default Pattern Path Location.
	 */
	public PostscriptPatternGenerator() {
		patternPath = PATTERN_PATH;
	}

	/**
	 * Customize the location of pattern definition files.
	 */
	public PostscriptPatternGenerator(File patternPathLocation) {
		patternPath = patternPathLocation;
	}

	/**
	 * @return a list of Pattern Packages that are available to the system. Packages are stored in
	 */
	public List<PatternPackage> getAvailablePatternPackages() {

		final List<PatternPackage> packages = new ArrayList<PatternPackage>();

		// list the available directories
		final List<File> visibleDirs = FileUtils.listVisibleDirs(patternPath);
		// System.out.println(visibleDirs);

		
		// create new PatternPackage objects from the directories
		for (File f : visibleDirs) {
			packages.add(new PatternPackage(f));
		}
		
		// return the list of packages
		return packages;
	}
}
