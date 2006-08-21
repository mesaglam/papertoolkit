/**
 * 
 */
package edu.stanford.hci.r3.util.graphics;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.media.jai.PlanarImage;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.jpeg.JpegDirectory;

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
	 * @return whether or not a file is a JPEG (just based on file extensions of .jpeg or .jpg) =]
	 */
	public static boolean isJPEGFile(File imageFile) {
		final String fileName = imageFile.getName().toLowerCase();
		if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param source
	 * @return
	 */
	public static BufferedImage readImage(File source) {
		return JAIUtils.readJPEG(source).getAsBufferedImage();
	}

	/**
	 * This works for both JPEG and non JPEG images/photos. It defaults to a generic method if it
	 * cannot find the jpeg EXIF.
	 * 
	 * @param imageFile
	 * @return
	 */
	public static Dimension readSize(File imageFile) {
		if (isJPEGFile(imageFile)) {
			try {
				final Metadata metadata = JpegMetadataReader.readMetadata(imageFile);
				final Directory jpegDirectory = metadata.getDirectory(JpegDirectory.class);
				return new Dimension(jpegDirectory.getInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH),
						jpegDirectory.getInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT));
			} catch (JpegProcessingException e) {
				// fall through
			} catch (MetadataException e) {
				// fall through
			}
		}
		return readSizeByLoading(imageFile);
	}

	/**
	 * Load in the image file just to read the photo size... This should be slower than
	 * readSize(...) if most of your images are JPEG files with EXIF headers... Use it if you are
	 * loading non-JPEGS or if your JPEGs do not have EXIFs.
	 * 
	 * @param imageFile
	 * @return
	 */
	public static Dimension readSizeByLoading(File imageFile) {
		final PlanarImage planarImage = ImageCache.getInstance().getPlanarImage(imageFile);
		return new Dimension(planarImage.getWidth(), planarImage.getHeight());
	}

}
