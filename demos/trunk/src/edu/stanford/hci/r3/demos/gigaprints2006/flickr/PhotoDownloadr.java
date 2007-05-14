package edu.stanford.hci.r3.demos.gigaprints2006.flickr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jickr.Flickr;
import org.jickr.FlickrException;
import org.jickr.Group;
import org.jickr.Photo;
import org.jickr.Photo.Size;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;
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
public class PhotoDownloadr {

	private static final String API_KEY = "e116dc00f4f3c76a71419645642b1fd6";

	private static final String SHARED_SECRET = "301355be6f03b04b";

	/**
	 * 
	 */
	public PhotoDownloadr() {
		Flickr.setApiKey(API_KEY);
		Flickr.setSharedSecret(SHARED_SECRET);
	}

	/**
	 * This method doesn't download the original-sized photo. Feel free to change it...
	 * 
	 * @param numPhotos
	 * @param groupURL
	 * @param pathToStorePhotos
	 * @param destXMLFile
	 */
	public void downloadFromGroup(int numPhotos, URL groupURL, File pathToStorePhotos,
			File destXMLFile) {
		final List<FlickrPhoto> photosToSave = new ArrayList<FlickrPhoto>();
		try {
			final Group group = Group.findByURL(groupURL);
			System.out.println("Found Group: " + groupURL);
			final List<Photo> photos = group.getPhotos();
			System.out.println("Got list of Photos...");

			int count = 0;
			for (final Photo p : photos) {
				final String id = p.getID();
				final String secret = p.getSecret();

				final File destFile = new File(pathToStorePhotos, id + "_" + secret + ".jpg");
				final BufferedImage image = p.getImage();
				ImageUtils.writeImageToJPEG(image, destFile);

				photosToSave.add(new FlickrPhoto(p.getID(), p.getSecret(), destFile, destFile, p
						.getStaticURL(), p.getTitle(), "Unknown Date"));
				if (++count >= numPhotos) {
					break;
				}
				System.out.println("Saved " + count + " photos.");
			}

			PaperToolkit.toXML(photosToSave, destXMLFile);
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final FlickrException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param numPhotos
	 * @param pathToStorePhotos
	 * @param destXMLFile
	 */
	@SuppressWarnings("unchecked")
	public void downloadInterestingPhotos(int photosPerDay, Calendar startDay, int numDaysBack,
			File pathToStorePhotos, File destXMLFile) {
		File largeDir = new File(pathToStorePhotos, "large/");
		File normalDir = new File(pathToStorePhotos, "normal/");
		if (!largeDir.exists()) {
			largeDir.mkdirs();
		}
		if (!normalDir.exists()) {
			normalDir.mkdirs();
		}

		final List<FlickrPhoto> photosToSave = new ArrayList<FlickrPhoto>();

		int totalCount = 0;
		for (int i = 0; i < numDaysBack; i++) {
			startDay.add(Calendar.DAY_OF_YEAR, -1);
			List<Photo> photos = null;
			try {
				photos = Photo.getInteresting(startDay);
			} catch (FlickrException e) {
				DebugUtils.println("No Photos for this day... [" + e.getMessage() + "]");
				continue;
			}
			System.out.println("Got list of Photos...");
			int count = 0;
			for (final Photo p : photos) {
				try {
					final String dateString = String.format("%1$tY-%1$tm-%1$td", startDay);
					DebugUtils.println("On date: " + dateString);

					final String id = p.getID();
					String secret = p.getSecret();
					final File destFile = new File(normalDir, id + "_" + secret + ".jpg");
					final File destFileLarge = new File(largeDir, id + "_" + secret + "_l.jpg");

					if (destFile.exists() && destFileLarge.exists()) {
						DebugUtils.println("Already downloaded this one!");
						continue;
					} else {

						BufferedImage image = null;
						BufferedImage imageLarge = null;
						try {
							image = p.getImage();
							imageLarge = p.getImage(Size.LARGE);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (FlickrException e) {
							e.printStackTrace();
						}

						if (image == null || imageLarge == null) {
							continue;
						}

						ImageUtils.writeImageToJPEG(image, destFile);
						ImageUtils.writeImageToJPEG(imageLarge, destFileLarge);

						photosToSave.add(new FlickrPhoto(p.getID(), p.getSecret(), destFile,
								destFileLarge, p.getStaticURL(), p.getTitle(), dateString));

					}

					System.out.println(p.getStaticURL());
					System.out.println("Saved " + ++totalCount + " photos.");
				} catch (FlickrException e) {
					DebugUtils.println(e.getMessage());
				} catch (Exception e) {
					DebugUtils.println(e.getMessage());
					// keep on truckin'
				}
				if (++count >= photosPerDay) {
					break;
				}

				// write to it periodically
				PaperToolkit.toXML(photosToSave, destXMLFile);
			}
		}
	}
}
