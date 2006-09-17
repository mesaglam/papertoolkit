package edu.stanford.hci.r3.demos.biomap;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.Bundle;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;
import edu.stanford.hci.r3.pen.batch.BatchEventHandler;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.units.Millimeters;

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
public class BioMapSmallTest extends Application {

	private PDFSheet sheet;

	private Bundle notebook;
	
	/**
	 * 
	 */
	public BioMapSmallTest() {
		super("Field Biology Map");
	}

	/**
	 * @see edu.stanford.hci.r3.Application#initializeEventHandlers()
	 */
	protected void initializeEventHandlers() {
		addBatchEventHandler(new BatchEventHandler("Note Pages Renderer") {

			@Override
			public void inkArrived(Ink inkOnThisPage) {
				
			}
			// nothing for now...
		});
	}

	/**
	 * Called by the super(...) constructor
	 * 
	 * @see edu.stanford.hci.r3.Application#initializePaperUI()
	 */
	protected void initializePaperUI() {
		sheet = new PDFSheet(new File("data/BioMap/SurveyLocationsTest.pdf"));
		sheet.addRegions(new File("data/BioMap/SurveyLocationsTest_WithRegions.regions.xml"));
		
		// this must come after you have added regions to the sheet
		addSheet(sheet);
		
		
		
		// the biomap application includes one field notebook... (95 pages)
		// for now, the Application class doesn't allow us to add bundles
		// so we keep it in this subclass for now
		notebook = new Bundle("Field Notebook");
		final int numPages = 95;
		for (int i=0; i<numPages; i++) {
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
		r3.loadApplication(new BioMapSmallTest());
	}
}
