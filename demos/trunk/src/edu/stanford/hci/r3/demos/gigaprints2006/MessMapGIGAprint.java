package edu.stanford.hci.r3.demos.gigaprints2006;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.application.Application;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickAdapter;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.tools.develop.CodeGenerator;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A GIGAprint with an Avian Flu MessMap developed by Robert Horn. Ambiently, the GIGAprint will
 * flip through its database of photos (with backing URLs). If a person is interested, he will tap
 * the stop button, and the GIGAprint displays the article on screen. Additionally, you can tap
 * regions on the print which will bring up the associated article. Finally, you can write your
 * comments in the capture area which will update an online representation of the avian flu mess
 * map. We will do something similar for the Timeline.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class MessMapGIGAprint extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MessMapGIGAprint print = new MessMapGIGAprint();
		// print.generateCode();

		PaperToolkit p = new PaperToolkit(true /* use app manager */);
		p.loadApplication(print);
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
	public MessMapGIGAprint() {
		super("Mess Map");
		file = new File("data/AvianFlu/AvianFluMessMap.pdf");
		sheet = new PDFSheet(file);
		sheet.addRegions(new File("data/AvianFlu/AvianFlumessMap.regions.xml"));

		// must go after addRegions
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
	 * @see edu.stanford.hci.r3.application.Application#renderToPDF()
	 */
	public void renderToPDF() {
		renderToPDF(new File("data/AvianFlu/"), "AvianFluMessMap_WithPattern");
	}

	/**
	 * 
	 */
	private void initializePaperUI() {
		Region regionFreeComments1 = sheet.getRegion("FreeComments1");
		setupFreeComments1(regionFreeComments1);

		Region regionFreeComments1Wikify = sheet.getRegion("FreeComments1_Wikify");
		setupFreeComments1Wikify(regionFreeComments1Wikify);

		Region regionInternationalNature = sheet.getRegion("International_Nature");
		setupInternationalNature(regionInternationalNature);

		Region regionNationalNature = sheet.getRegion("National_Nature");
		setupNationalNature(regionNationalNature);

		Region regionHealthCareNature = sheet.getRegion("HealthCare_Nature");
		setupHealthCareNature(regionHealthCareNature);

		Region regionRichNature = sheet.getRegion("Rich_Nature");
		setupRichNature(regionRichNature);

		Region regionVaccineNature = sheet.getRegion("Vaccine_Nature");
		setupVaccineNature(regionVaccineNature);

		Region regionEarlyFluNature = sheet.getRegion("EarlyFlu_Nature");
		setupEarlyFluNature(regionEarlyFluNature);

		Region regionLocalCommunitiesNature = sheet.getRegion("LocalCommunities_Nature");
		setupLocalCommunitiesNature(regionLocalCommunitiesNature);

		Region regionFreeComments2Wikify = sheet.getRegion("FreeComments2_Wikify");
		setupFreeComments2Wikify(regionFreeComments2Wikify);

		Region regionUnknownsNature = sheet.getRegion("Unknowns_Nature");
		setupUnknownsNature(regionUnknownsNature);

		Region regionFreeComments2 = sheet.getRegion("FreeComments2");
		setupFreeComments2(regionFreeComments2);

		Region regionVirusItselfNature = sheet.getRegion("VirusItself_Nature");
		setupVirusItselfNature(regionVirusItselfNature);

		Region regionAntiviralProducersNature = sheet.getRegion("AntiviralProducers_Nature");
		setupAntiviralProducersNature(regionAntiviralProducersNature);

		Region regionScienceOfAntiviralsNature = sheet.getRegion("ScienceOfAntivirals_Nature");
		setupScienceOfAntiviralsNature(regionScienceOfAntiviralsNature);

		Region regionFoodWikify = sheet.getRegion("Food_Wikify");
		setupFoodWikify(regionFoodWikify);

		Region regionCommunicationsWikify = sheet.getRegion("Communications_Wikify");
		setupCommunicationsWikify(regionCommunicationsWikify);

		Region regionTransportationWikify = sheet.getRegion("Transportation_Wikify");
		setupTransportationWikify(regionTransportationWikify);

		Region regionPublicHealthWikify = sheet.getRegion("PublicHealth_Wikify");
		setupPublicHealthWikify(regionPublicHealthWikify);

		Region regionEducationWikify = sheet.getRegion("Education_Wikify");
		setupEducationWikify(regionEducationWikify);

		Region regionPublicSafetyWikify = sheet.getRegion("PublicSafety_Wikify");
		setupPublicSafetyWikify(regionPublicSafetyWikify);

		Region regionPublicUtilitiesWikify = sheet.getRegion("PublicUtilities_Wikify");
		setupPublicUtilitiesWikify(regionPublicUtilitiesWikify);

		Region regionFinancialWikify = sheet.getRegion("Financial_Wikify");
		setupFinancialWikify(regionFinancialWikify);

		Region regionPublicServicesWikify = sheet.getRegion("PublicServices_Wikify");
		setupPublicServicesWikify(regionPublicServicesWikify);

		Region regionPublicUtilitiesComments = sheet.getRegion("PublicUtilities_Comments");
		setupPublicUtilitiesComments(regionPublicUtilitiesComments);

		Region regionFinancialComments = sheet.getRegion("FinancialComments");
		setupFinancialComments(regionFinancialComments);

		Region regionPublicServicesComments = sheet.getRegion("PublicServices_Comments");
		setupPublicServicesComments(regionPublicServicesComments);

		Region regionEducationComments = sheet.getRegion("Education_Comments");
		setupEducationComments(regionEducationComments);

		Region regionPublicHealthComments = sheet.getRegion("PublicHealth_Comments");
		setupPublicHealthComments(regionPublicHealthComments);

		Region regionPublicSafetyComments = sheet.getRegion("PublicSafety_Comments");
		setupPublicSafetyComments(regionPublicSafetyComments);

		Region regionFoodComments = sheet.getRegion("Food_Comments");
		setupFoodComments(regionFoodComments);

		Region regionCommunicationsComments = sheet.getRegion("Communications_Comments");
		setupCommunicationsComments(regionCommunicationsComments);

		Region regionTransportationComments = sheet.getRegion("Transportation_Comments");
		setupTransportationComments(regionTransportationComments);

		Region regionHowManyMightDie = sheet.getRegion("HowManyMightDie");
		setupHowManyMightDie(regionHowManyMightDie);

	}

	private void setupAntiviralProducersNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on AntiviralProducersNature.");
			}
		});
	}

	private void setupCommunicationsComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on CommunicationsComments.");
			}
		});
	}

	private void setupCommunicationsWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on CommunicationsWikify.");
			}
		});
	}

	private void setupEarlyFluNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on EarlyFluNature.");
			}
		});
	}

	private void setupEducationComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on EducationComments.");
			}
		});
	}

	private void setupEducationWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on EducationWikify.");
			}
		});
	}

	private void setupFinancialComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FinancialComments.");
			}
		});
	}

	private void setupFinancialWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FinancialWikify.");
			}
		});
	}

	private void setupFoodComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FoodComments.");
				System.out.println(e.getPercentageLocation());
			}
		});
	}

	private void setupFoodWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FoodWikify.");
			}
		});
	}

	private void setupFreeComments1(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FreeComments1.");
			}
		});
	}

	private void setupFreeComments1Wikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FreeComments1Wikify.");
			}
		});
	}

	private void setupFreeComments2(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FreeComments2.");
			}
		});
	}

	private void setupFreeComments2Wikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on FreeComments2Wikify.");
			}
		});
	}

	private void setupHealthCareNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on HealthCareNature.");
			}
		});
	}

	private void setupHowManyMightDie(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on HowManyMightDie.");
			}
		});
	}

	private void setupInternationalNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on InternationalNature.");
			}
		});
	}

	private void setupLocalCommunitiesNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on LocalCommunitiesNature.");
			}
		});
	}

	private void setupNationalNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on NationalNature.");
			}
		});
	}

	private void setupPublicHealthComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicHealthComments.");
			}
		});
	}

	private void setupPublicHealthWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicHealthWikify.");
			}
		});
	}

	private void setupPublicSafetyComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicSafetyComments.");
			}
		});
	}

	private void setupPublicSafetyWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicSafetyWikify.");
			}
		});
	}

	private void setupPublicServicesComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicServicesComments.");
			}
		});
	}

	private void setupPublicServicesWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicServicesWikify.");
			}
		});
	}

	private void setupPublicUtilitiesComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicUtilitiesComments.");
			}
		});
	}

	private void setupPublicUtilitiesWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on PublicUtilitiesWikify.");
			}
		});
	}

	private void setupRichNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on RichNature.");
			}
		});
	}

	private void setupScienceOfAntiviralsNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on ScienceOfAntiviralsNature.");
			}
		});
	}

	private void setupTransportationComments(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on TransportationComments.");
			}
		});
	}

	private void setupTransportationWikify(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on TransportationWikify.");
			}
		});
	}

	private void setupUnknownsNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on UnknownsNature.");
			}
		});
	}

	private void setupVaccineNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on VaccineNature.");
			}
		});
	}

	private void setupVirusItselfNature(Region region) {
		region.addEventHandler(new ClickAdapter() {
			@Override
			public void clicked(PenEvent e) {
				DebugUtils.println("You clicked on VirusItselfNature.");
			}
		});
	}
}
