package papertoolkit.demos.gigaprints2006;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.tools.develop.CodeGenerator;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * A GIGAprint with an Avian Flu MessMap developed by Robert Horn. Ambiently, the GIGAprint will flip through
 * its database of photos (with backing URLs). If a person is interested, he will tap the stop button, and the
 * GIGAprint displays the article on screen. Additionally, you can tap regions on the print which will bring
 * up the associated article. Finally, you can write your comments in the capture area which will update an
 * online representation of the avian flu mess map. We will do something similar for the Timeline.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class AvianFluTimelineGIGAprint extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AvianFluTimelineGIGAprint print = new AvianFluTimelineGIGAprint();
		// print.generateCode();
		print.run();
	}

	/**
	 * 
	 */
	private File file;

	/**
	 * 
	 */
	private PDFSheet sheet;

	/**
	 * 
	 */
	public AvianFluTimelineGIGAprint() {
		super("Timeline");
		
		file = new File("data/AvianFlu/AvianFluTimeline2_Cropped.pdf");
		sheet = new PDFSheet(file);
		sheet.addRegions(new File("data/AvianFlu/AvianFluTimeline2_WithRegions_Cropped.regions.xml"));

//		file = new File("data/AvianFlu/AvianFluTimeline2.pdf");
//		sheet = new PDFSheet(file);
//		sheet.addRegions(new File("data/AvianFlu/AvianFluTimeline2.regions.xml"));

		
		addSheet(sheet);
		initializePaperUI();
	}

	/**
	 * 
	 */
	public void generateCode() {
		CodeGenerator generator = new CodeGenerator();
		generator.generateInitializePaperUI(sheet);
		generator.generateSetupRegionMethods(sheet);
	}

	/**
	 * @see papertoolkit.application.Application#renderToPDF()
	 */
	public void renderToPDF() {
		DebugUtils.println("Rendering...");
//		renderToPDF(new File("data/AvianFlu/"), "AvianFluTimeline_WithPattern");
		renderToPDF(new File("data/AvianFlu/"), "AvianFluTimeline_Cropped_WithPattern");
	}

	/**
	 * 
	 */
	private void initializePaperUI() {
		Region regionComments0 = sheet.getRegion("Comments_0");
		setupComments0(regionComments0);

		Region regionVote5 = sheet.getRegion("Vote_5");
		setupVote5(regionVote5);

		Region regionVote4 = sheet.getRegion("Vote_4");
		setupVote4(regionVote4);

		Region regionVote7 = sheet.getRegion("Vote_7");
		setupVote7(regionVote7);

		Region regionRetrieve3 = sheet.getRegion("Retrieve_3");
		setupRetrieve3(regionRetrieve3);

		Region regionVote3 = sheet.getRegion("Vote_3");
		setupVote3(regionVote3);

		Region regionRetrieve7 = sheet.getRegion("Retrieve_7");
		setupRetrieve7(regionRetrieve7);

		Region regionVote0 = sheet.getRegion("Vote_0");
		setupVote0(regionVote0);

		Region regionRetrieve1 = sheet.getRegion("Retrieve_1");
		setupRetrieve1(regionRetrieve1);

		Region regionVote1 = sheet.getRegion("Vote_1");
		setupVote1(regionVote1);

		Region regionRetrieve0 = sheet.getRegion("Retrieve_0");
		setupRetrieve0(regionRetrieve0);

		Region regionComments1 = sheet.getRegion("Comments_1");
		setupComments1(regionComments1);

		Region regionRetrieve2 = sheet.getRegion("Retrieve_2");
		setupRetrieve2(regionRetrieve2);

		Region regionVote2 = sheet.getRegion("Vote_2");
		setupVote2(regionVote2);

		Region regionVote6 = sheet.getRegion("Vote_6");
		setupVote6(regionVote6);

		Region regionRetrieve5 = sheet.getRegion("Retrieve_5");
		setupRetrieve5(regionRetrieve5);

		Region regionRetrieve4 = sheet.getRegion("Retrieve_4");
		setupRetrieve4(regionRetrieve4);

		Region regionRetrieve6 = sheet.getRegion("Retrieve_6");
		setupRetrieve6(regionRetrieve6);

		Region regionTopTimeline = sheet.getRegion("TopTimeline");
		setupTopTimeline(regionTopTimeline);

	}

	private void setupComments0(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Comments0.");
			}
		});
	}

	private void setupVote5(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote5.");
			}
		});
	}

	private void setupVote4(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote4.");
			}
		});
	}

	private void setupVote7(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote7.");
			}
		});
	}

	private void setupRetrieve3(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve3.");
			}
		});
	}

	private void setupVote3(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote3.");
			}
		});
	}

	private void setupRetrieve7(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve7.");
			}
		});
	}

	private void setupVote0(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote0.");
			}
		});
	}

	private void setupRetrieve1(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve1.");
			}
		});
	}

	private void setupVote1(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote1.");
			}
		});
	}

	private void setupRetrieve0(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve0.");
			}
		});
	}

	private void setupComments1(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Comments1.");
			}
		});
	}

	private void setupRetrieve2(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve2.");
			}
		});
	}

	private void setupVote2(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote2.");
			}
		});
	}

	private void setupVote6(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Vote6.");
			}
		});
	}

	private void setupRetrieve5(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve5.");
			}
		});
	}

	private void setupRetrieve4(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve4.");
			}
		});
	}

	private void setupRetrieve6(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on Retrieve6.");
			}
		});
	}

	private void setupTopTimeline(Region region) {
		region.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on TopTimeline.");
			}
		});
	}

}
