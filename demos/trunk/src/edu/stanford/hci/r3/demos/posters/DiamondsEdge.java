package edu.stanford.hci.r3.demos.posters;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.ContentFilterListener;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.pen.Pen;

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
public class DiamondsEdge extends Application {

	/**
	 * The misspellings are intentional, to allow the speech synthesizer to sound correct.
	 */
	private static final String INTRO_TEXT_TO_READ = "Diamond's Edge marries two technologies, "
			+ "The Diamond Touch table, and the Anoto digital pen. We support collaborative "
			+ "brainstorming by allowing users to sketch together and leapfrog off each others designs.";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// if we do not add a pen, the PaperToolkit will add a pen for us...
		final Application a = new DiamondsEdge();
		final PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.startApplication(a);
	}

	private PDFSheet poster;

	/**
	 * 
	 */
	public DiamondsEdge() {
		super("DiamondsEdgePoster");
	}

	/**
	 * Add Event Handlers Here. Do nothing unless it is overridden by a subclass.
	 */
	protected void initializeEventHandlers() {
		// lower left side of the poster
		final Region captureArea = poster.getRegion("CaptureArea");
//		final InkCollector inkCollector = new InkCollector();
//		captureArea.addContentFilter(inkCollector);
//		inkCollector.addListener(new ContentFilterListener() {
//			public void contentArrived() {
//				System.out.println("CaptureArea");
//				System.out.println("Got Ink in the Capture Area...");
//			}
//		});

		// next to the stanford logo...
		Region websiteLink = poster.getRegion("HCIWebsiteArea");
		websiteLink.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("HCIWebsiteArea");
				doOpenURL("http://hci.stanford.edu/");
			}
		});

		Region titleWebsiteLink = poster.getRegion("WebsiteArea");
		titleWebsiteLink.addEventHandler(new ClickAdapter() {

			@Override
			public void clicked(PenEvent e) {
				System.out.println("WebsiteArea");
				doOpenURL("http://hci.stanford.edu/");
				doSpeakText(INTRO_TEXT_TO_READ);
			}
		});

		Region email = poster.getRegion("EmailArea");
		email.addEventHandler(new ClickAdapter() {

			/**
			 * Opens GMAIL
			 */
			@Override
			public void clicked(PenEvent e) {
				System.out.println("EmailArea");
				doOpenURL("https://mail.google.com/mail/?view=cm&tf=0&fs=1&to=mbernst@stanford.edu%20avir@stanford.edu");
			}
		});

		Region video = poster.getRegion("ShowVideoArea");
		video.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("VideoArea");
				doOpenFile(new File("data/Posters/DiamondsEdge.mov"));
			}
		});
	}

	/**
	 * This is an empty initialization method that developers can override if they choose to
	 * subclass an Application instead of creating an empty App and adding sheets to it.
	 * 
	 * It is called by the constructor.
	 */
	protected void initialize() {
		poster = new PDFSheet(new File("data/Posters/DiamondsEdge.pdf"));
		poster.addRegions(new File("data/Posters/DiamondsEdge.regions.xml"));
		// DebugUtils.println(poster.getRegionNames());

		Pen pen = new Pen("Main Pen");
		addPen(pen);
		
		// WARNING
		// Adding the sheet HAS to go after event handlers are added to the regions
		// This is poor design and MUST be changed... for usability's sake.
		// WAIT: This is not true, as Sketch! adds event handlers after adding the sheet. =\
		addSheet(poster, new File("data/Posters/DiamondsEdge2.patternInfo.xml"));

		initializeEventHandlers();
	}
}
