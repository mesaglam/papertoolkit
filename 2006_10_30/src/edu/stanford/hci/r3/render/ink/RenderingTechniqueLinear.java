package edu.stanford.hci.r3.render.ink;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import edu.stanford.hci.r3.pen.ink.InkStroke;

/**
 * <p>
 * Simple Line-To Rendering.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
class RenderingTechniqueLinear implements RenderingTechnique {

	public void render(Graphics2D g2d, List<InkStroke> strokes) {
		for (InkStroke stroke : strokes) {
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
