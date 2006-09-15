package edu.stanford.hci.r3.render.regions;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;

import javax.media.jai.PlanarImage;

import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.graphics.ImageCache;

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
	 * @see edu.stanford.hci.r3.render.RegionRenderer#renderToG2D(java.awt.Graphics2D)
	 */
	public void renderToG2D(Graphics2D g2d) {
		if (RegionRenderer.DEBUG_REGIONS) {
			super.renderToG2D(g2d);
		}
		final File file = imgRegion.getFile();
		final Units units = imgRegion.getUnits();
		final double ppi = imgRegion.getPixelsPerInch(); // default is 72
		final double ppiConversion = 72 / ppi;
		final double conv = units.getConversionTo(new Points());
		final PlanarImage image = ImageCache.loadPlanarImage(file);
		final AffineTransform transform = new AffineTransform();

		// System.out.println(imgRegion);

		// translate to the origin first!
		transform.translate(imgRegion.getX() * conv, imgRegion.getY() * conv);
		// resize the image based on its scale
		transform.scale(imgRegion.getScaleX(), imgRegion.getScaleY());
		// resize the image based on its pixelsPerInch
		transform.scale(ppiConversion, ppiConversion);
		g2d.drawRenderedImage(image, transform);
	}
}
