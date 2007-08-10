package papertoolkit.util.graphics;

import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import papertoolkit.units.Units;


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

	private static RenderingHints hintsBest;

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

	/**
	 * @return smooth as silk rendering hints, useful for nice scaling. Use this with Graphics2D, to
	 *         create nice graphics.
	 */
	public static RenderingHints getBestRenderingHints() {
		if (hintsBest == null) {
			hintsBest = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			hintsBest.add(new RenderingHints(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY));
			hintsBest.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR));
		}
		return hintsBest;
	}

	/**
	 * @param pathIterator
	 * @return
	 */
	public static String getPathAsString(PathIterator pathIterator) {
		final StringBuilder sb = new StringBuilder();
		final double[] points = new double[6];
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
		return sb.toString();
	}

	/**
	 * Recenter the points around x and y.
	 * 
	 * @param points
	 *            each point will be recalculatd relative to the new origin.
	 * @param originX
	 *            the new originX
	 * @param originY
	 * @return
	 */
	public static Point2D[] getPointsRecentered(Point2D[] points, double originX, double originY) {
		final int length = points.length;
		final Point2D[] dest = new Point2D[length];
		for (int i = 0; i < length; i++) {
			dest[i] = new Point2D.Double(points[i].getX() - originX, points[i].getY() - originY);
		}
		return dest;
	}

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
	public static Point2D[] getPointsWithConvertedUnits(Units sourceUnit, Units destUnit,
			Point2D... points) {
		final int length = points.length;
		final Point2D[] dest = new Point2D[length];
		final double conversion = sourceUnit.getScalarMultipleToConvertTo(destUnit); // the multiplier

		for (int i = 0; i < length; i++) {
			dest[i] = new Point2D.Double(points[i].getX() * conversion, points[i].getY()
					* conversion);
		}
		return dest;
	}

}
