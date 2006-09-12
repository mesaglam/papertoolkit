package edu.stanford.hci.r3.pattern;

import org.junit.Test;

import edu.stanford.hci.r3.units.Inches;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_TiledPatternGenerator {

	public static void main(String[] args) {
	}

	@Test
	public void threeSmallStrips() {
		TiledPatternGenerator generator = new TiledPatternGenerator();

		TiledPattern pattern = generator.getPattern(new Inches(26), new Inches(2));
		System.out.println(pattern);

		TiledPattern pattern2 = generator.getPattern(new Inches(26), new Inches(2));
		System.out.println(pattern2);

		// if you don't have enough pattern files, a warning will be raised and sent to the console
		TiledPattern pattern3 = generator.getPattern(new Inches(26), new Inches(2));
		System.out.println(pattern3);
	}

	/**
	 * Try to get three chunks of pattern.
	 */
	@Test
	public void threeBigChunks() {
		TiledPatternGenerator generator = new TiledPatternGenerator();

		TiledPattern pattern = generator.getPattern(new Inches(26), new Inches(22));
		System.out.println(pattern);

		TiledPattern pattern2 = generator.getPattern(new Inches(26), new Inches(22));
		System.out.println(pattern2);

		// if you don't have enough pattern files, a warning will be raised and sent to the console
		TiledPattern pattern3 = generator.getPattern(new Inches(26), new Inches(22));
		System.out.println(pattern3);
	}

}
