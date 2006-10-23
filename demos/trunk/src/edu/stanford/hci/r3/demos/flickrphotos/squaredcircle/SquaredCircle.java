package edu.stanford.hci.r3.demos.flickrphotos.squaredcircle;

import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.actions.types.OpenURL2Action;
import edu.stanford.hci.r3.demos.flickrphotos.FlickrPhoto;
import edu.stanford.hci.r3.demos.flickrphotos.PhotoDownloadr;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.CompoundRegion;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.Coordinates;

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

	private static final double HEIGHT_IN_INCHES = 4;

	private static final double MARGIN_IN_INCHES = 0.5;

	// we can fit 19 per row! =)
	private static final int MAX_PHOTOS = 19;

	private static final double PADDING_X_IN_INCHES = .2;

	private static final double PADDING_Y_IN_INCHES = .25;

	private static final double PHOTO_HEIGHT_IN_INCHES = 1.5;

	private static final double PHOTO_WIDTH_IN_INCHES = 1.5;

	private static final double PHOTO_WIDTH_IN_PIXELS = 500;

	private static final Pixels PPI = new Pixels(1, PHOTO_WIDTH_IN_PIXELS / PHOTO_WIDTH_IN_INCHES /* ppi */);

	private static final double WIDTH_IN_INCHES = 43;

	public static final File XML_FILE = new File("data/Flickr/SquaredCircle.xml");

	/**
	 * @param args
	 */
	public static void downloadPhotos() {
		PhotoDownloadr downloadr = new PhotoDownloadr();
		try {
			downloadr.downloadFromGroup(500, new URL("http://www.flickr.com/groups/circle/"),
					new File("data/Flickr/SquaredCircle/"), SquaredCircle.XML_FILE);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (false) { // the switch for getting content or running the app
			downloadPhotos();
		} else {
			final PaperToolkit r3 = new PaperToolkit();
			r3.useApplicationManager(true);
			r3.loadApplication(new SquaredCircle());
		}
	}

	private List<FlickrPhoto> photosToView;

	/**
	 * @param theName
	 */
	public SquaredCircle() {
		super("Squared Circle Viewer");
		initializePaperUI();
	}

	/**
	 * Compound Regions don't work yet.
	 * 
	 * @param currXInches
	 * @param currYInches
	 * @param photo
	 * @return
	 */
	@SuppressWarnings("unused")
	private Region getImageWithWidgets(Inches currXInches, Inches currYInches,
			final FlickrPhoto photo) {
		final String photoID = photo.getId();
		final CompoundRegion cr = new CompoundRegion("ImageAndWidgets_" + photoID, currXInches,
				currYInches);
		final Inches zero = new Inches(0);
		// add an image at the upper left corner of this compoudn region
		cr.addChild(new ImageRegion("Image_" + photoID, photo.getFile(), zero, zero, PPI),
				new Coordinates(zero, zero));
		final Units photoWidth = new Inches(PHOTO_WIDTH_IN_INCHES);

		// one inch tall
		final Region voteUp = new Region("VoteUp_" + photoID, zero, zero, new Inches(0.5),
				new Inches(0.5));
		voteUp.setStrokeColor(Color.LIGHT_GRAY);

		final Region retrieve = new Region("Retrieve_" + photoID, zero, new Inches(0.5),
				new Inches(0.5), new Inches(0.5));
		retrieve.setStrokeColor(Color.LIGHT_GRAY);
		retrieve.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("Clicked on Photo " + photo.getUrl());
			}
		});

		final Region voteDown = new Region("VoteDown_" + photoID, zero, new Inches(1.0),
				new Inches(0.5), new Inches(0.5));
		voteDown.setStrokeColor(Color.LIGHT_GRAY);

		// add a rectangular region to the right of the image
		cr.addChild(voteUp, new Coordinates(photoWidth, zero));
		cr.addChild(retrieve, new Coordinates(photoWidth, zero));
		cr.addChild(voteDown, new Coordinates(photoWidth, zero));
		return cr;
	}

	/**
	 * Called by the constructor.
	 * 
	 * @see edu.stanford.hci.r3.Application#initializeAfterConstructor()
	 */
	@SuppressWarnings("unchecked")
	protected void initializePaperUI() {
		System.out.println("Initializing Paper UI...");
		final Sheet poster = new Sheet(WIDTH_IN_INCHES, HEIGHT_IN_INCHES);
		poster.addConfigurationPath(new File("data/Flickr/"));

		// read in the XML file
		photosToView = (List<FlickrPhoto>) PaperToolkit.fromXML(XML_FILE);

		// leave .5 inch margin on both sides of the sheet...
		// .25 inch padding between each photo
		// .25 inch padding between each row

		double currX = MARGIN_IN_INCHES;
		double currY = MARGIN_IN_INCHES;

		final Inches halfInch = new Inches(0.5);

		int n = 0;
		for (final FlickrPhoto photo : photosToView) {
			final String photoID = photo.getId();

			// add an image region with some paper buttons
			// final Region imageWithWidgets = getImageWithWidgets(new Inches(currX),
			// new Inches(currY), photo);
			// poster.addRegion(imageWithWidgets);

			// since we do not handle pattern on compound regions yet, we should add active regions
			// directly to the sheet

			final Inches currYInches = new Inches(currY);
			final ImageRegion imgRegion = new ImageRegion(photoID + "_image", photo.getFile(),
					new Inches(currX), currYInches, PPI);
			final Inches rightAfterImage = new Inches(currX + imgRegion.getWidthVal());
			final Region voteUpRegion = new Region(photoID + "_voteUp", rightAfterImage,
					currYInches, halfInch, halfInch);
			voteUpRegion.setStrokeColor(Color.LIGHT_GRAY);
			voteUpRegion.addEventHandler(new ClickAdapter() {
				@Override
				public void clicked(PenEvent e) {
					System.out.println("This Photo is Great!: " + photo.getUrl() + " " + photoID);
				}
			});

			final Region retrieveRegion = new Region(photoID + "_retrieve", rightAfterImage,
					new Inches(currY + 0.5), halfInch, halfInch);
			retrieveRegion.setStrokeColor(Color.LIGHT_GRAY);
			retrieveRegion.addEventHandler(new ClickAdapter() {
				@Override
				public void clicked(PenEvent e) {
					System.out.println("Clicked on Photo " + photo.getUrl() + " " + photoID);
					new OpenURL2Action(photo.getUrl(), OpenURL2Action.FIREFOX).invoke();
				}
			});
			final Region voteDownRegion = new Region(photoID + "_voteDown", rightAfterImage,
					new Inches(currY + 1.0), halfInch, halfInch);
			voteDownRegion.setStrokeColor(Color.LIGHT_GRAY);
			voteDownRegion.addEventHandler(new ClickAdapter() {
				@Override
				public void clicked(PenEvent e) {
					System.out.println("This Photo is No Good: " + photo.getUrl() + " " + photoID);
				}
			});

			poster.addRegion(imgRegion);
			poster.addRegion(voteUpRegion);
			poster.addRegion(retrieveRegion);
			poster.addRegion(voteDownRegion);

			currX += PADDING_X_IN_INCHES + imgRegion.getWidth().getValue() + halfInch.getValue();
			n++;
			// if we will overshoot the boundary on the next turn
			if (currX + imgRegion.getWidth().getValue() + halfInch.getValue() + MARGIN_IN_INCHES > WIDTH_IN_INCHES) {
				currX = MARGIN_IN_INCHES;
				currY += PHOTO_HEIGHT_IN_INCHES + PADDING_Y_IN_INCHES;
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
