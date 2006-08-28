package edu.stanford.hci.r3.pattern;

import edu.stanford.hci.r3.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TiledPatternTest {
	public static void main(String[] args) {
		TiledPatternGenerator generator = new TiledPatternGenerator();
		TiledPattern pattern = generator.getPattern(new Inches(0), new Inches(0), new Inches(26),
				new Inches(22));
		System.out.println("Rows: " + pattern.getNumRows());
		System.out.println(pattern);
	}
}
