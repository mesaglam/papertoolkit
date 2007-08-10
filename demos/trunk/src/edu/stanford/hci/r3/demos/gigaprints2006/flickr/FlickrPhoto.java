package edu.stanford.hci.r3.demos.gigaprints2006.flickr;

import java.io.File;
import java.net.URL;

import javax.media.jai.PlanarImage;

import papertoolkit.util.graphics.ImageCache;


/**
 * <p>
 * Allows us to serialize and unserialize flickr objects.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlickrPhoto {

	private String date;

	/**
	 * Representing the regular sized image.
	 */
	private File file;

	/**
	 * 
	 */
	private File fileLarge;

	/**
	 * 
	 */
	private String id;

	/**
	 * 
	 */
	private String secret;

	/**
	 * 
	 */
	private String title;

	/**
	 * 
	 */
	private URL url;

	/**
	 * @param staticPhotoURL
	 * @param title
	 */
	public FlickrPhoto(String flickrID, String photoSecret, File localFile, File localFileOrig,
			URL staticPhotoURL, String theTitle, String dateString) {
		id = flickrID;
		secret = photoSecret;
		file = localFile;
		fileLarge = localFileOrig;
		url = staticPhotoURL;
		title = theTitle;
		date = dateString;
	}

	/**
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the file for the original sized image
	 */
	public File getFileLarge() {
		return fileLarge;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public PlanarImage getImage() {
		return ImageCache.loadPlanarImage(file);
	}

	/**
	 * @return
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fileLarge.getName() + " " + url.toString();
	}

}
