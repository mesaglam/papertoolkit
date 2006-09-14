package edu.stanford.hci.r3.paper.regions;

import java.awt.Font;

import edu.stanford.hci.r3.paper.regions.TextRegion;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_TextRegion {

	public static void main(String[] args) {
		TextRegion tr = new TextRegion("Hello World!", new Font("Comic Sans", Font.PLAIN, 44),
				new Inches(1), new Centimeters(2.54));
		System.out.println(tr.getLinesOfText().length);
	}
}
