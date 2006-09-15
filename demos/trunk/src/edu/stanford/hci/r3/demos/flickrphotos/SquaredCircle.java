package edu.stanford.hci.r3.demos.flickrphotos;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;

/**
 * <p>
 * A GIGAprint of the Flickr Squared Circle Group.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SquaredCircle extends Application {

	private static final double HEIGHT_IN_INCHES = 48;

	private static final double MARGIN_IN_INCHES = 0.5;

	private static final double PADDING_X_IN_INCHES = .2;

	private static final double PADDING_Y_IN_INCHES = .25;

	private static final double PHOTO_HEIGHT_IN_INCHES = 1.5;

	private static final double PHOTO_WIDTH_IN_INCHES = 1.5;

	private static final double PHOTO_WIDTH_IN_PIXELS = 500;

	private static final Pixels PPI = new Pixels(1, PHOTO_WIDTH_IN_PIXELS / PHOTO_WIDTH_IN_INCHES /* ppi */);

	private static final double WIDTH_IN_INCHES = 43;

	public static final File XML_FILE = new File("data/Flickr/SquaredCircle.xml");

	private static final int MAX_PHOTOS = 10;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit r3 = new PaperToolkit();
		r3.useApplicationManager(true);
		r3.loadApplication(new SquaredCircle());
	}

	private List<FlickrPhoto> photosToView;

	/**
	 * @param theName
	 */
	public SquaredCircle() {
		super("Squared Circle Viewer");
	}

	/**
	 * Called by the constructor after initializePaperUI()
	 * 
	 * @see edu.stanford.hci.r3.Application#initializeEventHandlers()
	 */
	protected void initializeEventHandlers() {

	}

	/**
	 * Called by the constructor.
	 * 
	 * @see edu.stanford.hci.r3.Application#initializePaperUI()
	 */
	@SuppressWarnings("unchecked")
	protected void initializePaperUI() {
		System.out.println("Initializing Paper UI...");
		Sheet poster = new Sheet(WIDTH_IN_INCHES, HEIGHT_IN_INCHES);

		// read in the XML file
		photosToView = (List<FlickrPhoto>) PaperToolkit.fromXML(XML_FILE);

		// leave .5 inch margin on both sides of the sheet...
		// .25 inch padding between each photo
		// .25 inch padding between each row

		double currX = MARGIN_IN_INCHES;
		double currY = MARGIN_IN_INCHES;
		Inches currYInches = new Inches(currY);

		int n = 0;
		for (FlickrPhoto photo : photosToView) {

			// add an image region
			poster.addRegion(new ImageRegion(photo.getFile(), new Inches(currX), currYInches, PPI));
			currX += PADDING_X_IN_INCHES + PHOTO_WIDTH_IN_INCHES;
			n++;
			// if we will overshoot the boundary on the next turn
			if (currX + PHOTO_WIDTH_IN_INCHES + MARGIN_IN_INCHES > WIDTH_IN_INCHES) {
				currX = MARGIN_IN_INCHES;
				currY += PHOTO_HEIGHT_IN_INCHES + PADDING_Y_IN_INCHES;
				currYInches = new Inches(currY);
				if (currY + PHOTO_HEIGHT_IN_INCHES + MARGIN_IN_INCHES > HEIGHT_IN_INCHES) {
					// we're done
					break;
				}
			}
			System.out.println("Added " + n + " photos");
			if (n >= MAX_PHOTOS) {
				break;
			}
		}

		addSheet(poster);
	}
}
