package papertoolkit.render.regions;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;

import javax.media.jai.PlanarImage;

import papertoolkit.paper.regions.ImageRegion;
import papertoolkit.render.RegionRenderer;
import papertoolkit.units.Points;
import papertoolkit.units.Units;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.graphics.ImageCache;
import papertoolkit.util.graphics.JAIUtils;


/**
 * <p>
 * Renders an ImageRegion to a graphics context or PDF file.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ImageRenderer extends RegionRenderer {

	/**
	 * The region to render.
	 */
	private ImageRegion imgRegion;

	/**
	 * @param region
	 */
	public ImageRenderer(ImageRegion region) {
		super(region);
		imgRegion = region;
	}

	/**
	 * Render the image to a graphics context, given the pixels per inch scaling.
	 * 
	 * @see papertoolkit.render.RegionRenderer#renderToG2D(java.awt.Graphics2D)
	 */
	public void renderToG2D(Graphics2D g2d) {
		if (RegionRenderer.DEBUG_REGIONS) {
			super.renderToG2D(g2d);
		}
		final File file = imgRegion.getFile();
		final Units units = imgRegion.getUnits();
		final double ppi = imgRegion.getPixelsPerInch(); // default is 72
		final double ppiConversion = 72 / ppi;
		final double conv = units.getScalarMultipleToConvertTo(new Points());
		
		// load the image
		PlanarImage image = ImageCache.loadPlanarImage(file);
		if (imgRegion.isActive()) {
			DebugUtils.println("Image an Active Region. Blurring the Image to Increase Contrast w/ Pattern Dots.");
			image = JAIUtils.blur(image);
		}
		
		final AffineTransform transform = new AffineTransform();

		// System.out.println("Rendering " + imgRegion);

		// translate to the origin first!
		transform.translate(imgRegion.getX() * conv, imgRegion.getY() * conv);

		// resize the image based on its scale
		transform.scale(imgRegion.getScaleX(), imgRegion.getScaleY());

		// resize the image based on its pixelsPerInch
		transform.scale(ppiConversion, ppiConversion);
		
		g2d.drawRenderedImage(image, transform);
	}
}
