package edu.stanford.hci.r3.render.ink;

import java.awt.Graphics2D;
import java.util.List;

import edu.stanford.hci.r3.pen.ink.InkStroke;

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
public interface RenderingTechnique {

	public void render(Graphics2D g2d, final List<InkStroke> strokes);
	
}
