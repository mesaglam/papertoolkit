package edu.stanford.hci.r3.demos.blog;

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

import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkRenderer;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.util.DebugUtils;

public class CommentCollector extends InkCollector {

	private BoingBoingEntry bbentry;
	private Thread updater;

	private int index = 0;
	private int count = 0;

	private static final long ONE_MINUTE = 10L * 1000L;
	private static final String COMMENT_DIR = "C:/www/bbcomments/";
	private static final String COMMENT_URL_PREFIX = "http://171.67.77.236/bbcomments/";

	private SyndFeed feed;
	private List<SyndEntry> rss_entries = new ArrayList<SyndEntry>();

	public CommentCollector(BoingBoingEntry bbentry, int index) {
		this.bbentry = bbentry;
		this.index = index;

		feed = new SyndFeedImpl();
		feed.setFeedType("RSS 2.0");

		feed.setTitle("BoingBoing GIGAprints Comments");
		feed.setLink("http://hci.stanford.edu/gigaprints");
		feed.setDescription("");
		
		updater = new Thread() {

			long lastRecorded = 0L;

			public void run() {
				while (true) {
					try {
						sleep(CommentCollector.ONE_MINUTE);
					} catch (InterruptedException e) { }
					long last = getLastTimestamp();
					long now = System.currentTimeMillis();
					if (last > 0 && now - last > CommentCollector.ONE_MINUTE && lastRecorded < last) {
						CommentCollector.this.storeComment();
						lastRecorded = last;
					}
				}
			}
		};
		updater.start();
	}

	void storeComment() {
		Ink comment_ink = getInk();
		// clear();

		String comment_filename = "comment" + index + "_" + count + ".jpg";
		File comment_image = new File(CommentCollector.COMMENT_DIR + comment_filename); 

		InkRenderer r = new InkRenderer(comment_ink);
		r.renderToJPEG(comment_image, new Pixels(1), new Inches(8.5), new Inches(8.5));

		SyndEntry entry;
		SyndContent description;

			try {
				entry = new SyndEntryImpl();
				entry.setTitle("Comment " + (count + 1) + " on story \"" + bbentry.title + "\"");
				entry.setLink(CommentCollector.COMMENT_URL_PREFIX + comment_filename);
				entry.setPublishedDate(new Date());
				description = new SyndContentImpl();
				description.setType("text/plain");
				description.setValue("<p><img src=\"" + CommentCollector.COMMENT_URL_PREFIX + comment_filename + "\"></p>");
				entry.setDescription(description);
				rss_entries.add(entry);

				feed.setEntries(rss_entries);
				
				Writer writer = new FileWriter(CommentCollector.COMMENT_DIR + "comments.rss");
				SyndFeedOutput output = new SyndFeedOutput();
				output.output(feed,writer);
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
