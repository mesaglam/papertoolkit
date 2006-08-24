package edu.stanford.hci.r3.core.regions;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import edu.stanford.hci.r3.core.Region;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.render.regions.PolygonRenderer;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * Polygonal Regions store the individual vertices and the units to interpret the vertices.
 * 
 * WARNING: Java2D Polygons only accept integer values! Thus, it might not be smart to pass in
 * fractional values (even though we accept Floats/Doubles). We WILL round off each input value with
 * Math.round(...). So, if you pass in a point to the main constructor PolygonalRegion(Units,
 * Point2D) with vertices such as 0.45 inches, your vertex may be snapped to 0.0 inches!
 * 
 * Instead, you may use the alternate convenience constructor PolygonalRegion(Units current, Units
 * destination, Point2D). This constructor will take in your Polygon in the current units, and
 * convert all the values to the (presumably finer) destination unit. For example, to convert your
 * Points from Inches to Points, call PolygonalRegion(new Inches(), new Points(), Point2D vertex1,
 * Point2D vertex2, ...). Of course, with Java's varargs, you can pass in a Point2D[] as the final
 * argument.
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PolygonalRegion extends Region {

	private static double getMinX(Point2D[] points) {
		double minX = Double.MAX_VALUE;
		for (Point2D p : points) {
			minX = Math.min(minX, p.getX());
		}
		return minX;
	}

	private static double getMinY(Point2D[] points) {
		double minY = Double.MAX_VALUE;
		for (Point2D p : points) {
			minY = Math.min(minY, p.getY());
		}
		return minY;
	}

	private Units offsetX;

	private Units offsetY;

	/**
	 * Creates a polygon with origin at the first point that was passed in.
	 * 
	 * @param u
	 * @param points
	 */
	public PolygonalRegion(Units u, Point2D... points) {
		this(u, u.getUnitsObjectOfSameTypeWithValue(getMinX(points)), u
				.getUnitsObjectOfSameTypeWithValue(getMinY(points)), points);
	}

	/**
	 * Alternate constructor, if your current units are not optimal (e.g., Inches, Centimeters, and
	 * anything really big). For destinationUnits, choose something fine, like Millimeters, Points,
	 * or your desired Screen Pixel or PrinterDPI conversion.
	 * 
	 * @param currUnits
	 * @param destUnits
	 * @param pts
	 */
	public PolygonalRegion(Units currUnits, Units destUnits, Point2D... pts) {
		this(destUnits, GraphicsUtils.getPointsWithConvertedUnits(currUnits, destUnits, pts));
	}

	/**
	 * Creates a PolygonalRegion with the designated Units u. Note that WEIRD things can happen if
	 * you set your origin (i.e., center of scaling) at a different place than the upper left corner
	 * of the bounding box of your polygon. Thus, we reserve this constructor to be used only
	 * internally.
	 * 
	 * @param u
	 *            the units object we use to interpret the Point2D objects.
	 * @param originX
	 *            The origins are used ONLY when rendering. They serve as offsets for the polygon.
	 * @param originY
	 * @param points
	 */
	private PolygonalRegion(Units u, Units originX, Units originY, Point2D... points) {
		super(GraphicsUtils.createPolygon(points), u);

		// save these around for later
		offsetX = originX;
		offsetY = originY;
	}

	/**
	 * @return
	 */
	public Units getOffsetX() {
		return offsetX;
	}

	/**
	 * @return
	 */
	public Units getOffsetY() {
		return offsetY;
	}

	/**
	 * @return the internal polygon (unscaled).
	 */
	public Polygon getPolygonReference() {
		return (Polygon) getShape();
	}

	/**
	 * @see edu.stanford.hci.r3.core.Region#getRenderer()
	 */
	public RegionRenderer getRenderer() {
		return new PolygonRenderer(this);
	}

	/**
	 * Please override for more interesting output.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Polygon: {");
		final Polygon poly = getPolygonReference();
		final double sX = getScaleX();
		final double sY = getScaleY();

		for (int i = 0; i < poly.npoints; i++) {
			sb.append("(" + poly.xpoints[i] * sX + ", " + poly.ypoints[i] * sY + ")");
		}
		sb.append("} in " + getUnitsReference().getUnitName());
		return sb.toString();
	}
}
