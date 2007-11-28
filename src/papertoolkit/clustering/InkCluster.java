package papertoolkit.clustering;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Not very different from Ink....
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkCluster {

	/**
	 * Most recent timestamp in this Ink cluster.
	 */
	private long maxTS = Long.MIN_VALUE;

	/**
	 * The bounds of this ink collection. This is the rightmost x coordinate of any sample in this collection
	 * of strokes.
	 */
	private double maxX = Double.MIN_VALUE;

	/**
	 * 
	 */
	private double maxY = Double.MIN_VALUE;

	/**
	 * Earliest Timestamp in this Ink cluster.
	 */
	private long minTS = Long.MAX_VALUE;

	/**
	 * 
	 */
	private double minX = Double.MAX_VALUE;

	/**
	 * 
	 */
	private double minY = Double.MAX_VALUE;

	/**
	 * An Ink object is essentially a list of InkStroke objects.
	 */
	private List<InkStroke> strokes;

	/**
	 * New ink object w/ an empty array of strokes.
	 */
	public InkCluster() {
		this(new ArrayList<InkStroke>());
	}

	/**
	 * @param theStrokes
	 */
	public InkCluster(List<InkStroke> theStrokes) {
		strokes = theStrokes;

		for (InkStroke s : theStrokes) {
			updateMinAndMax(s);
		}
	}

	public void addStroke(InkStroke s) {
		strokes.add(s);
		updateMinAndMax(s);
	}

	public int distanceInSpace(InkStroke stroke) {
		if (strokes.size() == 0) {
			return 0;
		}

		Rectangle2D strokeBounds = stroke.getBounds();
		if (getBounds().intersects(strokeBounds)) {
			return 0;
		}

		double hDist = 0;
		double vDist = 0;

		// find farthest perpendicular distance... :-)
		// check if they vertically "intersect"
		double sMinY = stroke.getMinY();
		double sMaxY = stroke.getMaxY();
		if (sMinY > maxY || sMaxY < minY) {
			// no vertical overlap
			vDist = Math.min(Math.abs(sMinY - maxY), Math.abs(sMaxY - minY));
		} else {
			vDist = 0;
		}

		// check if they horizontally "intersect"
		double sMinX = stroke.getMinX();
		double sMaxX = stroke.getMaxX();
		if (sMinX > maxX || sMaxX < minX) {
			// no horizontal overlap
			hDist = Math.min(Math.abs(sMinX - maxX), Math.abs(sMaxX - minX));
		} else {
			hDist = 0;
		}
		
		return (int) Math.max(hDist, vDist);
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * @param stroke
	 * @return
	 */
	public long distanceInTime(InkStroke stroke) {
		if (strokes.size() == 0) {
			return 0;
		}

		long strokeMin = stroke.getFirstTimestamp();
		long strokeMax = stroke.getLastTimestamp();

		// if we overlap, return 0
		if (strokeMin >= minTS && strokeMin <= maxTS) {
			return 0;
		}
		if (strokeMax >= minTS && strokeMax <= maxTS) {
			return 0;
		}

		// if no overlap, return the min distance
		long a = Math.abs(strokeMin - minTS);
		long b = Math.abs(strokeMin - maxTS);

		long c = Math.abs(strokeMax - minTS);
		long d = Math.abs(strokeMax - maxTS);

		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	public Rectangle2D getBoundingBoxOfInkStrokes() {
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	public int getNumInkStrokes() {
		return strokes.size();
	}

	private void updateMinAndMax(InkStroke s) {
		if (s == null) {
			DebugUtils.println("Warning: Detected a NULL Ink stroke.");
			return;
		}

		// update maxs and mins
		minX = Math.min(s.getMinX(), minX);
		minY = Math.min(s.getMinY(), minY);
		maxX = Math.max(s.getMaxX(), maxX);
		maxY = Math.max(s.getMaxY(), maxY);
		minTS = Math.min(s.getFirstTimestamp(), minTS);
		maxTS = Math.max(s.getLastTimestamp(), maxTS);
	}
}
