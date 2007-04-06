package edu.stanford.hci.r3.demos.audioguide;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.devices.Device;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Task 3 of the CHI 2007 Studies.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class AudioGuide extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AudioGuide guide = new AudioGuide();
		PaperToolkit p = new PaperToolkit();
		p.useApplicationManager(true); // flip to false later on
		p.loadApplication(guide); // just call start later on
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
		// initializePaperUI();
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeBeforeStarting()
	 */
	@Override
	protected void initializeBeforeStarting() {

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
				Device.doPlaySound(new File("data/AudioGuide/audio/MemorialChurch.wav"));
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
				Device.doPlaySound(new File("data/AudioGuide/audio/Gates.wav"));
			}
		});
	}

	private void setupGenericPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Generic Photos.");
				Device.doPlaySound(new File("data/AudioGuide/audio/SEQ.wav"));
			}
		});
	}

	private void setupGeology(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Geology.");
				Device.doPlaySound(new File("data/AudioGuide/audio/GeologyCorner.wav"));
			}
		});
	}

	private void setupGilbert(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Gilbert and Herrin.");
				Device.doPlaySound(new File("data/AudioGuide/audio/Bio1.wav"));
				// doPlaySound(new File("data/AudioGuide/audio/Bio2.wav"));
			}
		});
	}

	private void setupGSB(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the GSB.");
				Device.doPlaySound(new File("data/AudioGuide/audio/GSB.wav"));
			}
		});
	}

	private void setupHistory(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the History.");
				Device.doPlaySound(new File("data/AudioGuide/audio/History.wav"));
			}
		});
	}

	private void setupLanguage(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Language.");
				Device.doPlaySound(new File("data/AudioGuide/audio/LanguageCorner.wav"));
			}
		});
	}

	private void setupMath(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Math.");
				Device.doPlaySound(new File("data/AudioGuide/audio/MathCorner.wav"));
			}
		});
	}

	private void setupOval(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Oval.");
				Device.doPlaySound(new File("data/AudioGuide/audio/AtOval.wav"));
			}
		});
	}

	private void setupOvalPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on The Oval Photos.");
				Device.doPlaySound(new File("data/AudioGuide/audio/AtOval.wav"));
			}
		});
	}

	private void setupPackardPhotos(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Packard Photos.");
				Device.doPlaySound(new File("data/AudioGuide/audio/SEQ.wav"));
			}
		});
	}

	private void setupQuad(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Quad.");
				Device.doPlaySound(new File("data/AudioGuide/audio/MainQuad.wav"));
			}
		});
	}

	private void setupTourists(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Tourists.");
				Device.doPlaySound(new File("data/AudioGuide/audio/AtOval.wav"));
			}
		});
	}

	private void setupTressider(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Tressider.");
				Device.doPlaySound(new File("data/AudioGuide/audio/Tressider.wav"));
			}
		});
	}

	private void setupWhereAmI(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the Where Am I.");
				System.out.println(e.getPercentageLocation());
				Device.doPlaySound(new File("data/AudioGuide/audio/AtOval.wav"));
			}
		});
	}

	private void setupWhitePlaza(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on the White Plaza.");
				System.out.println(e.getPercentageLocation());
				Device.doPlaySound(new File("data/AudioGuide/audio/WhitePlaza1.wav"));
				// doPlaySound(new File("data/AudioGuide/audio/WhitePlaza2.wav"));
			}
		});
	}
}
