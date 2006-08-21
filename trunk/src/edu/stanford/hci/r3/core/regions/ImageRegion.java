package edu.stanford.hci.r3.core.regions;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;

import edu.stanford.hci.r3.core.Region;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.graphics.ImageUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ImageRegion extends Region {

	private Rectangle2D imageRect;

	/**
	 * This default constructor interprets the image as 72 pixels per physical inch. Use the
	 * alternate constructor if you feel otherwise. ;)
	 * 
	 * @param imageFile
	 * @param originX
	 * @param originY
	 */
	public ImageRegion(File imageFile, Units originX, Units originY) {
		super(originX); // initialize units

		// figure out the shape of this image, by loading it and determining the dimensions of the
		// image
		final Dimension dimension = ImageUtils.readSize(imageFile);

		// my units (actually, just originX, as we passed it in earlier)
		final Units u = getUnits();

		// create a Rectangle from origin X, Y, with the correct dimensions (72 pixels per inch)
		final Rectangle2D.Double rect = new Rectangle2D.Double(originX.getValue(), originY
				.getValueIn(u), new Pixels(dimension.getWidth(), 72).getValueIn(u), new Pixels(
				dimension.getHeight(), 72).getValueIn(u));
		setShape(rect);
		imageRect = rect;
	}

	/**
	 * Please override for more interesting output.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Image: {" + imageRect.getX() + ", " + imageRect.getY() + ", "
				+ imageRect.getWidth() + ", " + imageRect.getHeight() + "} in "
				+ getUnits().getUnitName();
	}

}
