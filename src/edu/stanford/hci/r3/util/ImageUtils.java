/**
 * 
 */
package edu.stanford.hci.r3.util;

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
public class ImageUtils {

	/**
	 * @param source
	 * @return
	 */
	public static BufferedImage readImageFromFile(File source) {
		return JAIUtils.readJPEG(source).getAsBufferedImage();
	}
}
