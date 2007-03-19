/**
 * 
 */
package edu.stanford.hci.r3.util.graphics;

import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class JAIUtils {

	/**
	 * border extender, reflects the pixels like a mirror.
	 */
	private static final RenderingHints RH_BORDER_REFLECT = new RenderingHints(
			JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));

	/**
	 * Creates an alpha channel to write translucent images to.
	 * 
	 * @param width
	 * @param height
	 * @return a buffer that you can get a Graphics2D context from, and write to!
	 */
	public static TiledImage createWritableBuffer(int width, int height) {
		return createWritableBuffer(width, height, 4);
	}

	/**
	 * @param width
	 * @param height
	 * @param numBands
	 *            generally, use 4 (with alpha) or 3
	 * @return
	 */
	private static TiledImage createWritableBuffer(int width, int height, int numBands) {
		// The ColorModel
		final ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
		final boolean hasAlpha = (numBands > 3) ? true : false;
		final ColorModel colorModel = new ComponentColorModel(new ICC_ColorSpace(profile),
				hasAlpha, /* premultipliedAlpha */
				false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		// The SampleModel
		final int defaultTileSize = 128;
		final int[] bandOffsets = new int[numBands];
		for (int i = 0; i < numBands; i++) {
			bandOffsets[i] = numBands - 1 - i;
		}

		final SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
				defaultTileSize, defaultTileSize /* heightOfTile */, numBands /* pixelStride */,
				numBands * defaultTileSize, bandOffsets);

		return new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
	}

	/**
	 * Even without an alpha channel, you can do alpha compositing! However, it won't work well if
	 * you want a transparent background for PNGs.
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static TiledImage createWritableBufferWithoutAlpha(int width, int height) {
		return createWritableBuffer(width, height, 3);
	}

	/**
	 * Create a black image with the given width, height, and color depth (use 3 for an RGB image, 4
	 * for RGBA).
	 * 
	 * @param width
	 * @param height
	 * @param numBands
	 * @return
	 */
	public static PlanarImage createZeroImage(int width, int height, int numBands) {
		final Byte[] bandValues = new Byte[numBands];
		for (int i = 0; i < numBands; i++) {
			bandValues[i] = new Byte((byte) 0);
		}
		final ParameterBlock pb = new ParameterBlock();
		pb.add(new Float(width));
		pb.add(new Float(height));
		pb.add(bandValues);
		return (PlanarImage) JAI.create("constant", pb);
	}

	/**
	 * @param jpegFile
	 * @return a PlanarImage read from a File object.
	 */
	public static PlanarImage readJPEG(final File jpegFile) {
		return JAI.create("fileload", jpegFile.getAbsolutePath());
	}

	/**
	 * performs a JAI scaling of the PlanarImage with appropriate scale factors
	 * 
	 * @param src
	 * @param interpQuality
	 * @param scaleFactorX
	 * @param scaleFactorY
	 * @return
	 */
	public static PlanarImage scaleImage(PlanarImage src, InterpolationQuality interpQuality,
			float scaleFactorX, float scaleFactorY) {

		// set the parameters
		final ParameterBlock pb = new ParameterBlock();
		pb.addSource(src);
		pb.add(scaleFactorX);
		pb.add(scaleFactorY);
		pb.add(0.0f);
		pb.add(0.0f);
		pb.add(interpQuality.getInterpolation());

		// scale!
		return JAI.create("scale", pb, RH_BORDER_REFLECT);
	}

	public static PlanarImage scaleImageIteratively(PlanarImage src, float scaleFactorX,
			float scaleFactorY) {
		return scaleImageIterativelyToDimensions(src, InterpolationQuality.BILINEAR, (int) Math
				.round(src.getWidth() * scaleFactorX), (int) Math.round(src.getHeight()
				* scaleFactorY));
	}

	/**
	 * Higher quality, but slower implementation...
	 * 
	 * @param src
	 * @param interpQuality
	 * @param scaleFactorX
	 * @param scaleFactorY
	 * @return
	 */
	public static PlanarImage scaleImageIteratively(PlanarImage src,
			InterpolationQuality interpQuality, float scaleFactorX, float scaleFactorY) {
		return scaleImageIterativelyToDimensions(src, interpQuality, (int) Math.round(src
				.getWidth()
				* scaleFactorX), (int) Math.round(src.getHeight() * scaleFactorY));
	}

	private static PlanarImage scaleImageIterativelyToDimensions(PlanarImage src,
			InterpolationQuality interpQuality, int width, int height) {
		// never scale to lower than...
		double minScale = .5;
		
		// is there a possiblility that it will loop forever?
		// we can prevent it by capping it to 10 iterations...
		int iterations = 0;
		final double targetWidth = (double) width;
		final double targetHeight = (double) height;
		while (!(src.getWidth() == width & src.getHeight() == height) && iterations < 10) {
			float scaleFactorX = (float) Math.max(minScale, targetWidth / src.getWidth());
			float scaleFactorY = (float) Math.max(minScale, (targetHeight / src.getHeight()));
			// System.out.println("Scaling To: " + scaleFactorX + ", " + scaleFactorY);
			src = scaleImage(src, interpQuality, scaleFactorX, scaleFactorY);
			iterations++;
		}
		return src;
	}

	/**
	 * scale the image to best fit into a rectangle of size width x height
	 * 
	 * defaults to good (but slower) scaling
	 * 
	 * @param src
	 * @param width
	 * @param height
	 * @return
	 * @author Ron Yeh
	 */
	public static PlanarImage scaleImageToFit(PlanarImage src, int width, int height) {
		return scaleImageToSize(src, width, height, InterpolationQuality.BICUBIC, true);
	}

	/**
	 * the targets are hints... to tell us what box the src should fit within
	 * 
	 * @param src
	 * @param targetWidth
	 * @param targetHeight
	 * @param quality
	 * @param maintainAspectRatio
	 * @return
	 */
	public static PlanarImage scaleImageToSize(PlanarImage src, int targetWidth, int targetHeight,
			InterpolationQuality quality, boolean maintainAspectRatio) {
		int oldWidth = src.getWidth();
		int oldHeight = src.getHeight();

		float testScaleFactorX = (float) targetWidth / (float) oldWidth;
		float testScaleFactorY = (float) targetHeight / (float) oldHeight;

		float scaleFactorX = 0;
		float scaleFactorY = 0;

		// if maintaining aspect Ratio, use the smaller of the two scales
		if (maintainAspectRatio) {
			float min = Math.min(testScaleFactorX, testScaleFactorY);
			scaleFactorX = min;
			scaleFactorY = min;
		} else {
			scaleFactorX = testScaleFactorX;
			scaleFactorY = testScaleFactorY;
		}

		return scaleImageIteratively(src, quality, scaleFactorX, scaleFactorY);
	}

	/**
	 * Writes a PlanarImage to a file, specified by the String (absolute path)
	 * 
	 * @param img
	 * @param quality
	 *            [0 to 100]
	 * @param path
	 */
	public static void writeImageToJPEG(PlanarImage img, File path) {
		ImageUtils.writeImageToJPEG(img.getAsBufferedImage(), 100, path);
	}

}
