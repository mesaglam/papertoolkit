/**
 * 
 */
package edu.stanford.hci.r3.util.graphics;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.jpeg.JpegDirectory;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

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
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage createWritableBuffer(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
	}

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

	/**
	 * @param asBufferedImage
	 * @param file
	 */
	public static void writeImageToJPEG(BufferedImage asBufferedImage, File file) {
		writeImageToJPEG(asBufferedImage, 100, file);
	}

	/**
	 * This method is generally BETTER than ImageIO.write(...) as that produces low quality output.
	 * This method will not work for a 4-banded image. The colors will look incorrect. Use the PNG
	 * version instead.
	 * 
	 * @param buffImage
	 * @param quality
	 *            goes from 0 to 100
	 * @param outputFile
	 * @created Jun 21, 2006
	 * @author Ron Yeh
	 */
	public static void writeImageToJPEG(BufferedImage buffImage, int quality, File outputFile) {
		BufferedOutputStream out;

		// bounds check on quality parameter
		if (quality < 0) {
			quality = 0;
		}
		if (quality > 100) {
			quality = 100;
		}

		try {
			out = new BufferedOutputStream(new FileOutputStream(outputFile));
			final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			final JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(buffImage);
			quality = Math.max(0, Math.min(quality, 100));
			param.setQuality(quality / 100.0f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(buffImage);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param rImage
	 * @param outputFile
	 */
	public static void writeImageToPNG(RenderedImage rImage, File outputFile) {
		try {
			ImageIO.write(rImage, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
