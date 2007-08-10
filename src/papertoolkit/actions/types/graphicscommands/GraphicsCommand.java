package papertoolkit.actions.types.graphicscommands;

import java.awt.Graphics2D;

/**
 * <p>
 * Let's support the < 10 things that we need, for now...
 * <ul>
 * <li>g2d.draw(Shape s)
 * <li>g2d.fill(Shape s)
 * <li>g2d.drawImage(image, (BufferedImageOp) null, x, y);
 * <li>g2d.drawRenderedImage(RenderedImage img, AffineTransform xform);
 * <li>g2d.setFont(Font font)
 * <li>g2d.drawString(string, x, y)
 * <li>set the JFrame's location
 * <li>set the JFrame's size
 * <li>set the JFrame's FullScreen Mode to True or False
 * </ul>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface GraphicsCommand {

	/**
	 * @param g2d
	 */
	public void invoke(Graphics2D g2d);
}
