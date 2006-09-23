package edu.stanford.hci.r3.demos.blog;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class BoingBoingReader {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
			
		List<BoingBoingEntry> bbentries = getBoingBoingEntries();

		if (bbentries != null) {
			for (BoingBoingEntry bbentry : bbentries) {
				System.out.println(bbentry);
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public static List<BoingBoingEntry> getBoingBoingEntries() {
		try {
			URL feedUrl = new URL("http://feeds.feedburner.com/boingboing/iBag");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));

			List<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();

			ArrayList<BoingBoingEntry> bbentries = new ArrayList<BoingBoingEntry>();

			for (SyndEntry entry : entries) {
				bbentries.add(parseBoingBoingEntry(entry));
			}
			return bbentries;
		}
		catch (Exception ex) {
			System.out.println("Error retrieving BoingBoing feed:\n");
			ex.printStackTrace();
			return null;
		}
	}
	
	public static BoingBoingEntry parseBoingBoingEntry(SyndEntry entry) {
		BoingBoingEntry bbentry = new BoingBoingEntry();
		bbentry.title = entry.getTitle();
		bbentry.link = entry.getUri();

		if (entry.getContents().size() > 0) {
			String content = ((SyndContent) (entry.getContents().get(0))).getValue();
			int p, q;

			// first, we get the author out
			p = content.indexOf("<strong>");
			q = content.indexOf("</strong>");
			bbentry.author = content.substring(p + 8, q);
			content = content.substring(q+10);

			// next, we get an image URL
			p = content.indexOf("<img src=\"");
			q = content.indexOf("\"", p+10);
			bbentry.image = content.substring(p+10, q);

			// finally, we strip the content of all HTML tags, and store it in the body
			content = content.replaceAll("\n", "");
			content = content.replaceAll("<p>", "\n");
			q = -1;
			p = content.indexOf("<");
			while (p >= 0) {
				if (q+1 <= p-1) {
					if (bbentry.body.length() > 0 &&
						bbentry.body.charAt(bbentry.body.length()-1) != ' ' &&
						bbentry.body.charAt(bbentry.body.length()-1) != '\n' &&
						content.charAt(q+1) != ' ' && 
						content.charAt(q+1) != ',' &&
						content.charAt(q+1) != ';' &&
						content.charAt(q+1) != ':') {
						bbentry.body = bbentry.body + " ";
					}
					bbentry.body = bbentry.body + content.substring(q+1, p);
				}
				p = content.indexOf("<", p+1);
				q = content.indexOf(">", q+1);
			}
			bbentry.body = bbentry.body + content.substring(q+1);

			String oldbody = bbentry.body;
			bbentry.body = oldbody.replaceAll("\n ", "\n");
			while(oldbody.length() != bbentry.body.length()) {
				oldbody = bbentry.body;
				bbentry.body = oldbody.replaceAll("\n ", "\n");
			}
		}

		return bbentry;
	}

}

class BoingBoingEntry {
	String title = "";
	String author = "";
	String body = "";
	String image = "";
	String link = "";

	public String toString() {
		String result = "BoingBoingEntry:\n";
		result = result + "  Title:  [" + title + "]\n";
		result = result + "  Author: [" + author + "]\n";
		result = result + "  Image:  [" + image + "]\n";
		result = result + "  Link:   [" + link + "]\n";
		result = result + "  Body:\n" + body;
		return result;
	}
}