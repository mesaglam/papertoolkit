package edu.stanford.hci.r3.core;

import org.junit.Test;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SheetTest {

	/**
	 * 
	 */
	//@Test
	public void printingTest() {
		System.out.println(new Sheet());
	}

	@Test
	public void addRegions() {
		Sheet sheet = new Sheet();
		sheet.addRegion(new Region(0, 0, 8.5, 11));
		System.out.println(sheet);
	}
}
