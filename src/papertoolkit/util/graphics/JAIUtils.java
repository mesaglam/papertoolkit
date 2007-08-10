package papertoolkit.util.graphics;

import java.awt.Rectangle;
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
import javax.media.jai.KernelJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.SwingUtilities;

/**
 * <p>
 * Allows you to manipulate and work with images in Java Advanced Imaging (JAI).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class JAIUtils {

	// different "kernels" for convolution and interpolation, etc...
	public enum Kernel {
		Bicubic, Bilinear, Box, Gaussian, Laplacian, Nearest, Peak, Triangle
	}

	/**
	 * Flat top, 1/9 all around... Averages the nine pixels and deposits the result into the center pixel.
	 */
	public static final KernelJAI KERNEL_BOX = new KernelJAI(3, 3, //
			new float[] { 1 / 9f, 1 / 9f, 1 / 9f, //
					1 / 9f, 1 / 9f, 1 / 9f, //
					1 / 9f, 1 / 9f, 1 / 9f });

	/**
	 * Comment for <code>kernelGaussian</code>
	 */
	public static final KernelJAI KERNEL_GAUSSIAN = new KernelJAI(3, 3, //
			new float[] { 0.0105f, 0.0812f, 0.0105f, //
					0.0812f, 0.6332f, 0.0812f, //
					0.0105f, 0.0812f, 0.0105f });

	/**
	 * Comment for <code>kernelLaplacian</code>
	 */
	public static final KernelJAI KERNEL_LAPLACIAN = new KernelJAI(3, 3, //
			new float[] { 1, -2, 1, //
					-2, 5, -2, //
					1, -2, 1 });

	/**
	 * border extender, reflects the pixels like a mirror.
	 */
	private static final RenderingHints RH_BORDER_REFLECT = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
			BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));

	/**
	 * Blur this image with a box filter.
	 * 
	 * @param img
	 * @return
	 */
	public static PlanarImage blur(PlanarImage img) {
		return convolve(img, Kernel.Box);
	}

	/**
	 * Hopefully, this complicated way to convolve fixes the border issues...
	 * 
	 * @param image
	 * @param kernel
	 * @return
	 */
	public static PlanarImage convolve(PlanarImage image, Kernel kernel) {
		KernelJAI k = null;

		switch (kernel) {
		case Gaussian:
			k = KERNEL_GAUSSIAN;
			break;
		case Laplacian:
			k = KERNEL_LAPLACIAN;
			break;
		case Box:
			k = KERNEL_BOX;
			break;
		default:
			k = KERNEL_LAPLACIAN;
		}

		// the kernels have their default required padding
		int top = k.getTopPadding();
		int left = k.getLeftPadding();
		int right = k.getRightPadding();
		int bottom = k.getBottomPadding();

		// SystemUtilities.println(top + " " + left + " " + right + " " + bottom);

		final PlanarImage paddedImage = pad(image, left, right, top, bottom, BorderExtender.BORDER_REFLECT);
		final PlanarImage convolved = JAI.create("convolve", paddedImage, k, RH_BORDER_REFLECT);

		// SystemUtilities.println(image.getWidth() + " " + image.getHeight());
		// SystemUtilities.println(paddedImage.getWidth() + " " + paddedImage.getHeight());

		// we don't want the border, though, in the final result
		final PlanarImage result = JAIUtils.crop(convolved, left, top, image.getWidth(), image.getHeight());
		return result;
	}

	/**
	 * returns a writable buffer that is compatible with and (depending on the boolean flag) contains the same
	 * data as the input image
	 * 
	 * @param src
	 * @return the writable buffer
	 */
	public static TiledImage createCompatibleWritableBuffer(PlanarImage inputImage, boolean copyDataFromInput) {
		// create Tiled Image that is compatible with the input image
		final TiledImage ti = new TiledImage(inputImage.getMinX(), inputImage.getMinY(), inputImage
				.getWidth(), inputImage.getHeight(), inputImage.getTileGridXOffset(), inputImage
				.getTileGridYOffset(), inputImage.getSampleModel(), inputImage.getColorModel());
		// make it look the same too!
		if (copyDataFromInput) {
			ti.setData(inputImage.copyData());
		}
		return ti;
	}

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
		final ColorModel colorModel = new ComponentColorModel(new ICC_ColorSpace(profile), hasAlpha, /* premultipliedAlpha */
		false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		// The SampleModel
		final int defaultTileSize = 128;
		final int[] bandOffsets = new int[numBands];
		for (int i = 0; i < numBands; i++) {
			bandOffsets[i] = numBands - 1 - i;
		}

		final SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
				defaultTileSize, defaultTileSize /* heightOfTile */, numBands /* pixelStride */, numBands
						* defaultTileSize, bandOffsets);

		return new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
	}

	/**
	 * Even without an alpha channel, you can do alpha compositing! However, it won't work well if you want a
	 * transparent background for PNGs.
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static TiledImage createWritableBufferWithoutAlpha(int width, int height) {
		return createWritableBuffer(width, height, 3);
	}

	/**
	 * Create a black image with the given width, height, and color depth (use 3 for an RGB image, 4 for
	 * RGBA).
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
	 * Crop and reposition the image to (0,0) origin Do not use crop by itself, as it is quite useless... :\
	 * 
	 * @param img
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static PlanarImage crop(PlanarImage img, float x, float y, float width, float height) {
		// error check by intersecting the image bounds with the cropping bounds
		final Rectangle imageBounds = new Rectangle(0, 0, img.getWidth(), img.getHeight());
		SwingUtilities.computeIntersection((int) x, (int) y, (int) width, (int) height, imageBounds);
		final PlanarImage cropped = cropWithoutRecenter(img, imageBounds.x, imageBounds.y, imageBounds.width,
				imageBounds.height);
		return translate(cropped, -imageBounds.x, -imageBounds.y);
	}

	/**
	 * Deletes pixels from the top, left, bottom, and right.
	 * 
	 * @param img
	 * @param fromTop
	 * @param fromLeft
	 * @param fromBottom
	 * @param fromRight
	 * @return
	 */
	public static PlanarImage cropAwayPixels(PlanarImage img, //
			float fromTop, float fromLeft, float fromBottom, float fromRight) {
		final int width = img.getWidth();
		final int height = img.getHeight();
		PlanarImage cropped = cropWithoutRecenter(img, fromLeft, fromTop, width - fromLeft - fromRight,
				height - fromTop - fromBottom);
		return translate(cropped, -fromLeft, -fromTop);
	}

	/**
	 * The raw JAI call that crops the image. Watch out for some unintuitiveness... Instead, we wrap the call
	 * in a new crop() that auto repositions your image for you.
	 * 
	 * @see cropAndReposition(...)
	 * @param img
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private static PlanarImage cropWithoutRecenter(PlanarImage img, //
			float x, float y, float width, float height) {
		final ParameterBlock pb = new ParameterBlock();
		pb.addSource(img);
		pb.add(new Float(x));
		pb.add(new Float(y));
		pb.add(new Float(width));
		pb.add(new Float(height));
		return JAI.create("crop", pb);
	}

	/**
	 * BorderExtender... This adds pixels all around the image.
	 * 
	 * @param src
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @return
	 */
	public static PlanarImage pad(PlanarImage src, int left, int right, int top, int bottom, int extenderType) {
		final ParameterBlock pb = new ParameterBlock();
		pb.addSource(src);
		pb.add(new Integer(left)); // left
		pb.add(new Integer(right)); // right
		pb.add(new Integer(top)); // top
		pb.add(new Integer(bottom)); // bottom
		pb.add(BorderExtender.createInstance(extenderType));
		return translate(JAI.create("border", pb), left, top);
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

	public static PlanarImage scaleImageIteratively(PlanarImage src, float scaleFactorX, float scaleFactorY) {
		return scaleImageIterativelyToDimensions(src, InterpolationQuality.BILINEAR, (int) Math.round(src
				.getWidth()
				* scaleFactorX), (int) Math.round(src.getHeight() * scaleFactorY));
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
	public static PlanarImage scaleImageIteratively(PlanarImage src, InterpolationQuality interpQuality,
			float scaleFactorX, float scaleFactorY) {
		return scaleImageIterativelyToDimensions(src, interpQuality, (int) Math.round(src.getWidth()
				* scaleFactorX), (int) Math.round(src.getHeight() * scaleFactorY));
	}

	/**
	 * @param src
	 * @param interpQuality
	 * @param width
	 * @param height
	 * @return
	 */
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
	 * Translates the image.
	 * 
	 * @param img
	 * @param tx
	 * @param ty
	 * @return
	 */
	public static PlanarImage translate(PlanarImage img, float tx, float ty) {
		final ParameterBlockJAI pb = new ParameterBlockJAI("translate");
		pb.addSource(img);
		pb.setParameter("xTrans", tx);
		pb.setParameter("yTrans", ty);
		return JAI.create("translate", pb);
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
