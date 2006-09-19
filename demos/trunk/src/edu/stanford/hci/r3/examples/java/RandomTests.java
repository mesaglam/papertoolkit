package edu.stanford.hci.r3.examples.java;

import java.text.DecimalFormat;

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
public class RandomTests {

	public static void main(String[] args) {
		Double d = new Double(18008083247892.62344);
		
		DecimalFormat format = new DecimalFormat("#.000");
		String string = format.format(d);
		System.out.println(string);
		System.out.println(d);
	}
}
