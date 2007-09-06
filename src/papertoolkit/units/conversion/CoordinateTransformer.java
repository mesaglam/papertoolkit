package papertoolkit.units.conversion;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

import papertoolkit.pen.PenSample;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;

/**
 * <p>
 * Scale, Translate, and Rotate coordinates....
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CoordinateTransformer {

	private AffineTransform transform;

	/**
	 * Uses an internal AffineTransform object to do the math.
	 */
	public CoordinateTransformer() {
		transform = new AffineTransform();
	}

	/**
	 * @param oldInk
	 * @return
	 */
	public Ink convert(Ink oldInk) {
		// convert each of the strokes, and add it to the new Ink object
		List<InkStroke> strokes = oldInk.getStrokes();
		Ink newInk = new Ink();
		for (InkStroke s : strokes) {
			newInk.addStroke(convert(s));
		}
		return newInk;
	}

	/**
	 * @param oldCoordinates
	 * @return
	 */
	public InkStroke convert(InkStroke oldCoordinates) {
		List<PenSample> samples = oldCoordinates.getSamples();
		InkStroke newCoordinates = new InkStroke();
		Point2D src = new Point2D.Double();
		Point2D dest = new Point2D.Double();
		for (PenSample s : samples) {
			src.setLocation(s.x, s.y);
			transform.transform(src, dest);
			newCoordinates.addSample(new PenSample(dest.getX(), dest.getY(), s.force, s.timestamp));
		}
		return newCoordinates;
	}

	/**
	 * @param angleDegrees
	 * @param anchorPtX
	 * @param anchorPtY
	 */
	public void rotate(double angleDegrees, double anchorPtX, double anchorPtY) {
		transform.rotate(Math.toRadians(angleDegrees), anchorPtX, anchorPtY);
	}

	/**
	 * @param sx
	 * @param sy
	 */
	public void scale(double sx, double sy) {
		transform.scale(sx, sy);
	}

	/**
	 * @param tx
	 * @param ty
	 */
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}
}
