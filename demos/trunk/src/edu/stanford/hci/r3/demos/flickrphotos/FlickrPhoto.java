package edu.stanford.hci.r3.demos.flickrphotos;

import java.io.File;
import java.net.URL;

import javax.media.jai.PlanarImage;

import edu.stanford.hci.r3.util.graphics.ImageCache;

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
public class FlickrPhoto {

	/**
	 * Representing the regular sized image.
	 */
	private File file;

	private String id;

	private String secret;

	private String title;

	private URL url;

	/**
	 * @param staticPhotoURL
	 * @param title
	 */
	public FlickrPhoto(String flickrID, String photoSecret, File localFile, URL staticPhotoURL,
			String theTitle) {
		id = flickrID;
		secret = photoSecret;
		file = localFile;
		url = staticPhotoURL;
		title = theTitle;
	}

	public File getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public PlanarImage getImage() {
		return ImageCache.loadPlanarImage(file);
	}

	public String getSecret() {
		return secret;
	}

	public String getTitle() {
		return title;
	}

	public URL getUrl() {
		return url;
	}

}
