/**
 * 
 */
package edu.stanford.hci.r3.util.graphics;

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
}
