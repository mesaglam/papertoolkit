package edu.stanford.hci.r3.actions.types.graphicscommands;

import java.awt.Graphics2D;

/**
 * <p>
 * Fills a rectangle into the provided g2d.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FillRectMethod extends GraphicsCommand {

	private int h;

	private int w;

	private int x;

	private int y;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public FillRectMethod(int xVal, int yVal, int wVal, int hVal) {
		x = xVal;
		y = yVal;
		w = wVal;
		h = hVal;
	}

	@Override
	public void invoke(Graphics2D g2d) {
		g2d.fillRect(x, y, w, h);
	}
}
