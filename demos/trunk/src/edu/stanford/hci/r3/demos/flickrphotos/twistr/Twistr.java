package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.demos.flickrphotos.FlickrPhoto;
import edu.stanford.hci.r3.demos.flickrphotos.PhotoDownloadr;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A Twistr game with Flickr photos. This is used as the first task in the GIGAprints study. We
 * should log actions to a file, so we can calculate the acquisition times...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Twistr {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static void downloadPhotos() {
		Calendar startDay = Calendar.getInstance();
		startDay.set(Calendar.MONTH, 3); // 8==september
		startDay.set(Calendar.DATE, 1);
		startDay.set(Calendar.YEAR, 2006);

		// download photos
		PhotoDownloadr p = new PhotoDownloadr();
		p.downloadInterestingPhotos(3, startDay, 30, new File("data/Flickr/Twistr/"), new File(
				"data/Flickr/TwistrTemp.xml"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// some setup code.... to be run once
		// downloadPhotos();

		// set up and run the application
		new Twistr();
	}

	private List<FlickrPhoto> listOfPhotos;

	private PhotoDisplay frame;

	private int numPhotos;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Twistr() {
		PaperToolkit.initializeLookAndFeel();

		// unserialize our TwistrFinal.xml file
		listOfPhotos = (ArrayList<FlickrPhoto>) PaperToolkit.fromXML(new File(
				"data/Flickr/TwistrFinal.xml"));
		numPhotos = listOfPhotos.size();
		DebugUtils.println(numPhotos + " photos in this game");
		setupFrame();
	}

	/**
	 * 
	 */
	private void setupFrame() {
		frame = new PhotoDisplay(this);
	}

	/**
	 * Returns four RANDOM photos every single time.
	 */
	public void getPhotos() {
		double r1 = Math.random();
		double r2 = Math.random();
		double r3 = Math.random();
		double r4 = Math.random();
		DebugUtils.println(r1 + " " + r2 + " " + r3 + " " + r4);

		int p1 = (int) (numPhotos * r1);
		int p2 = (int) (numPhotos * r2);
		int p3 = (int) (numPhotos * r3);
		int p4 = (int) (numPhotos * r4);

		
		FlickrPhoto photo1 = listOfPhotos.get(p1);
		FlickrPhoto photo2 = listOfPhotos.get(p2);
		FlickrPhoto photo3 = listOfPhotos.get(p3);
		FlickrPhoto photo4 = listOfPhotos.get(p4);
		
		DebugUtils.println(photo1);
		DebugUtils.println(photo2);
		DebugUtils.println(photo3);
		DebugUtils.println(photo4);
		
		frame.placeFourPhotos(photo1.getFileLarge(), photo2.getFileLarge(), photo3.getFileLarge(), photo4.getFileLarge());
	}
}
