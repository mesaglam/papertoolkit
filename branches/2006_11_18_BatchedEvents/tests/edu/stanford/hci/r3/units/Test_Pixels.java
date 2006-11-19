package edu.stanford.hci.r3.units;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_Pixels {

	/**
	 * 
	 */
	@Test
	public void pixelsPerInchLoadingTest() {
		System.out.println("One Inch is: " + Inches.ONE.toPixels());
		System.out.println("Two Inches is: " + new Inches(2).toPixels());
		Assert.assertEquals(new Inches(2), new Pixels(188.364));
	}
}
