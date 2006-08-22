package edu.stanford.hci.r3.util.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ImageUtilsTest {

	public static void main(String[] args) {
		BufferedImage image = ImageUtils.createWritableBuffer(640, 480);
		Graphics2D graphics2D = image.createGraphics();
		graphics2D.setColor(new Color(123, 140, 180, 120));
		graphics2D.fillRect(10, 10, 100, 34);
		graphics2D.setColor(new Color(244, 244, 123, 100));
		graphics2D.fillRect(50, 80, 1500, 300);
		ImageUtils.writeImageToPNG(image, new File("testData/Test.png"));
	}
}
