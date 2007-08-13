package papertoolkit.demos.gigaprints2006.twistr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

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
public class PImageWithHighlight extends PImage {

	private static final Color NOT_OK = new Color(220, 120, 100, 200);

	private static final Color OK = new Color(120, 120, 200, 200);

	private boolean incorrectlySelected = false;

	private boolean selected = false;

	/**
	 * @param image
	 */
	public PImageWithHighlight(BufferedImage image) {
		super(image);
	}

	/**
	 * @see edu.umd.cs.piccolo.nodes.PImage#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paint(PPaintContext ppc) {
		super.paint(ppc);
		final Graphics2D g2d = ppc.getGraphics();
		final double h = getHeight() * 0.05;

		// ONLY IF WE CLICKED ON THE RIGHT ONE!
		if (selected) {
			g2d.setColor(OK);
			g2d.fillRect(-1, -1, (int) getWidth() + 2, (int) h + 2);
			g2d.fillRect(-1, (int) (0.95 * getHeight() - 1), (int) getWidth() + 2, (int) h + 2);
		} else if (incorrectlySelected) {
			g2d.setColor(NOT_OK);
			g2d.fillRect(-1, -1, (int) getWidth() + 2, (int) h + 2);
			g2d.fillRect(-1, (int) (0.95 * getHeight() - 1), (int) getWidth() + 2, (int) h + 2);
		}
	}

	public void setIncorrectlySelected(boolean flag) {
		incorrectlySelected = flag;
		repaint();
	}

	/**
	 * @param flag
	 */
	public void setSelected(boolean flag) {
		selected = flag;
		repaint();
	}
}
