package edu.stanford.hci.r3.core;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
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
 * 
 * Also, a region can be INPUT(REALTIME|BATCH), OUTPUT(REALTIME|INTERMITTENT), or STATIC. This is
 * entirely determined by the type(s) of attachments (input event handlers, input filters, realtime
 * outputs, print output) you add to a region. If you do not add anything, it is by default a STATIC
 * region. For example, if you add an input event handler for a streaming pen, it becomes an
 * INPUT(REALTIME) region. Regions that handle interactive input will automatically be overlaid with
 * pattern when rendered to PDF or to a printer.
 * 
 * Regardless of whether the Shape is closed or not, we assume that all regions are closed Shapes.
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
	 * By default, regions are visible (they tend to be images, pattern, etc). However, if you would
	 * like to create an invisible region, go ahead. We're not gonna stop you.=)
	 */
	private boolean visible = true;

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
	 * A protected constructor so subclasses can assign the shape whichever way they please.
	 * 
	 * @param u
	 */
	protected Region(Units u) {
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
	 * Subclasses can modify the internal shape object.
	 * 
	 * @return the internal shape, at your peril.
	 */
	protected Shape getShape() {
		return shape;
	}

	/**
	 * @return a copy of the internal shape as a Java2D GeneralPath.
	 */
	public Shape getShapeCopy() {
		return new GeneralPath(shape);
	}

	/**
	 * @return
	 */
	protected Units getUnits() {
		return units;
	}

	/**
	 * @return
	 */
	public Units getUnitsCopy() {
		return units.getCopy();
	}

	/**
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Subclasses can use this method to set the shape after constructing the object.
	 * 
	 * @param s
	 */
	protected void setShape(Shape s) {
		shape = s;
	}

	/**
	 * @param v
	 */
	public void setVisible(boolean v) {
		visible = v;
	}

	/**
	 * Please override for more interesting output.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return shape.toString() + " in " + units.getUnitName();
	}
}
