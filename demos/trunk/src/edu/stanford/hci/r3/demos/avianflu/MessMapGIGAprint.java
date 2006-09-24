package edu.stanford.hci.r3.demos.avianflu;

import java.io.File;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;

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

	@Override
	protected void initializeBeforeStarting() {
	}

	protected void initializePaperUI() {
		System.out.println(sheet.getRegionNames());
	}

	/**
	 * @see edu.stanford.hci.r3.Application#renderToPDF()
	 */
	public void renderToPDF() {
		renderToPDF(new File("data/AvianFlu/"), "data/AvianFlu/AvianFluMessMap_WithPattern.pdf");
	}
}
