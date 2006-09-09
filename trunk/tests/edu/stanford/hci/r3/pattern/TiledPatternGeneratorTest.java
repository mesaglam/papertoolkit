package edu.stanford.hci.r3.pattern;

import edu.stanford.hci.r3.units.Inches;

public class TiledPatternGeneratorTest {

	public static void main(String[] args) {
		TiledPatternGenerator generator = new TiledPatternGenerator();

		TiledPattern pattern = generator.getPattern(new Inches(26), new Inches(22));
		System.out.println(pattern);

		TiledPattern pattern2 = generator.getPattern(new Inches(26), new Inches(22));
		System.out.println(pattern2);
	
	}

}
