package edu.stanford.hci.r3.demos.gigaprints2006.blog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import edu.stanford.hci.r3.events.handlers.InkCollector;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.render.ink.InkRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.conversion.PixelsPerInch;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author Joel Brandt
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class CommentCollector extends InkCollector {

	private static final String COMMENT_DIR = "C:/www/bbcomments/";

	private static final String COMMENT_URL_PREFIX = "http://171.67.77.236/bbcomments/";

	/**
	 * Customize this to simulate the passage of time.
	 */
	private static final long SOME_AMOUNT_OF_TIME = 10L * 1000L; // in milliseconds

	private BoingBoingEntry bbentry;

	private int count = 0;

	private SyndFeed feed;

	private int index = 0;

	private List<SyndEntry> rss_entries = new ArrayList<SyndEntry>();

	private Thread updater;

	/**
	 * @param bbentry
	 * @param index
	 */
	public CommentCollector(BoingBoingEntry bbentry, int index) {
		this.bbentry = bbentry;
		this.index = index;

		feed = new SyndFeedImpl();
		feed.setFeedType("rss_1.0");

		feed.setTitle("BoingBoing GIGAprints Comments");
		feed.setLink("http://hci.stanford.edu/gigaprints");
		feed.setDescription("BoingBoing GIGAprints Comments");

		updater = new Thread() {

			long lastRecorded = 0L;

			@Override
			public void run() {
				while (true) {
					try {
						sleep(SOME_AMOUNT_OF_TIME);
					} catch (InterruptedException e) {
					}
					final long last = getTimestampOfMostRecentInkStroke();
					final long now = System.currentTimeMillis();
					if (last > 0 && (now - last > SOME_AMOUNT_OF_TIME) && lastRecorded < last) {
						CommentCollector.this.storeComment();
						lastRecorded = last;
					}
				}
			}
		};
		updater.start();
	}

	/**
	 * Save the ink ot a local JPEG file. Write it out to an RSS feed (stored on the local machine).
	 */
	private void storeComment() {
		final Ink commentInk = getInk();
		// clear();

		final String commentFileName = "comment" + index + "_" + count + ".jpg";
		final File commentImage = new File(CommentCollector.COMMENT_DIR + commentFileName);

		final InkRenderer r = new InkRenderer(commentInk);
		r.renderToJPEG(commentImage, new PixelsPerInch(72), new Inches(8.5), new Inches(8.5));

		SyndEntry entry;
		SyndContent description;

		try {
			entry = new SyndEntryImpl();
			entry.setTitle("Comment " + (count + 1) + " on story \"" + bbentry.title + "\"");
			entry.setLink(CommentCollector.COMMENT_URL_PREFIX + commentFileName);
			entry.setPublishedDate(new Date());
			description = new SyndContentImpl();
			description.setType("text/html");
			description.setValue("<p><img src=\"" + CommentCollector.COMMENT_URL_PREFIX + commentFileName + "\"></p>");
			entry.setDescription(description);
			rss_entries.add(entry);

			feed.setEntries(rss_entries);

			final Writer writer = new FileWriter(CommentCollector.COMMENT_DIR + "comments.rss");
			final SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			writer.close();
		} catch (IOException e) {
			DebugUtils.println("IO exception encountered trying to write RSS file");
			e.printStackTrace();
		} catch (FeedException e) {
			DebugUtils.println("FeedException encountered trying to write RSS file");
			e.printStackTrace();
		}

		DebugUtils.println("Stored comment for story \"" + bbentry.title + "\"");
		count++;
	}
}
