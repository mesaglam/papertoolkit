package edu.stanford.hci.r3.pattern;

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
		PostscriptPatternGenerator generator = new PostscriptPatternGenerator();
		generator.getAvailablePatternPackages();
	}
}
