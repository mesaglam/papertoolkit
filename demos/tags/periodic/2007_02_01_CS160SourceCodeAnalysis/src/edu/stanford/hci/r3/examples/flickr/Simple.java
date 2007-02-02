package edu.stanford.hci.r3.examples.flickr;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jickr.Flickr;
import org.jickr.FlickrException;
import org.jickr.Group;
import org.jickr.Photo;

/**
 * <p>
 * The R3 API Key is e116dc00f4f3c76a71419645642b1fd6. As long as you don't abuse it, feel free to
 * use it. The R3 Flickr shared secret is 301355be6f03b04b. Once again, please don't abuse it (by
 * flooding the Flickr servers with requests). You may sign up for your own Key/SharedSecret if you
 * need to.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Simple {

	private static final String API_KEY = "e116dc00f4f3c76a71419645642b1fd6";

	private static final int MAX_PHOTOS = 50;

	private static final String SHARED_SECRET = "301355be6f03b04b";

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Flickr.setApiKey(API_KEY);
		Flickr.setSharedSecret(SHARED_SECRET);
		File parent = new File("data/Flickr/");

		
		retrieveGroupPhotos();
		// retrieveInterestingPhotos();
	}

	@SuppressWarnings("unused")
	private static void retrieveGroupPhotos() {
		try {
			Group group = Group.findByURL(new URL("http://www.flickr.com/groups/circle/"));
			// System.out.println(group.getNumMembers());

			// get up to 5000
			List<Photo> photos = group.getPhotos();
			// System.out.println(photos.size());

			int count = 0;
			for (Photo p : photos) {
				String id = p.getID();
				String secret = p.getSecret();
				System.out.println(id + "  " + secret);
				if (++count >= MAX_PHOTOS) {
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void retrieveInterestingPhotos() {
		int count = 0;
		try {
			List<Photo> interesting = Photo.getInteresting();
			for (Photo photo : interesting) {
				System.out.println(photo.getTitle() + " [" + photo.getStaticURL() + "]");
				// append _o.jpg to the root file name and you get the original-sized photo
				if (++count >= MAX_PHOTOS) {
					break;
				}
			}
		} catch (FlickrException e) {
			e.printStackTrace();
		}
	}
}
