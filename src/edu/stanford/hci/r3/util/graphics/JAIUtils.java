/**
 * 
 */
package edu.stanford.hci.r3.util.graphics;

import java.io.File;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

/**
 * <p>
 * This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt"> BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class JAIUtils {

	/**
	 * @param jpegFile
	 * @return a PlanarImage read from a File object.
	 */
	public static PlanarImage readJPEG(final File jpegFile) {
		return JAI.create("fileload", jpegFile.getAbsolutePath());
	}
}
