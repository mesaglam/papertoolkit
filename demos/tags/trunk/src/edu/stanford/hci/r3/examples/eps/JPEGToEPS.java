/**
 * 
 */
package edu.stanford.hci.r3.examples.eps;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jibble.epsgraphics.EpsGraphics2D;

import edu.stanford.hci.r3.util.graphics.ImageUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class JPEGToEPS {

	/**
	 * Aug 14, 2006
	 */
	public static void main(String[] args) {
		try {
			final File file = new File("testData/dragon.eps");
			EpsGraphics2D g = new EpsGraphics2D("JPEG Image", file, 0, 0, 144, 72);
			BufferedImage img = ImageUtils.readImage(new File("testData/dragonSmall.png"));
			g.drawImage(img, new AffineTransform(), null);
			g.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
