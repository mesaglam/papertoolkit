package edu.stanford.hci.r3.actions.types.graphicscommands;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import edu.stanford.hci.r3.util.graphics.ImageCache;

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
public class DrawImageCommand implements GraphicsCommand {

	private File file;

	private int x;

	private AffineTransform xform = new AffineTransform();

	private int y;

	/**
	 * 
	 */
	public DrawImageCommand(File imageFile, int xVal, int yVal) {
		file = imageFile;
		x = xVal;
		y = yVal;
	}

	/**
	 * @param imageFile
	 * @param xVal
	 * @param yVal
	 * @param transform
	 */
	public DrawImageCommand(File imageFile, int xVal, int yVal, AffineTransform transform) {
		this(imageFile, xVal, yVal);
		xform = transform;
	}

	/**
	 * @param g2d
	 */
	public void invoke(Graphics2D g2d) {
		BufferedImage image = ImageCache.loadBufferedImage(file);
		g2d.drawImage(image, xform, null /* ImageObserver */);
	}
}
