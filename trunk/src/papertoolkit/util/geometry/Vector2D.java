/**
 * 
 */
package papertoolkit.util.geometry;

import java.awt.geom.Point2D;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt"> BSD
 * License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Vector2D {

	/**
	 * Adds a vector to a point, resulting in a new point.
	 * 
	 * @param p
	 * @param v
	 * @return
	 */
	public static Point2D add(Point2D p, Vector2D v) {
		final Point2D result = new Point2D.Double();
		result.setLocation(p.getX() + v.x, p.getY() + v.y);
		return result;
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2D add(Vector2D a, Vector2D b) {
		final Vector2D result = new Vector2D();
		result.x = a.x + b.x;
		result.y = a.y + b.y;
		return result;
	}

	/**
	 * @return
	 */
	public static Vector2D getNormalized(Vector2D v) {
		return getScaled(1.0, v);
	}

	/**
	 * @param desiredMagnitude
	 * @return
	 */
	public static Vector2D getScaled(double desiredMagnitude, Vector2D v) {
		final double scaleFactor = desiredMagnitude / v.magnitude();
		final double newX = v.x * scaleFactor;
		final double newY = v.y * scaleFactor;
		return new Vector2D(newX, newY);
	}

	/**
	 * @param d
	 * @param vector2D
	 * @return
	 */
	public static Vector2D multiply(double d, Vector2D vector2D) {
		return new Vector2D(vector2D.x * d, vector2D.y * d);
	}

	/**
	 * Returns a - b.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Vector2D subtract(Point2D a, Point2D b) {
		final Vector2D result = new Vector2D();
		result.x = a.getX() - b.getX();
		result.y = a.getY() - b.getY();
		return result;
	}

	/**
	 * The X component.
	 */
	private double x = 0;

	/**
	 * The Y component.
	 */
	private double y = 0;

	public Vector2D() {

	}

	/**
	 * @param d
	 * @param e
	 */
	public Vector2D(double _x, double _y) {
		x = _x;
		y = _y;
	}

	public double getX() {
		return x;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return
	 */
	public double magnitude() {
		return Math.sqrt(x * x + y * y);
	}

}
