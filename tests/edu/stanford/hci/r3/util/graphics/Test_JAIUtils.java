package edu.stanford.hci.r3.util.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.io.File;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import org.junit.Ignore;

import edu.stanford.hci.r3.util.ArrayUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_JAIUtils {
	public static void main(String[] args) {
		showSampleAndColorModels();
		writeToTiledImage();
	}

	/**
	 * Just for debugging some sample and colorspace models.
	 */
	@Ignore
	@SuppressWarnings("unused")
	private static void showSampleAndColorModels() {
		PlanarImage image = JAIUtils.createZeroImage(640, 480, 4);

		SampleModel sampleModel = image.getSampleModel();
		System.out.println(sampleModel);
		System.out.println("NumDataElements: " + sampleModel.getNumDataElements());
		System.out.println("SMWidth: " + sampleModel.getWidth());
		System.out.println("SMHeight: " + sampleModel.getHeight());
		System.out.println(sampleModel.getDataType() + " == " + DataBuffer.TYPE_BYTE);
		System.out.println(sampleModel.getNumBands());

		PixelInterleavedSampleModel pism = (PixelInterleavedSampleModel) sampleModel;
		System.out.println("PixelStride: " + pism.getPixelStride());
		System.out.println("ScanlineStride: " + pism.getScanlineStride());
		System.out.println("BandOffsets: " + ArrayUtils.toString(pism.getBandOffsets()));

		System.out.println();
		System.out.println();
		System.out.println();

		ColorModel colorModel = image.getColorModel();
		System.out.println(colorModel);
		System.out.println(colorModel instanceof ComponentColorModel);
		ComponentColorModel ccm = (ComponentColorModel) colorModel;
		ColorSpace colorSpace = ccm.getColorSpace();
		System.out.println(colorSpace.getClass());

		ICC_ColorSpace icc = (ICC_ColorSpace) colorSpace;
		ICC_Profile profile = icc.getProfile();
		System.out.println(profile.getClass());

		System.out.println("Color Space Type: " + profile.getColorSpaceType() + " ==? "
				+ ColorSpace.TYPE_RGB);

		System.out.println(ccm.getNumComponents());
		System.out.println("Has Alpha? " + ccm.hasAlpha());
		System.out.println("Transparency: " + ccm.getTransparency() + " ==? "
				+ Transparency.TRANSLUCENT);
		System.out.println("Transfer Type: " + ccm.getTransferType() + " ==? "
				+ DataBuffer.TYPE_BYTE);
		System.out.println("Premultiplied Alpha? " + ccm.isAlphaPremultiplied());
		System.out.println(colorSpace.getType() + " ==? " + ColorSpace.TYPE_RGB);
		System.out.println(colorSpace.getNumComponents());

		ImageUtils.writeImageToJPEG(image.getAsBufferedImage(), 100, new File("testData/Test.jpg"));
	}

	/**
	 * Even without an alpha channel, you can do alpha compositing!
	 */
	private static void writeToTiledImage() {
		TiledImage image = JAIUtils.createWritableBufferWithoutAlpha(640, 480);
		System.out.println("Buffer Created...");
		Graphics2D g2D = image.createGraphics();
		g2D.setColor(Color.BLUE);
		g2D.fillRect(10, 10, 100, 134);
		g2D.setColor(new Color(204, 244, 244, 200));
		g2D.fillRect(50, 80, 500, 300);

		g2D.setColor(Color.RED);
		g2D.fillRect(30, 30, 30, 400);

		System.out.println("Starting to Write...");

		ImageUtils.writeImageToJPEG(image.getAsBufferedImage(), new File("testData/Test.jpg"));
		ImageUtils.writeImageToPNG(image.getAsBufferedImage(), new File("testData/Test.png"));
	}
}