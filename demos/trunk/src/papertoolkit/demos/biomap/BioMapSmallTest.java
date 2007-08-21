package papertoolkit.demos.biomap;

import java.io.File;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.paper.Bundle;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.sheets.PDFSheet;
import papertoolkit.units.Millimeters;


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
	 * Called by the super(...) constructor
	 * 
	 * @see papertoolkit.application.Application#initializeAfterConstructor()
	 */
	protected void initializeAfterConstructor() {
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
