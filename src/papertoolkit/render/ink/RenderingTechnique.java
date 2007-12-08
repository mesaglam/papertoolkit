package papertoolkit.render.ink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;

import papertoolkit.pen.ink.InkStroke;

/**
 * <p>
 * Different techniques have tradeoffs w/ rendering speed and quality. They iterate through the samples of a
 * stroke to write to the Graphics2D object.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class RenderingTechnique {

	public static final Stroke DEBUG_INK_STROKE = new BasicStroke(0.7f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_ROUND);
	public static final Color DEBUG_RED = new Color(250, 215, 215, 245);
	
	public static final Stroke DEFAULT_INK_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_ROUND);
	
	private boolean debug = false;

	public void setDebug(boolean b) {
		debug = b;
	}
	
	public void render(Graphics2D g2d, final List<InkStroke> strokes) {
		if (!debug) {
			return;
		}
		
		// if debug, plot the dots!
		g2d.setStroke(DEBUG_INK_STROKE);
		Color oldColor = g2d.getColor();
		g2d.setColor(DEBUG_RED);
		for (InkStroke stroke : strokes) {
			final double[] x = stroke.getXSamples();
			final double[] y = stroke.getYSamples();
			for (int i = 0; i < stroke.getNumSamples(); i++) {
				g2d.drawOval((int) x[i]-1, (int) y[i]-1, 3, 3);
			}
		}
		g2d.setColor(oldColor);
	}
}
