package papertoolkit.render.ink;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.List;

import papertoolkit.pen.ink.InkStroke;


/**
 * <p>
 * Uses quadratic splines to render the ink. It isn't as faithful as Catmull Rom or even Linear, because the
 * line doesn't pass exactly through the ink samples, but rather, close to them.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
class RenderingTechniqueQuadratic extends RenderingTechnique {

	private static int ERROR_THRESHOLD = 500;

	public void render(Graphics2D g2d, final List<InkStroke> strokes) {
		super.render(g2d, strokes);
		
		g2d.setStroke(DEFAULT_INK_STROKE);
		
		// Each Stroke will be One Path (it's just more efficient this way)
		for (final InkStroke s : strokes) {

			final double[] xArr = s.getXSamples();
			final double[] yArr = s.getYSamples();

			final GeneralPath strokePath = new GeneralPath();
			final int len = xArr.length;
			if (len > 0) {
				strokePath.moveTo(xArr[0], yArr[0]);
			}

			// keeps last known "good point"
			double lastGoodX = xArr[0];
			double lastGoodY = yArr[0];

			// connect the samples w/ quadratic curve segments
			// in the future, do catmull-rom, because that's ideal...
			int numPointsCollected = 0;
			for (int i = 0; i < len; i++) {
				final double currX = xArr[i];
				final double currY = yArr[i];

				numPointsCollected++;

				final double diffFromLastX = currX - lastGoodX;
				final double diffFromLastY = currY - lastGoodY;

				if (Math.abs(diffFromLastX) > ERROR_THRESHOLD || Math.abs(diffFromLastY) > ERROR_THRESHOLD) {
					// too much error; eliminate totally random data...
					// this usually arises from writing outside the margin onto disjoint pattern
					// (like the anoto pidget)
					// try just discarding this point!
					// strokePath.lineTo(lastGoodX, lastGoodY);
				} else {
					// OK, not that much error
					if (numPointsCollected == 2) {
						numPointsCollected = 0;
						strokePath.quadTo(lastGoodX, lastGoodY, currX, currY);
					}

					// set the last known good point
					lastGoodX = currX;
					lastGoodY = currY;
				}
			}

			// if there's any points left, just render them
			if (numPointsCollected == 1) {
				strokePath.lineTo(lastGoodX, lastGoodY);
			}
			g2d.draw(strokePath);
		}
	}

}
