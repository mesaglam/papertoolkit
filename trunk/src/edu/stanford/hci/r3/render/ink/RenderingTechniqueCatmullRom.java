package edu.stanford.hci.r3.render.ink;

import java.awt.Graphics2D;
import java.util.List;

import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.util.geometry.CatmullRomSpline;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
class RenderingTechniqueCatmullRom implements RenderingTechnique {
	public void render(Graphics2D g2d, List<InkStroke> strokes) {
		for (InkStroke stroke : strokes) {
			final CatmullRomSpline crspline = new CatmullRomSpline();
			final double[] x = stroke.getXSamples();
			final double[] y = stroke.getYSamples();
			crspline.setPoints(x, y);
			g2d.draw(crspline.getShape());
		}
	}
}
