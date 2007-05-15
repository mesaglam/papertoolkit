package edu.stanford.hci.r3.demos.gigaprints2006.posters;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.application.Application;
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
public class TangibleDrawers extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// if we do not add a pen, the PaperToolkit will add a pen for us...
		final Application a = new TangibleDrawers();
		final PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.startApplication(a);
	}

	private PDFSheet poster;

	private Region region0;

	private Region region1;

	private Region region2;

	private Region region3;

	private Region region4;

	/**
	 * 
	 */
	public TangibleDrawers() {
		super("Tangible Drawers");
	}

	/**
	 * Add Event Handlers Here. Do nothing unless it is overridden by a subclass.
	 */
	protected void initializeEventHandlers() {
		region0 = poster.getRegion("R0");
		region0.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("R0");
			}
		});

		region1 = poster.getRegion("R1");
		region1.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("R1");
			}
		});

		region2 = poster.getRegion("R2");
		region2.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("R2");
			}
		});

		region3 = poster.getRegion("R3");
		region3.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("R3");
			}
		});

		region4 = poster.getRegion("R4");
		region4.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("R4");
			}
		});
	}

	/**
	 * @see edu.stanford.hci.r3.application.Application#initializeAfterConstructor()
	 */
	protected void initializeAfterConstructor() {
		poster = new PDFSheet(new File("data/Posters/TangibleDrawers.pdf"));
		poster.addRegions(new File("data/Posters/TangibleDrawers.regions.xml"));
		addSheet(poster, new File("data/Posters/TangibleDrawers.patternInfo.xml"));
		initializeEventHandlers();
	}
}
