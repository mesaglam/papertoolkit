package edu.stanford.hci.r3.demos.gigaprints2006.posters;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.devices.Device;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.pen.Pen;
import papertoolkit.pen.ink.InkStroke;


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
		final InkHandler inkCollector = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				System.out.println("CaptureArea");
				System.out.println("Got Ink in the Capture Area...");
			}
		};
		captureArea.addEventHandler(inkCollector);

		// next to the stanford logo...
		Region websiteLink = poster.getRegion("HCIWebsiteArea");
		websiteLink.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("HCIWebsiteArea");
				Device.doOpenURL("http://hci.stanford.edu/");
			}
		});

		Region titleWebsiteLink = poster.getRegion("WebsiteArea");
		titleWebsiteLink.addEventHandler(new ClickAdapter() {

			@Override
			public void clicked(PenEvent e) {
				System.out.println("WebsiteArea");
				Device.doOpenURL("http://hci.stanford.edu/");
				Device.doSpeakText(INTRO_TEXT_TO_READ);
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
				Device
						.doOpenURL("https://mail.google.com/mail/?view=cm&tf=0&fs=1&to=mbernst@stanford.edu%20avir@stanford.edu");
			}
		});

		Region video = poster.getRegion("ShowVideoArea");
		video.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				System.out.println("VideoArea");
				Device.doOpenFile(new File("data/Posters/DiamondsEdge.mov"));
			}
		});
	}

	/**
	 * This is an empty initialization method that developers can override if they choose to
	 * subclass an Application instead of creating an empty App and adding sheets to it.
	 * 
	 * It is called by the constructor.
	 */
	protected void initializeAfterConstructor() {
		poster = new PDFSheet(new File("data/Posters/DiamondsEdge.pdf"));
		poster.addRegions(new File("data/Posters/DiamondsEdge.regions.xml"));
		// DebugUtils.println(poster.getRegionNames());

		Pen pen = new Pen("Main Pen");
		addPenInput(pen);

		// WARNING
		// Adding the sheet HAS to go after event handlers are added to the regions
		// This is poor design and MUST be changed... for usability's sake.
		// WAIT: This is not true, as Sketch! adds event handlers after adding the sheet. =\
		addSheet(poster, new File("data/Posters/DiamondsEdge2.patternInfo.xml"));

		initializeEventHandlers();
	}
}
