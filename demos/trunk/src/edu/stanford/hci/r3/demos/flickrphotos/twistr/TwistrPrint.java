package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.demos.flickrphotos.FlickrPhoto;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.layout.FlowPaperLayout;
import edu.stanford.hci.r3.paper.layout.RegionGroup;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.coordinates.Coordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TwistrPrint extends Sheet {

	private static final Inches H_PADDING = new Inches(0.15);

	private static final double HEIGHT_OF_BUTTON = 0.92;

	private static final double TARGET_HEIGHT_IN_INCHES = 1.91;

	private static final Inches V_PADDING = new Inches(0.2);

	private static final Inches ZERO = new Inches(0);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TwistrPrint print = new TwistrPrint();
		SheetRenderer r = new SheetRenderer(print);
		r.renderToPDF(new File("data/Flickr/Twistr.pdf"));
	}

	private ArrayList<FlickrPhoto> listOfPhotos;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public TwistrPrint() {
		super(44, 33.8); // ~three feet tall

		listOfPhotos = (ArrayList<FlickrPhoto>) PaperToolkit.fromXML(new File(
				"data/Flickr/TwistrFinal.xml"));
		List<RegionGroup> regionGroups = new ArrayList<RegionGroup>();
		for (final FlickrPhoto photo : listOfPhotos) {
			final File file = photo.getFile();
			final String fileName = file.getName().replace(".jpg", "");
			final Region r = new ImageRegion(fileName, file, ZERO, ZERO);

			final double heightInInches = r.getHeight().getValueInInches();
			final double scaleFactor = TARGET_HEIGHT_IN_INCHES / heightInInches;
			r.setScale(scaleFactor, scaleFactor);

			final Region rButton = new Region(fileName + "_Button", ZERO, ZERO, r.getWidth(),
					new Inches(HEIGHT_OF_BUTTON));
			rButton.addEventHandler(new ClickHandler() {
				public void clicked(PenEvent e) {
					DebugUtils.println("Clicked on " + fileName + " :: " + photo.getTitle());
				}

				public void pressed(PenEvent e) {
					DebugUtils.println("Pressed on " + fileName + " :: " + photo.getTitle());
				}

				public void released(PenEvent e) {
					DebugUtils.println("Released on " + fileName + " :: " + photo.getTitle());
				}
			});

			// add the cluster
			final RegionGroup rg = new RegionGroup(fileName + "Group", new Inches(0), new Inches(0));
			rg.addRegion(r, new Coordinates(ZERO, ZERO));
			rg.addRegion(rButton, new Coordinates(ZERO, r.getHeight()));
			regionGroups.add(rg);
		}

		FlowPaperLayout.layoutRegionGroups(this, regionGroups, new Coordinates(new Inches(0.25),
				new Inches(0.25)), new Inches(43.5), new Inches(33.5), H_PADDING, V_PADDING);

	}
}
