package edu.stanford.hci.r3.core;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
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
	 * Internal horizontal scale of the region. When rendering, we will multiply the shape by this
	 * scale.
	 */
	protected double scaleX = 1.0;

	/**
	 * Internal vertical scale of the region. When rendering, we will multiply the shape by this
	 * scale.
	 */
	protected double scaleY = 1.0;

	/**
	 * This is the shape of the region.
	 */
	private Shape shape;

	/**
	 * This is used only to interpret the shape's true physical size. The value of the units object
	 * doesn't matter. Only the type of the unit matters.
	 */
	protected Units units;

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
	 * A protected constructor so subclasses can assign the shape whichever way they please. A
	 * Region doesn't really make sense without a shape, so use this constructor carefully (i.e.,
	 * make sure to assign a sensible shape).
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
	 * @return a scaled copy of the internal shape.
	 */
	public Shape getScaledShapeCopy() {
		final GeneralPath gp = new GeneralPath();
		gp.append(shape.getPathIterator(AffineTransform.getScaleInstance(scaleX, scaleY)), false);
		return gp;
	}

	/**
	 * @return
	 */
	public double getScaleX() {
		return scaleX;
	}

	/**
	 * @return
	 */
	public double getScaleY() {
		return scaleY;
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
	 * @return a copy of the internal shape as a Java2D GeneralPath. You should use this with
	 *         getScaleX/Y to determine the true shape. Alternatively, use getScaledShapeCopy()
	 */
	public Shape getUnscaledShapeCopy() {
		return new GeneralPath(shape);
	}

	/**
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Resets to the default scaling factors.
	 */
	public void resetScale() {
		setScale(1.0, 1.0);
	}

	/**
	 * Scales the points in the current region by a horizontal and vertical multiplier.
	 * 
	 * @param sX
	 * @param sY
	 */
	public void scaleRegion(double sX, double sY) {
		scaleX = scaleX * sX;
		scaleY = scaleY * sY;
	}

	/**
	 * Resizes the region uniformly in x and y. We actually just store the number and scale it
	 * whenever we need to render the final region. The Lazy Approach. =) This is nice because we
	 * can scale the region multiple times without getting aliasing effects.
	 * 
	 * @param scale
	 */
	public void scaleRegionUniformly(double scale) {
		scaleRegion(scale, scale);
	}

	/**
	 * Replaces the scaling factors.
	 * 
	 * @param newScaleX
	 * @param newScaleY
	 */
	public void setScale(double newScaleX, double newScaleY) {
		scaleX = newScaleX;
		scaleY = newScaleY;
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
		StringBuilder sb = new StringBuilder();
		String className = shape.getClass().getName();
		sb.append(className.substring(className.lastIndexOf(".") + 1) + ": {");
		PathIterator pathIterator = shape.getPathIterator(AffineTransform.getScaleInstance(scaleX,
				scaleY));
		double[] points = new double[6];
		while (!pathIterator.isDone()) {
			int type = pathIterator.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_CLOSE:
				sb.append("Close");
				break;
			case PathIterator.SEG_CUBICTO:
				sb.append("CubicTo: (" + points[0] + "," + points[1] + ") (" + points[2] + ","
						+ points[3] + ") (" + points[4] + "," + points[5] + ")");
				break;
			case PathIterator.SEG_LINETO:
				sb.append("LineTo: (" + points[0] + "," + points[1] + ")");
				break;
			case PathIterator.SEG_MOVETO:
				sb.append("Move: (" + points[0] + "," + points[1] + ")");
				break;
			case PathIterator.SEG_QUADTO:
				sb.append("QuadTo: (" + points[0] + "," + points[1] + ") (" + points[2] + ","
						+ points[3] + ")");
				break;
			}
			pathIterator.next();
			if (!pathIterator.isDone()) {
				sb.append(", ");
			}
		}
		sb.append("} in " + units.getUnitName());
		return sb.toString();
	}
}
