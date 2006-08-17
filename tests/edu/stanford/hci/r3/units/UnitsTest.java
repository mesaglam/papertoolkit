package edu.stanford.hci.r3.units;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class UnitsTest {

	/**
	 * Check that our inches and SI units convert nicely.
	 */
	@Test
	public void inchesToSIUnits() {
		assertEquals(new Inches().getValueInInches(), 1.0);
		assertEquals(new Inches(2).getValueInCentimeters(), 2 * 2.54);
		assertEquals(new Inches(-1.0000000), new Inches(-1));
		assertEquals(new Inches(-1.0000000), new Centimeters(-2.54));
	}


	/**
	 * Check that our SI units convert nicely.
	 */
	@Test
	public void siUnits() {
		assertEquals(new Meters().getValueInCentimeters(), 100.0);
		assertEquals(new Centimeters().getValueInMillimeters(), 10.0);
	}
}
