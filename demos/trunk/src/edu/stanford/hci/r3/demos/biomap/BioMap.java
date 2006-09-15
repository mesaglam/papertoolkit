package edu.stanford.hci.r3.demos.biomap;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;

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
		// do nothing, unless it is overridden.
	}

	/**
	 * Called by the super(...) constructor
	 * 
	 * @see edu.stanford.hci.r3.Application#initializePaperUI()
	 */
	protected void initializePaperUI() {
		sheet = new PDFSheet(new File("data/BioMap/SurveyLocations.pdf"));
		addSheet(sheet);
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
