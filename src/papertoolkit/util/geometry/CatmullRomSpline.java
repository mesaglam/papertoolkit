package papertoolkit.util.geometry;

import java.awt.Shape;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A Catmull-Rom Spline. Great for Handwriting. It's a basic implementation, so it might not be optimized for
 * performance yet.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CatmullRomSpline {

	private List<Vector2D> backwardTangents = new ArrayList<Vector2D>();

	private List<Point2D> controlPoints = new ArrayList<Point2D>();

	private List<Vector2D> forwardTangents = new ArrayList<Vector2D>();

	private GeneralPath linePath = new GeneralPath();

	private GeneralPath path = new GeneralPath();

	private List<Point2D> pathPoints = new ArrayList<Point2D>();

	// .3 is pretty good :)
	private double tension = .3;

	public List<Vector2D> getBackwardTangents() {
		return backwardTangents;
	}

	public List<Point2D> getControlPoints() {
		return controlPoints;
	}

	/**
	 * @return
	 */
	public List<Vector2D> getForwardTangents() {
		return forwardTangents;
	}

	/**
	 * @return
	 */
	public Shape getLineShape() {
		return linePath;
	}

	/**
	 * @return a copy of the path's points
	 */
	public List<Point2D> getPathPoints() {
		return pathPoints;
	}

	/**
	 * @return
	 */
	public Shape getShape() {
		return path;
	}

	/**
	 * Connect the points into line segments.
	 * 
	 * @param points
	 */
	private void makeConnectedLines() {
		for (int i = 0; i < pathPoints.size() - 1; i++) {
			Point2D p0 = pathPoints.get(i);
			Point2D p1 = pathPoints.get(i + 1);

			linePath.append(new Line2D.Double(p0, p1), false);

			p0 = p1;
		}
	}

	/**
	 * 
	 */
	private void makePiecewiseBezier() {
		path.reset();

		// for each bezier segment
		// pull one point
		// pull two control points
		// pull next point

		Point2D p1;
		Point2D c1;
		Point2D c2;
		Point2D p2;
		int j = 0; // control points index
		for (int i = 0; i < pathPoints.size() - 1; i++) {
			p1 = pathPoints.get(i);
			c1 = controlPoints.get(j++); // get j, and increment it
			c2 = controlPoints.get(j++);
			p2 = pathPoints.get(i + 1);

			path.append(//
					new CubicCurve2D.Double(//
							p1.getX(), p1.getY(),//
							c1.getX(), c1.getY(),//
							c2.getX(), c2.getY(),//
							p2.getX(), p2.getY()), //
					false);
		}
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setPoints(double[] x, double[] y) {
		List<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0; i < x.length; i++) {
			points.add(new Point2D.Double(x[i], y[i]));
		}
		setPoints(points);
	}

	/**
	 * TODO: Create a different hybridized spline, that will only go to Catmull-Rom if the points are far
	 * enough apart. Otherwise, it will just default to lineTos...
	 * 
	 * @param points
	 */
	public void setPoints(List<Point2D> points) {
		path.reset();
		pathPoints.clear();

		pathPoints.addAll(points);

		// if a single point, draw a dot
		if (points.size() == 1) {
			final Point2D point = points.get(0);
			path.append(new Line2D.Double(point, point), false);
			return;
		}

		// if two points, draw a line segment
		if (points.size() == 2) {
			final Point2D point1 = points.get(0);
			final Point2D point2 = points.get(1);
			path.append(new Line2D.Double(point1, point2), false);
			return;
		}

		// if three points or more, do the piecewise business...
		// construct piecewise cubic bezier curves that generate the catmull-rom

		// tack on an extra last point, to enable the calculation of the last tangent
		Point2D pSecondToLast = points.get(points.size() - 2);
		Point2D pLast = points.get(points.size() - 1);
		points.add(Vector2D.add(pLast, Vector2D.subtract(pLast, pSecondToLast)));

		// p0 is the previous point
		Point2D p0 = Vector2D.add(points.get(0), Vector2D.subtract(points.get(0), points.get(1)));
		// p1 is the current point
		Point2D p1 = points.get(0);
		// p2 is the next point
		Point2D p2;

		// add tangents and control points
		for (int i = 0; i < points.size() - 1; i++) {
			p2 = points.get(i + 1);

			// === Backwards ===
			// add a backward tangent for each point, equal to the point before minus the point
			// after
			final Vector2D bkwd = Vector2D.getScaled(tension * Vector2D.subtract(p0, p1).magnitude(),
					Vector2D.subtract(p0, p2));
			backwardTangents.add(bkwd);

			// make a backward control point by adding it to the current point
			controlPoints.add(Vector2D.add(p1, bkwd));

			// === Forwards ===
			// add a tangent for each point, that is equal to the point after minus the point before
			// length is equal to half the distance to the next point
			final Vector2D fwd = Vector2D.getScaled(tension * Vector2D.subtract(p2, p1).magnitude(), Vector2D
					.subtract(p2, p0));
			forwardTangents.add(fwd);

			// make a forward control point by adding it to the current point
			controlPoints.add(Vector2D.add(p1, fwd));

			// shift the points over
			p0 = p1;
			p1 = p2;
		}

		// delete first and last control points (as they are useless)
		controlPoints.remove(0);
		controlPoints.remove(controlPoints.size() - 1);

		// don't remove tangents

		// line segments
		makeConnectedLines();

		// general path, piecewise cubic bezier
		makePiecewiseBezier();
	}
}
