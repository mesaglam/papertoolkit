package edu.stanford.hci.r3.demos.posters;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.util.DebugUtils;

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
public class PALette extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// if we do not add a pen, the PaperToolkit will add a pen for us...
		final Application a = new PALette();
		final PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.startApplication(a);
	}

	private PDFSheet poster;

	/**
	 * 
	 */
	public PALette() {
		super("PALette");
	}

	/**
	 * Add Event Handlers Here. Do nothing unless it is overridden by a subclass.
	 */
	protected void initializeEventHandlers() {
		// lower left side of the poster
		final Region webArea = poster.getRegion("WebsiteArea");
		webArea.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Web");
			}
		});

		// next to the stanford logo...
		final Region emailArea = poster.getRegion("EmailArea");
		emailArea.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Email");
			}
		});

		// lower right
		final Region downloadArea = poster.getRegion("DownloadPDF");
		downloadArea.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("Download");
			}
		});

	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeAfterConstructor()
	 */
	protected void initializeAfterConstructor() {
		poster = new PDFSheet(new File("data/Posters/PALette.pdf"));
		// for some stupid reason, PowerPoint and PDF kept giving me a rotated page... Boo. We need
		// to support this in the future, instead of manually rotating the XML files.
		// ACTUALLY: Our Acrobat plugin just plain does not support this, because we flip the
		// locations by subtracting from the height of the document.
		// for Rotated pages, we need to code it by hand. Booooooo.
		// If we're gonna do it by hand, might as well just code it
		// ACTUALLY 2: I forgot! You need to export it through Acrobat Twice. Bah Humbug.
		poster.addRegions(new File("data/Posters/PALette.regions.xml"));

		initializeEventHandlers();
		
		DebugUtils.println(poster.getRegionNames());
		// weird... we need to use the old method once, until we render it... and
		// then we need to add the patternInfo later... this design should be changed.
		addSheet(poster, new File("data/Posters/PALette.patternInfo.xml"));
	}
}
