package edu.stanford.hci.r3.demos.blog;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.ContentFilterListener;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.paper.regions.TextRegion;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.util.DebugUtils;

public class BoingBoingPaperApp extends Application {

	private final boolean RENDER_NEW_STORIES = true;
	private final boolean MAKE_ACTIVE = false;
	
	private final Color BB_RED = new Color(191,0,0);
	
	private final Font TITLE_FONT = new Font("Trebuchet MS", Font.BOLD, 36);
	private final Font BODY_FONT = new Font("Trebuchet MS", Font.PLAIN, 14);
	private final Font BYLINE_FONT = new Font("Trebuchet MS", Font.PLAIN, 12);

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
		
		sheet.registerConfigurationPath(new File("data/Blog/"));
		
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
			
			if (all_entries.size() > 10) entries = new BoingBoingEntry[10];
			else entries = new BoingBoingEntry[all_entries.size()];
			
			// select entries to use (TODO: prompt user)
			for (BoingBoingEntry entry : all_entries) {
				entries[count] = entry;
				count++;
				if (count >= entries.length) break;
			}
			
			// output the stories as XML
			PaperToolkit.toXML(entries, new File("data/Blog/bb_stories.xml"));
		}
		else { // re-launching, so read entries from XML
			entries = (BoingBoingEntry[]) PaperToolkit.fromXML(new File("data/Blog/bb_stories.xml"));
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
	
	private void layoutRegions(int n) {
		Region r;
		double xOffset, yOffset;
		
		for (int i = 0; i < n; i++) {
			xOffset = (i%5)*10.0 + 1.0;
			yOffset = (i < 5 ? 6 : 22);
			
			// add link
			r = new Region("Link" + i, 0.25 + xOffset, 5.125 + yOffset, 0.625, 0.625);
			sheet.addRegion(r);
			
			// add comments section
			r = new Region("Comment" + i, 0.25 + xOffset, 6.25 + yOffset, 8.5, 8.5);
			sheet.addRegion(r);
		}
	}
	

	private void layoutEntries(BoingBoingEntry[] entries) {
		double xOffset, yOffset, xImageScale, yImageScale;
		TextRegion tr;
		ImageRegion ir;
		for (int i = 0; i < entries.length; i++) {
			xOffset = (i%5)*10.0 + 1.0;
			yOffset = (i < 5 ? 6 : 22);
			
			// Title
			tr = new TextRegion("title" + i, entries[i].title, TITLE_FONT, new Inches(.3 + xOffset), new Inches(.2 + yOffset), new Inches(8.5), new Inches(2.0)); 
			tr.setColor(BB_RED);
			tr.setLineWrapped(true);
			tr.setMaxLines(2);
			sheet.addRegion(tr);
			
			// Body
			tr = new TextRegion("title" + i, entries[i].body, BODY_FONT, new Inches(3.5 + xOffset), new Inches(1.75 + yOffset), new Inches(5.25), new Inches(4.375)); 
			tr.setColor(Color.BLACK);
			tr.setLineWrapped(true);
			tr.setMaxLines(0);
			sheet.addRegion(tr);
			
			// Byline
			tr = new TextRegion("title" + i, entries[i].author + "\n" + entries[i].date, BYLINE_FONT, new Inches(.3 + xOffset), new Inches(4 + yOffset)); 
			tr.setColor(BB_RED);
			tr.setLineWrapped(false);
			tr.setMaxLines(0);
			sheet.addRegion(tr);
			
			// Picture
			if (entries[i].image.toLowerCase().endsWith(".jpg") || entries[i].image.toLowerCase().endsWith(".jpeg")) {
				String filename = entries[i].image.substring(entries[i].image.lastIndexOf("/") + 1);
				File file = new File("data/Blog/images/" + filename);
				if (file.exists()) {
					ir = new ImageRegion("Picture" + i, file, new Inches(.25 + xOffset), new Inches(1.75 + yOffset));
					xImageScale = 3.0 / ir.getWidthVal();
					yImageScale = 2.25 / ir.getHeightVal();
					ir.setScale(Math.min(xImageScale, yImageScale), Math.min(xImageScale, yImageScale));
					sheet.addRegion(ir);
				}
				
			}
			
		}

	}

	
	
	private void initializePaperUI(BoingBoingEntry[] entries) {
		for (int i = 0 ; i < entries.length; i++) {
			final String link = entries[i].link;
			final String title = entries[i].title;
			sheet.getRegion("Link" + i).addEventHandler(new ClickAdapter() {
				public void pressed(PenEvent e) {
					Application.doOpenURL(link);
					DebugUtils.println("Clicked on link for story \"" + title + "\", opening URL: " + link);
				}
			});
			
			CommentCollector cc = new CommentCollector(entries[i], i);
			sheet.getRegion("Comment" + i).addContentFilter(cc);
			cc.addListener(new ContentFilterListener() {
				public void contentArrived() {
					DebugUtils.println("Comments are being made on story \"" + title + "\"");	
				}
			});
			
			
		}
		
	}

	public static void main(String[] args) {
		BoingBoingPaperApp print = new BoingBoingPaperApp();
		
		PaperToolkit p = new PaperToolkit(true);
		p.loadApplication(print);
	}
	
}
