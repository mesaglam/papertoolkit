package edu.stanford.hci.r3.actions.types.graphicscommands;

import java.awt.Graphics2D;
import java.awt.Shape;

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
public class DrawShape implements GraphicsCommand {

	private Shape shape;

	/**
	 * @param s
	 */
	public DrawShape(Shape s) {
		shape = s;
	}

	/**
	 * @see edu.stanford.hci.r3.actions.types.graphicscommands.GraphicsCommand#invoke(java.awt.Graphics2D)
	 */
	public void invoke(Graphics2D g2d) {
		g2d.draw(shape);
	}
}
