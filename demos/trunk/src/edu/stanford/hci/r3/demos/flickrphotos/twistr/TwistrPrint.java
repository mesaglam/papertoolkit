package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.io.File;
import java.util.ArrayList;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.demos.flickrphotos.FlickrPhoto;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.layout.FlowPaperLayout;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.coordinates.Coordinates;

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

	private static final Inches ZERO = new Inches(0);

	public static void main(String[] args) {
		TwistrPrint print = new TwistrPrint();
		SheetRenderer r = new SheetRenderer(print);
		r.renderToPDF(new File("data/Flickr/Twistr.pdf"));
	}

	private ArrayList<FlickrPhoto> listOfPhotos;

	private static final double TARGET_HEIGHT_IN_INCHES = 2;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public TwistrPrint() {
		super(44, 34); // ~three feet tall

		listOfPhotos = (ArrayList<FlickrPhoto>) PaperToolkit.fromXML(new File(
				"data/Flickr/TwistrFinal.xml"));
		ArrayList<Region> regions = new ArrayList<Region>();
		for (FlickrPhoto p : listOfPhotos) {
			File file = p.getFile();
			Region r = new ImageRegion(file.getName(), file, ZERO, ZERO);

			double heightInInches = r.getHeight().getValueInInches();
			double scaleFactor = TARGET_HEIGHT_IN_INCHES / heightInInches;
			r.setScale(scaleFactor, scaleFactor);
			regions.add(r);
		}
		FlowPaperLayout.layout(this, regions, new Coordinates(new Inches(0.5), new Inches(0.5)),
				new Inches(43), new Inches(33), new Inches(0.2), new Inches(0.2));
	}
}
