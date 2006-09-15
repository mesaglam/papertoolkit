package edu.stanford.hci.r3.demos.flickrphotos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jickr.Flickr;
import org.jickr.FlickrException;
import org.jickr.Group;
import org.jickr.Photo;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.graphics.ImageUtils;

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
public class DownloadPhotos {
	private static final String API_KEY = "e116dc00f4f3c76a71419645642b1fd6";

	private static final int MAX_PHOTOS = 500;

	private static final String SHARED_SECRET = "301355be6f03b04b";

	public static void main(String[] args) {
		File parentPath = new File("data/Flickr/SquaredCircle/");
		Flickr.setApiKey(API_KEY);
		Flickr.setSharedSecret(SHARED_SECRET);

		List<FlickrPhoto> photosToSave = new ArrayList<FlickrPhoto>();

		// download 500 photos
		try {
			Group group = Group.findByURL(new URL("http://www.flickr.com/groups/circle/"));
			// System.out.println(group.getNumMembers());
			System.out.println("Found Group");

			// get 500
			List<Photo> photos = group.getPhotos();

			// System.out.println(photos.size());
			System.out.println("Got list of Photos...");

			int count = 0;
			for (Photo p : photos) {
				String id = p.getID();
				String secret = p.getSecret();

				File destFile = new File(parentPath, id + "_" + secret + ".jpg");
				BufferedImage image = p.getImage();
				ImageUtils.writeImageToJPEG(image, destFile);

				photosToSave.add(new FlickrPhoto(p.getID(), p.getSecret(), destFile, p
						.getStaticURL(), p.getTitle()));
				if (++count >= MAX_PHOTOS) {
					break;
				}
				System.out.println("Saved " + count + " photos.");
			}

			PaperToolkit.toXML(photosToSave, SquaredCircle.XML_FILE);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// save them to a known path

		// serialize the list, so we can retrieve it next time...

	}
}
