package papertoolkit.paper.regions;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.File;

import papertoolkit.paper.Region;
import papertoolkit.render.RegionRenderer;
import papertoolkit.render.regions.ImageRenderer;
import papertoolkit.units.Inches;
import papertoolkit.units.Pixels;
import papertoolkit.units.Units;
import papertoolkit.units.conversion.PixelsPerInch;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.graphics.ImageUtils;


/**
 * <p>
 * Represents an Image. You can either initialize it with a File, or with a Java Image type. If you add an
 * event handler to this, we will render pattern on top of the image.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ImageRegion extends Region {

	/**
	 * Points to the source file.
	 */
	private File imageFile;

	/**
	 * References the same exact object as super.shape. For convenience in this class.
	 */
	private Rectangle2D imageRect;

	/**
	 * Helps us interpret the size of this image...
	 */
	private PixelsPerInch pixelsPerInch;

	/**
	 * Convenience function.
	 * 
	 * @param name
	 * @param file
	 * @param originXInches
	 * @param originYInches
	 */
	public ImageRegion(String name, File file, double originXInches, double originYInches) {
		this(name, file, new Inches(originXInches), new Inches(originYInches));
	}

	/**
	 * Determines the pixelsPerInch automatically, based on whatever will fit into the bounding box defined by
	 * wInches and hInches. The image will be scaled appropriately, and placed at the upper left corner of
	 * this bounding box.
	 * 
	 * @param string
	 * @param mapFile
	 * @param xInches
	 * @param yInches
	 * @param wInches
	 *            the maxWidth of the Image
	 * @param hInches
	 *            the maxHeight of the Image
	 */
	public ImageRegion(String name, File imgFile, double xInches, double yInches, double wInches,
			double hInches) {
		super(name, new Inches()); // initialize units to Inches

		// figure out the shape of this image, by loading it and determining the dimensions
		final Dimension dimension = ImageUtils.readSize(imgFile);

		// infer the physical size of the photo from the width and height of the actual image, and the width
		// and height of the bounding box

		// this all depends on the aspect ratio...

		// compare the aspect ratios
		double aspectImage = dimension.width / (double) dimension.height;
		double aspectBox = wInches / hInches;

		double ppi = 72;

		DebugUtils.println("AspectImage: " + aspectImage + " AspectBox: " + aspectBox);
		
		// if aspectImage is larger, that means it is shorter and wider than the bounding box
		if (aspectImage > aspectBox) {
			// use the ppi of the width
			ppi = dimension.width / wInches;
		}
		// if aspectBox is larger, that means that the image is taller and narrower than the box
		// or if they are equal, then they both have the same aspect ratio
		else {
			// use the ppi of the height
			ppi = dimension.height / hInches;
		}
		DebugUtils.println("PixelsPerInch: " + ppi);

		pixelsPerInch = new PixelsPerInch(ppi);

		imageFile = imgFile;

		// my units
		final Units u = getUnits();

		// create a Rectangle from origin X, Y, with the correct dimensions (72 pixels per inch)
		final Rectangle2D.Double rect = new Rectangle2D.Double(xInches, yInches, //
				new Pixels(dimension.getWidth(), pixelsPerInch).getValueIn(u), //
				new Pixels(dimension.getHeight(), pixelsPerInch).getValueIn(u));
		System.out.println("ImageRegion Bounds == " + rect);
		setShape(rect);
		imageRect = rect;
	}

	/**
	 * This constructor interprets the image as 72 pixels per physical inch. Use the alternate constructor if
	 * you feel otherwise. ;)
	 * 
	 * @param imgFile
	 * @param originX
	 * @param originY
	 */
	public ImageRegion(String name, File imgFile, Units originX, Units originY) {
		this(name, imgFile, originX, originY, new PixelsPerInch(72));
	}

	/**
	 * @param imgFile
	 * @param originX
	 * @param originY
	 * @param pixelConversion
	 */
	public ImageRegion(String name, File imgFile, Units originX, Units originY, PixelsPerInch ppi) {
		super(name, originX); // initialize units

		// figure out the shape of this image, by loading it and determining the dimensions
		final Dimension dimension = ImageUtils.readSize(imgFile);

		// infer the physical size of the photo from this value
		pixelsPerInch = ppi;

		imageFile = imgFile;

		// my units (actually, just originX, as we passed it in earlier)
		final Units u = getUnits();

		// create a Rectangle from origin X, Y, with the correct dimensions (72 pixels per inch)
		final Rectangle2D.Double rect = new Rectangle2D.Double(originX.getValue(), originY.getValueIn(u),
				new Pixels(dimension.getWidth(), pixelsPerInch).getValueIn(u), new Pixels(dimension
						.getHeight(), pixelsPerInch).getValueIn(u));
		// System.out.println(rect);
		setShape(rect);
		imageRect = rect;

		// I believe this was a bug, as you should NEVER override the name as set by the developer. This is
		// because we assign regions to their pattern maps by NAME!
		// setName("An Image Region: " + imgFile.getName());
	}

	/**
	 * @return the the source File.
	 */
	public File getFile() {
		return imageFile;
	}

	/**
	 * @return the height of the image.
	 */
	public double getHeightVal() {
		return imageRect.getHeight() * scaleY;
	}

	/**
	 * @return the resolution of the image, in pixels per inch. This will have impact on how large the image
	 *         looks, when rendered to paper.
	 */
	public double getPixelsPerInch() {
		return pixelsPerInch.getValue();
	}

	/**
	 * Renders the image to PS, PDF, and Java2D.
	 * 
	 * @see papertoolkit.paper.Region#getRenderer()
	 */
	public RegionRenderer getRenderer() {
		return new ImageRenderer(this);
	}

	/**
	 * @return width of the image.
	 */
	public double getWidthVal() {
		return imageRect.getWidth() * scaleX;
	}

	/**
	 * While the scale factors affect the size of the image, they do not affect the user-specified origins.
	 * 
	 * @return
	 */
	public double getX() {
		return imageRect.getX();
	}

	/**
	 * While the scale factors affect the size of the image, they do not affect the user-specified origins.
	 * 
	 * @return
	 */
	public double getY() {
		return imageRect.getY();
	}

	/**
	 * @see papertoolkit.paper.Region#toString()
	 */
	public String toString() {
		return "Image: {" + getX() + ", " + getY() + ", " + getWidthVal() + ", " + getHeightVal() + "} in "
				+ getUnits().getUnitName() + " SourceFile: " + imageFile.getName();
	}
}
