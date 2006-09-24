package edu.stanford.hci.r3.demos.audioguide;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.util.DebugUtils;

public class AudioGuide extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AudioGuide guide = new AudioGuide();
		PaperToolkit p = new PaperToolkit();
		p.useApplicationManager(true);
		p.loadApplication(guide);
	}

	/**
	 * 
	 */
	private PDFSheet sheet;

	/**
	 * 
	 */
	public AudioGuide() {
		super("Audio Guide");
		initializePaperUI();
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeAfterConstructor()
	 */
	public void initializePaperUI() {
		sheet = new PDFSheet(new File("data/AudioGuide/StanfordMapLight.pdf"));

		// will crash if after addSheet(...)
		sheet.addRegions(new File("data/AudioGuide/StanfordMapLight.regions.xml"));

		// List<String> regionNames = sheet.getRegionNames();
		// System.out.println(regionNames);

		setupGates(sheet.getRegion("Gates"));
		setupGilbert(sheet.getRegion("Gilbert"));
		setupPackardPhotos(sheet.getRegion("PackardPhotos"));
		setupOvalPhotos(sheet.getRegion("OvalPhotos"));
		setupChurchPhotos(sheet.getRegion("ChurchPhotos"));
		setupGenericPhotos(sheet.getRegion("GenericPhotos"));
		setupTressider(sheet.getRegion("Tressider"));
		setupWhitePlaza(sheet.getRegion("WhitePlaza"));
		setupGeology(sheet.getRegion("Geology"));
		setupQuad(sheet.getRegion("Quad"));
		setupMath(sheet.getRegion("Math"));
		setupOval(sheet.getRegion("Oval"));
		setupGSB(sheet.getRegion("GSB"));
		setupHistory(sheet.getRegion("History"));
		setupTourists(sheet.getRegion("Tourists"));
		setupLanguage(sheet.getRegion("Language"));
		setupWhereAmI(sheet.getRegion("WhereAmI"));

		addSheet(sheet);
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeBeforeStarting()
	 */
	@Override
	protected void initializeBeforeStarting() {

	}

	/**
	 * @see edu.stanford.hci.r3.Application#renderToPDF()
	 */
	public void renderToPDF() {
		renderToPDF(new File("data/AudioGuide/"), "StanfordMapLight_WithPattern");
	}

	/**
	 * @param region
	 */
	private void setupChurchPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Church Photos.");
			}
		});
	}

	/**
	 * @param gatesRegion
	 */
	private void setupGates(Region gatesRegion) {
		gatesRegion.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Gates Building.");
			}
		});
	}

	private void setupGenericPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Generic Photos.");
			}
		});
	}

	private void setupGeology(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Geology.");
			}
		});
	}

	private void setupGilbert(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Gilbert and Herrin.");
			}
		});
	}

	private void setupGSB(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the GSB.");
			}
		});
	}

	private void setupHistory(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the History.");
			}
		});
	}

	private void setupLanguage(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Language.");
			}
		});
	}

	private void setupMath(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Math.");
			}
		});
	}

	private void setupOval(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Oval.");
			}
		});
	}

	private void setupOvalPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on The Oval Photos.");
			}
		});
	}

	private void setupPackardPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Packard Photos.");
			}
		});
	}

	private void setupQuad(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Quad.");
			}
		});
	}

	private void setupTourists(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Tourists.");
			}
		});
	}

	private void setupTressider(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Tressider.");
			}
		});
	}

	private void setupWhereAmI(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Where Am I.");
				System.out.println(e.getPercentageLocation());
			}
		});
	}

	private void setupWhitePlaza(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the White Plaza.");
				System.out.println(e.getPercentageLocation());
			}
		});
	}
}
