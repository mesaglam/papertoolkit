package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.io.File;
import java.util.Calendar;

import edu.stanford.hci.r3.demos.flickrphotos.PhotoDownloadr;

/**
 * <p>
 * A Twistr game with Flickr photos. This is used as the first task in the GIGAprints study.
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
	 * @param args
	 */
	public static void main(String[] args) {
		if (true) {
			Calendar startDay = Calendar.getInstance();
			startDay.set(Calendar.MONTH, 3); // 8==september
			startDay.set(Calendar.DATE, 1);
			startDay.set(Calendar.YEAR, 2006);

			// download photos
			PhotoDownloadr p = new PhotoDownloadr();
			p.downloadInterestingPhotos(3, startDay, 30, new File("data/Flickr/Twistr/"), new File(
					"data/Flickr/Twistr.xml"));
		} else {
			// set up and run the application

		}

	}
}
