package papertoolkit.util.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt"> BSD
 * License</a>.
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
	 * Parses strings like 2005:03:30 19:20:23 and returns a java.sql.Timestamp object.
	 * 
	 * @param timestampString
	 * @return
	 */
	public static long getTimeFromString(String timestampString) {
		// Timestamp can parse strings like: yyyy-mm-dd hh:mm:ss.fffffffff
		// so, we transform the string

		if (timestampString == null) {
			return new Timestamp(0).getTime();
		}
		if (timestampString.trim().equals("")) {
			return new Timestamp(0).getTime();
		}

		// convert all colons before space to dashes
		String ymd = timestampString.substring(0, 11);
		ymd = ymd.replaceAll(":", "-");

		// add .0 to the end
		final String hms = timestampString.substring(11) + ".000000000";

		// create a new timestamp
		final String ts = ymd + hms;

		return Timestamp.valueOf(ts).getTime();
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
	 * Gets the EXIF timestamp from a photo.
	 * 
	 * @param photoFile
	 * @return
	 */
	public static String readCaptureDateFromEXIF(File photoFile) {
		Metadata metadata = null;
		try {
			metadata = JpegMetadataReader.readMetadata(photoFile);
		} catch (JpegProcessingException e) {
			// XXX: suppress this, as we do not checking JPEGness before call
			// e.printStackTrace();
			return null;
		}

		if (metadata == null) {
			return null;
		}
		Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);

		// Time Captured
		// System.out.println(exifDirectory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED));

		// Time Uploaded
		// System.out.println(exifDirectory.getString(ExifDirectory.TAG_DATETIME));

		// Time Captured
		String ts = exifDirectory.getString(ExifDirectory.TAG_DATETIME_ORIGINAL);
		return ts;
	}

	/**
	 * @param source
	 * @return
	 */
	public static BufferedImage readImage(File source) {
		return JAIUtils.readJPEG(source).getAsBufferedImage();
	}

	/**
	 * This works for both JPEG and non JPEG images/photos. It defaults to a generic method if it cannot find
	 * the jpeg EXIF.
	 * 
	 * @param imageFile
	 * @return
	 */
	public static Dimension readSize(File imageFile) {
		if (isJPEGFile(imageFile)) {
			try {
				final Metadata metadata = JpegMetadataReader.readMetadata(imageFile);
				final Directory jpegDirectory = metadata.getDirectory(JpegDirectory.class);
				return new Dimension(jpegDirectory.getInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH), jpegDirectory
						.getInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT));
			} catch (JpegProcessingException e) {
				// fall through
			} catch (MetadataException e) {
				// fall through
			}
		}
		return readSizeByLoading(imageFile);
	}

	/**
	 * Load in the image file just to read the photo size... This should be slower than readSize(...) if most
	 * of your images are JPEG files with EXIF headers... Use it if you are loading non-JPEGS or if your JPEGs
	 * do not have EXIFs.
	 * 
	 * @param imageFile
	 * @return
	 */
	public static Dimension readSizeByLoading(File imageFile) {
		final PlanarImage planarImage = ImageCache.getInstance().getPlanarImage(imageFile);
		return new Dimension(planarImage.getWidth(), planarImage.getHeight());
	}

	/**
	 * If the EXIF field does not exist, it will return a time representing the file last modified date/time.
	 * 
	 * TODO Would users rather pick the latest valid time read from the file system (based on an alphabetical
	 * sort by name), in the same directory? Maybe it should be a configurable option in whatever software
	 * uses this method?
	 * 
	 * @param photo
	 * @return
	 */
	public static long readTimeFrom(File photo) {
		String ts = readCaptureDateFromEXIF(photo);
		if (ts == null) {
			return photo.lastModified();
		} else {
			return getTimeFromString(ts);
		}
	}

	/**
	 * Scaled a buffered image by the two scale parameters. The scale operation is anti-aliased.
	 * 
	 * @param src
	 * @param sX
	 * @param sY
	 * @return A scaled BufferedImage
	 * 
	 * @author Ron Yeh
	 */
	public static BufferedImage scaleImage(BufferedImage src, float sX, float sY) {
		System.err.println("Warning: ImageUtils::scaleImage is a low quality scale. "
				+ "Please use ImageUtils/JAIUtils::scaleImageIteratively.");

		final int width = (int) (src.getWidth() * sX + 0.5);
		final int height = (int) (src.getHeight() * sY + 0.5);
		final BufferedImage scaledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D gScaledImg = scaledImg.createGraphics();
		gScaledImg.setRenderingHints(GraphicsUtils.getBestRenderingHints());
		gScaledImg.drawImage(src, 0, 0, width, height, null);
		return scaledImg;
	}

	/**
	 * @param src
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scaleImageToFit(BufferedImage src, int width, int height) {
		return scaleImageToSize(src, width, height, true);
	}

	/**
	 * @param src
	 * @param targetWidth
	 * @param targetHeight
	 * @param maintainAspectRatio
	 * @return
	 */
	public static BufferedImage scaleImageToSize(BufferedImage src, int targetWidth, int targetHeight,
			boolean maintainAspectRatio) {
		// old width and height
		final int width = src.getWidth();
		final int height = src.getHeight();

		// the scaling factor
		final float testScaleFactorX = (float) targetWidth / (float) width;
		final float testScaleFactorY = (float) targetHeight / (float) height;

		// SystemUtilities.println(testScaleFactorX + " " + testScaleFactorY);

		float scaleFactorX = 0;
		float scaleFactorY = 0;

		// if maintaining Aspect Ratio, add some padding either on
		// the top/bottom or the left/right
		if (maintainAspectRatio) {
			float min = Math.min(testScaleFactorX, testScaleFactorY);
			scaleFactorX = min;
			scaleFactorY = min;
		} else {
			scaleFactorX = testScaleFactorX;
			scaleFactorY = testScaleFactorY;
		}

		// never scale up a photo, or scale it to the same size.. :)
		if (scaleFactorX >= 1 || scaleFactorY >= 1) {
			return src;
		}

		return scaleImage(src, scaleFactorX, scaleFactorY);
	}

	/**
	 * @param bufferedImage
	 * @param file
	 */
	public static void writeImageToJPEG(BufferedImage bufferedImage, File file) {
		writeImageToJPEG(bufferedImage, 100, file);
	}

	/**
	 * This method is generally BETTER than ImageIO.write(...) as that produces low quality output. This
	 * method will not work for a 4-banded image. The colors will look incorrect. Use the PNG version instead.
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
