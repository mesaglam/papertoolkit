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
public class FlutterbyNet extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// if we do not add a pen, the PaperToolkit will add a pen for us...
		final Application a = new FlutterbyNet();
		final PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.startApplication(a);
	}

	private PDFSheet poster;

	/**
	 * 
	 */
	public FlutterbyNet() {
		super("FlutterbyNet");
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
	 * It's sort of weird we have to recompile in between designing, rendering, and running. Fix
	 * this.
	 * 
	 * @see edu.stanford.hci.r3.Application#initialize()
	 */
	protected void initialize() {
		poster = new PDFSheet(new File("data/Posters/FlutterbyNet.pdf"));
		poster.addRegions(new File("data/Posters/FlutterbyNet.regions.xml"));

		initializeEventHandlers();
		
		DebugUtils.println(poster.getRegionNames());
		// weird... we need to use the old method once, until we render it... and
		// then we need to add the patternInfo later... this design should be changed.
		addSheet(poster, new File("data/Posters/FlutterbyNet.patternInfo.xml"));
	}
}
