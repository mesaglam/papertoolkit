package papertoolkit.actions.types.graphicscommands;

import java.awt.Color;
import java.awt.Graphics2D;

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
public class SetColorCommand implements GraphicsCommand {

	private Color color;

	/**
	 * @param c
	 */
	public SetColorCommand(Color c) {
		color = c;
	}

	/**
	 * @see papertoolkit.actions.types.graphicscommands.GraphicsCommand#invoke(java.awt.Graphics2D)
	 */
	public void invoke(Graphics2D g2d) {
		g2d.setColor(color);
	}
}
