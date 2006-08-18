package edu.stanford.hci.r3.core;

import java.awt.Shape;

import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * A region is defined by a Shape that outlines the region. Internally, the region remembers the
 * Units that it is represented in. Thus, you may use whatever units pleases you.
 */
public class Region {

	/**
	 * We will convert all the units to x's type. Thus, if you pass in an Inch, Centimeter, Foot,
	 * Points, we will convert everything to Inches.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public Region(Units x, Units y, Units w, Units h) {

	}

	public Region(Shape s, Units u) {

	}
}
