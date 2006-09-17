package edu.stanford.hci.r3.demos.biomap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Bundle;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.pen.batch.BatchEventHandler;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Millimeters;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Create a GIGAprint of a JRBP Project's map. Enable some cool functionality in action bars at the
 * top and bottom of the map.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BioMap extends Application {

	private PDFSheet sheet;

	private Bundle notebook;

	/**
	 * 
	 */
	public BioMap() {
		super("Field Biology Map");
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeEventHandlers()
	 */
	protected void initializeEventHandlers() {
		addBatchEventHandler(new BatchEventHandler("Note Pages Renderer") {
			// nothing for now...
		});
	}

	/**
	 * Called by the super(...) constructor
	 * 
	 * @see edu.stanford.hci.r3.Application#initializePaperUI()
	 */
	protected void initializePaperUI() {
		// sheet = new PDFSheet(new File("data/BioMap/SurveyLocations.pdf"));
		sheet = new PDFSheet(new File("data/BioMap/SurveyLocationsLighterGaussianBlur1_0.pdf"));

		// read in the BioMapSites.txt file to define the locations of the regions

		double leftStartInches = 0.96;
		double topStartInches = 0;

		final int topLeftSiteUnmarked = 1064; // 0 Inches! Everything comes from this...
		// if smaller... drop down a row
		// calculate offset in X
		// if smaller by 40, drop down another row.... and so on...

		try {
			File siteInfo = new File("data/BioMap/BioMapSites.txt");
			BufferedReader br = new BufferedReader(new FileReader(siteInfo));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				System.out.println("Site #" + fields[0] + " at UTM: " + fields[1] + ", "
						+ fields[2]);

				final int siteNum = Integer.parseInt(fields[0]);
				final float utmX = Float.parseFloat(fields[1]);
				final float utmY = Float.parseFloat(fields[2]);

				int diff = topLeftSiteUnmarked - siteNum;
				final int rowsDown = 1 + (diff / 40); // floored!
				final int colsAcross = (40 - (diff % 40)) % 40;
				System.out.println(diff + "  " + diff % 40);
				System.out.println("Rows Down: " + rowsDown + "  Cols Across: " + colsAcross);

				double xOfThisSite = leftStartInches + colsAcross * 1.18;
				double yOfThisSite = topStartInches + rowsDown * 1.18;

				// add a region
				Region rSite = new Region("Site_" + siteNum, new Inches(xOfThisSite), new Inches(
						yOfThisSite), new Inches(.85), new Inches(.85));
				rSite.addEventHandler(new ClickHandler() {

					@Override
					public void clicked(PenEvent e) {
						DebugUtils.println("Site " + siteNum + " at " + utmX + ", " + utmY);
					}

					@Override
					public void pressed(PenEvent e) {

					}

					@Override
					public void released(PenEvent e) {

					}
				});
				sheet.addRegion(rSite);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// x=18.63 inches for top row, 1.17 inches..., 6 boxes..., .85 wide x .85 tall, 0.33
		// horiz/vert between...,
		// Site 1039, 1040... 1044

		// 611 right over......... 611

		// ...

		// 491 is right over 451

		// ... 451 is right over 411

		// x=11.56 inches, for 393, 394...397, 400, 404...411

		// 354, 355, 358, 364...368

		// 315, 316

		// 275, 276, 277

		// bottom row is #236, 237, 238

		addSheet(sheet);

		// the biomap application includes one field notebook... (95 pages)
		// for now, the Application class doesn't allow us to add bundles
		// so we keep it in this subclass for now
		notebook = new Bundle("Field Notebook");
		final int numPages = 95;
		for (int i = 0; i < numPages; i++) {
			final Sheet page = new Sheet(new Millimeters(148), new Millimeters(210));
			notebook.addSheets(page); // A5
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Ask the toolkit to load the application
		// The user can then choose whether to print the GIGAprint, or run the app
		PaperToolkit r3 = new PaperToolkit();
		r3.useApplicationManager(true);
		r3.loadApplication(new BioMap());
	}
}
