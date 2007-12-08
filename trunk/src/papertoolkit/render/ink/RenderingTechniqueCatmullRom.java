package papertoolkit.render.ink;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.List;

import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.geometry.CatmullRomSpline;


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
class RenderingTechniqueCatmullRom extends RenderingTechnique {

	public void render(Graphics2D g2d, List<InkStroke> strokes) {
		super.render(g2d, strokes);
		for (InkStroke stroke : strokes) {
			double width = stroke.getWidth();
			g2d.setStroke(new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
			final CatmullRomSpline crspline = new CatmullRomSpline();
			final double[] x = stroke.getXSamples();
			final double[] y = stroke.getYSamples();
			crspline.setPoints(x, y);
			g2d.draw(crspline.getShape());
		}
	}
}
