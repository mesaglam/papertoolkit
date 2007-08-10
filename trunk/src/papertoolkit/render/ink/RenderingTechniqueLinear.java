package papertoolkit.render.ink;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import papertoolkit.pen.ink.InkStroke;


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

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.render.ink.RenderingTechnique#render(java.awt.Graphics2D, java.util.List)
	 */
	public void render(Graphics2D g2d, List<InkStroke> strokes) {
		g2d.setStroke(DEFAULT_INK_STROKE);
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
