package edu.stanford.hci.r3.util.graphics;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * For working with Shapes and Java2D Graphics.
 */
public class GraphicsUtils {

	/**
	 * Takes an input array of points, and returns a new array of points where all the values have
	 * been converted from the old unit to the new destination unit.
	 * 
	 * @param sourceUnit
	 *            the input points are defined in this unit
	 * @param destUnit
	 *            the output Point2D[] is definited in this unit.
	 * @param points
	 *            the input points.
	 * @return
	 */
	public static Point2D[] convertUnits(Units sourceUnit, Units destUnit, Point2D... points) {
		final int length = points.length;
		final Point2D[] dest = new Point2D[length];
		final double conversion = sourceUnit.getConversionTo(destUnit); // the multiplier

		for (int i = 0; i < length; i++) {
			dest[i] = new Point2D.Double(points[i].getX() * conversion, points[i].getY()
					* conversion);
		}
		return dest;
	}

	/**
	 * Creates a polygon from an array of points. It rounds off all the points' x & y values to the
	 * nearest integer, as the Java Polygons only work with ints.
	 * 
	 * @param points
	 * @return the Polygon
	 */
	public static Polygon createPolygon(Point2D... points) {
		final Polygon p = new Polygon();
		for (Point2D pt : points) {
			p.addPoint((int) Math.round(pt.getX()), (int) Math.round(pt.getY()));
		}
		return p;
	}
}
