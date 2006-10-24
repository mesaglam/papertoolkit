package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.List;

import javax.media.jai.TiledImage;

import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;
import edu.stanford.hci.r3.util.graphics.ImageUtils;
import edu.stanford.hci.r3.util.graphics.JAIUtils;

/**
 * <p>
 * Renders Ink to a Graphics2D or PDF.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkRenderer {

	private Ink ink;

	public InkRenderer(Ink theInk) {
		ink = theInk;
	}

	/**
	 * Looks very similar to SheetRenderer's. TODO: Can we integrate this?
	 */
	public void renderToJPEG(File destJPEGFile, Pixels resolutionPixelsPerInch, Units width,
			Units height) {
		final double scale = Points.ONE.getConversionTo(resolutionPixelsPerInch);

		final int w = MathUtils.rint(width.getValueIn(resolutionPixelsPerInch));
		final int h = MathUtils.rint(height.getValueIn(resolutionPixelsPerInch));
		final TiledImage image = JAIUtils.createWritableBufferWithoutAlpha(w, h);
		final Graphics2D graphics2D = image.createGraphics();
		graphics2D.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		// transform the graphics such that we are in destUnits' pixels per inch, so that when we
		// draw 72 Graphics2D pixels from now on, it will equal the correct number of output pixels
		// in the JPEG.
		graphics2D.setTransform(AffineTransform.getScaleInstance(scale, scale));

		// render a white canvas
		graphics2D.setColor(Color.WHITE);
		graphics2D.fillRect(0, 0, w, h);

		renderToG2D(graphics2D);
		graphics2D.dispose();
		ImageUtils.writeImageToJPEG(image.getAsBufferedImage(), destJPEGFile);
		DebugUtils.println("Wrote the File");
	}

	/**
	 * @param g2d
	 */
	public void renderToG2D(Graphics2D g2d) {
		// anti-aliased, high quality rendering
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		final List<InkStroke> strokes = ink.getStrokes();

		g2d.setColor(Color.BLACK);
		
		// Each Stroke will be One PPath (it's just more efficient this way)
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

				if (Math.abs(diffFromLastX) > 500 || Math.abs(diffFromLastY) > 500) {
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
