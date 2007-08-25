package papertoolkit.demos.gigaprints2006.posters;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.util.DebugUtils;

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
		final PaperToolkit p = new PaperToolkit();
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
	 * @see papertoolkit.application.Application#initializeAfterConstructor()
	 */
	protected void initializeAfterConstructor() {
		poster = new PDFSheet(new File("data/Posters/TangibleDrawers.pdf"));
		poster.addRegions(new File("data/Posters/TangibleDrawers.regions.xml"));
		addSheet(poster);
		// addSheet(poster, new File("data/Posters/TangibleDrawers.patternInfo.xml"));
		initializeEventHandlers();
	}
}
