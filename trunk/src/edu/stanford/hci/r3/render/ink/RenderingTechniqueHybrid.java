package edu.stanford.hci.r3.render.ink;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.ink.InkUtils;
import edu.stanford.hci.r3.util.geometry.CatmullRomSpline;

/**
 * <p>
 * This uses Linear when points are close together, but goes to Catmull Rom when they are far apart.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
class RenderingTechniqueHybrid implements RenderingTechnique {

	public void render(Graphics2D g2d, List<InkStroke> strokes) {
		for (InkStroke stroke : strokes) {
			double width = stroke.getWidth();
			g2d.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			// this doubles as a good max velocity measure, since the samples come in at more or less a
			// constant rate. If the max velocity is too high, we should probably use catmull rom.
			// TODO: In the future, we should hybridize at an even finer granularity (i.e. within stroke)
			// right now, we are hybridizing between strokes.
			double maxDistanceBetweenSamples = InkUtils.getMaxDistanceBetweenSamples(stroke);
			// DebugUtils.println("MaxD: " + maxDistanceBetweenSamples);

			boolean largeStroke = maxDistanceBetweenSamples > 75;
			if (largeStroke) {
				final CatmullRomSpline crspline = new CatmullRomSpline();
				final double[] x = stroke.getXSamples();
				final double[] y = stroke.getYSamples();
				crspline.setPoints(x, y);
				g2d.draw(crspline.getShape());
			} else {
				final Path2D.Double path = new Path2D.Double();
				final double[] x = stroke.getXSamples();
				final double[] y = stroke.getYSamples();
				path.moveTo(x[0], y[0]);
				for (int i = 1; i < stroke.getNumSamples(); i++) {
					path.lineTo(x[i], y[i]);
				}
				g2d.draw(path);
			}
		}
	}
}
