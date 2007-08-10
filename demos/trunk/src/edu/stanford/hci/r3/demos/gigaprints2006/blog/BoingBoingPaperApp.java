package edu.stanford.hci.r3.demos.gigaprints2006.blog;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.devices.Device;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.regions.ImageRegion;
import papertoolkit.paper.regions.TextRegion;
import papertoolkit.units.Inches;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * Displays Blog Entries, and allows users to comment on them with ink.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author Joel Brandt
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BoingBoingPaperApp extends Application {

	private static final Color BB_RED = new Color(191, 0, 0);

	// ///////////////
	// The Fonts
	// ///////////////
	private static final Font FONT_BODY = new Font("Trebuchet MS", Font.PLAIN, 14);

	private static final Font FONT_BYLINE = new Font("Trebuchet MS", Font.PLAIN, 12);

	private static final Font FONT_TITLE = new Font("Trebuchet MS", Font.BOLD, 36);

	private static final boolean MAKE_ACTIVE = true;

	private static final boolean RENDER_NEW_STORIES = false;

	public static void main(String[] args) {
		BoingBoingPaperApp print = new BoingBoingPaperApp();

		PaperToolkit p = new PaperToolkit(true);
		p.loadApplication(print);
	}

	/**
	 * 
	 */
	private Sheet sheet;

	public BoingBoingPaperApp() {
		super("BoingBoing");

		// For using a PDF in the background instead of a Jpeg
		// file = new File("data/Blog/bb_blank.pdf");
		// sheet = new PDFSheet(file);

		sheet = new Sheet(51, 38);

		sheet.addConfigurationPath(new File("./data/Blog/"));

		// Put the background on the sheet
		File background = new File("data/Blog/bb_blank.jpg");
		if (background.exists()) {
			ImageRegion ir = new ImageRegion("background", background, new Inches(1), new Inches(1));
			double imageScale = 36.0 / ir.getHeightVal();
			ir.setScale(imageScale, imageScale);
			sheet.addRegion(ir);
		}

		// First, get the BoingBoing entries we're going to use
		BoingBoingEntry entries[];

		if (RENDER_NEW_STORIES) { // read the entries from RSS and make XML of stories
			// read from RSS
			List<BoingBoingEntry> all_entries = BoingBoingReader.getBoingBoingEntries();
			int count = 0;

			if (all_entries.size() > 10)
				entries = new BoingBoingEntry[10];
			else
				entries = new BoingBoingEntry[all_entries.size()];

			// select entries to use (TODO: prompt user)
			for (BoingBoingEntry entry : all_entries) {
				entries[count] = entry;
				count++;
				if (count >= entries.length)
					break;
			}

			// output the stories as XML
			PaperToolkit.toXML(entries, new File("data/Blog/bb_stories.xml"));
		} else { // re-launching, so read entries from XML
			entries = (BoingBoingEntry[]) PaperToolkit
					.fromXML(new File("data/Blog/bb_stories.xml"));
		}

		// Layout the entries themselves
		layoutEntries(entries);
		// Then, setup the regions
		layoutRegions(entries.length);

		if (MAKE_ACTIVE) {
			// Add handlers to regions
			initializePaperUI(entries);
		}

		// must go after adding all regions and event handlers
		addSheet(sheet);

	}

	private void initializePaperUI(BoingBoingEntry[] entries) {
		for (int i = 0; i < entries.length; i++) {
			final String link = entries[i].link;
			final String title = entries[i].title;
			sheet.getRegion("Link" + i).addEventHandler(new ClickAdapter() {
				public void pressed(PenEvent e) {
					Device.doOpenURL(link);
					DebugUtils.println("Clicked on link for story \"" + title + "\", opening URL: "
							+ link);
				}
			});

			CommentCollector cc = new CommentCollector(entries[i], i) {
				public void contentArrived() {
					DebugUtils.println("Comments are being made on story \"" + title + "\"");
				}
			};
			sheet.getRegion("Comment" + i).addEventHandler(cc);

		}

	}

	/**
	 * @param entries
	 */
	private void layoutEntries(BoingBoingEntry[] entries) {
		double xOffset, yOffset, xImageScale, yImageScale;
		TextRegion tr;
		ImageRegion ir;
		for (int i = 0; i < entries.length; i++) {
			xOffset = (i % 5) * 10.0 + 1.0;
			yOffset = (i < 5 ? 6 : 22);

			// Title
			tr = new TextRegion("title" + i, entries[i].title, FONT_TITLE,
					new Inches(.3 + xOffset), new Inches(.2 + yOffset), new Inches(8.5),
					new Inches(2.0));
			tr.setColor(BB_RED);
			tr.setLineWrapped(true);
			tr.setMaxLines(2);
			sheet.addRegion(tr);

			// Body
			tr = new TextRegion("title" + i, entries[i].body, FONT_BODY, new Inches(3.5 + xOffset),
					new Inches(1.75 + yOffset), new Inches(5.25), new Inches(4.375));
			tr.setColor(Color.BLACK);
			tr.setLineWrapped(true);
			tr.setMaxLines(0);
			sheet.addRegion(tr);

			// Byline
			tr = new TextRegion("title" + i, entries[i].author + "\n" + entries[i].date,
					FONT_BYLINE, new Inches(.3 + xOffset), new Inches(4 + yOffset));
			tr.setColor(BB_RED);
			tr.setLineWrapped(false);
			tr.setMaxLines(0);
			sheet.addRegion(tr);

			// Picture
			if (entries[i].image.toLowerCase().endsWith(".jpg")
					|| entries[i].image.toLowerCase().endsWith(".jpeg")) {
				String filename = entries[i].image.substring(entries[i].image.lastIndexOf("/") + 1);
				File file = new File("data/Blog/images/" + filename);
				if (file.exists()) {
					ir = new ImageRegion("Picture" + i, file, new Inches(.25 + xOffset),
							new Inches(1.75 + yOffset));
					xImageScale = 3.0 / ir.getWidthVal();
					yImageScale = 2.25 / ir.getHeightVal();
					ir.setScale(Math.min(xImageScale, yImageScale), Math.min(xImageScale,
							yImageScale));
					sheet.addRegion(ir);
				}

			}

		}

	}

	/**
	 * Add a region for each entry.
	 * 
	 * @param numEntries
	 */
	private void layoutRegions(int numEntries) {
		Region r;
		double xOffset, yOffset;

		for (int i = 0; i < numEntries; i++) {
			xOffset = (i % 5) * 10.0 + 1.0;
			yOffset = (i < 5 ? 6 : 22);

			// add link
			r = new Region("Link" + i, 0.25 + xOffset, 5.125 + yOffset, 0.625, 0.625);
			sheet.addRegion(r);

			// add comments section
			r = new Region("Comment" + i, 0.25 + xOffset, 6.25 + yOffset, 8.5, 8.5);
			sheet.addRegion(r);
		}
	}

}
