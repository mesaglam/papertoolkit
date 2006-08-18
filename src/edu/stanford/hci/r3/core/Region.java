package edu.stanford.hci.r3.core;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.stanford.hci.r3.units.Inches;
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
	 * This is the shape of the region.
	 */
	private Shape shape;

	/**
	 * This is used only to interpret the shape's true physical size.
	 */
	private Units units;

	/**
	 * Bye default, regions are visible (they tend to be images, pattern, etc). However, if you
	 * would like to create an invisible region, go ahead. We're not gonna stop you.=)
	 */
	private boolean visible = true;

	/**
	 * 
	 * @param s
	 *            the shape that defines this region.
	 * @param u
	 */
	public Region(Shape s, Units u) {
		shape = s;
		units = u;
	}

	/**
	 * We will convert all the units to x's type. Thus, if you pass in an Inch, Centimeter, Foot,
	 * Points, we will convert everything to Inches. It's OK to keep the x object around, because we
	 * only use it to interpret the shape object.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public Region(Units x, Units y, Units w, Units h) {
		this(new Rectangle2D.Double(x.getValue(), y.getValueIn(x), // assume a Rectangle2D
				w.getValueIn(x), h.getValueIn(x)), x);
	}

	/**
	 * For our American friends.
	 * 
	 * @param xInches
	 * @param yInches
	 * @param wInches
	 * @param hInches
	 */
	public Region(double xInches, double yInches, double wInches, double hInches) {
		this(new Rectangle2D.Double(xInches, yInches, wInches, hInches), Inches.ONE);
	}

	/**
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param v
	 */
	public void setVisible(boolean v) {
		visible = v;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return shape.toString() + " in " + units.getUnitName();
	}
}
