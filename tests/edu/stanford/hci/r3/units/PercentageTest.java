package edu.stanford.hci.r3.units;

import org.junit.Test;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PercentageTest {

	/**
	 * 
	 */
	@Test
	public void percentageTest() {
		Percentage p = new Percentage(75, new Centimeters(10));
		System.out.println(p);
		System.out.println(p.getValueInInches());
		System.out.println(p.getValueInCentimeters());
		
	}
}
