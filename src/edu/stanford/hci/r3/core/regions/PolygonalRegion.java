package edu.stanford.hci.r3.core.regions;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import edu.stanford.hci.r3.core.Region;
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

	/**
	 * Creates a PolygonalRegion with the designated Units u.
	 * 
	 * @param u
	 *            the units object we use to interpret the Point2D objects.
	 * @param points
	 */
	public PolygonalRegion(Units u, Point2D... points) {
		super(GraphicsUtils.createPolygon(points), u);
	}

	/**
	 * Alternate constructor, if your current units are not optimal (e.g., Inches, Centimeters, and
	 * anything really big). For destinationUnits, choose something fine, like Millimeters, Points,
	 * or your desired Screen Pixel or PrinterDPI conversion.
	 * 
	 * @param currentUnits
	 * @param destinationUnits
	 * @param points
	 */
	public PolygonalRegion(Units currentUnits, Units destinationUnits, Point2D... points) {
		this(destinationUnits, GraphicsUtils.convertUnits(currentUnits, destinationUnits, points));
	}

	/**
	 * @return the internal polygon (unscaled).
	 */
	protected Polygon getPolygon() {
		return (Polygon) getShape();
	}

	/**
	 * Please override for more interesting output.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Polygon: {");
		final Polygon poly = getPolygon();
		final double sX = getScaleX();
		final double sY = getScaleY();

		for (int i = 0; i < poly.npoints; i++) {
			sb.append("(" + poly.xpoints[i] * sX + ", " + poly.ypoints[i] * sY + ")");
		}
		sb.append("} in " + getUnitsReference().getUnitName());
		return sb.toString();
	}
}
