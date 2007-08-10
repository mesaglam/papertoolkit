package edu.stanford.hci.r3.demos.simple;

import java.awt.Font;
import java.io.File;
import java.util.List;

import papertoolkit.paper.Bundle;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.regions.TextRegion;
import papertoolkit.render.BundleRenderer;
import papertoolkit.units.Inches;


/**
 * <p>
 * Creates Four Page-sized Patterned PDFs. Each page should have unique anoto pattern.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FourPagesBundle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		renderFourPDFs();
	}

	/**
	 * Generate a Bundle and render it to four individual PDFs.
	 */
	@SuppressWarnings("unused")
	private static void renderFourPDFs() {
		Bundle bundle = new Bundle("Four Pages");
		bundle.createAndAddSheets(4, "Lined Notes", new Inches(8.5), new Inches(11));
		List<Sheet> sheets = bundle.getSheets();
		for (Sheet sheet : sheets) {
			Region r = new Region("Square_" + sheet.getName(), 2, 2, 4, 5);
			r.setActive(true); // get some pattern on there!
			sheet.addRegion(r);
		}

		BundleRenderer renderer = new BundleRenderer(bundle);
		renderer.renderToIndividualPDFs(new File("data/Bundles"), "FourPages");
	}

	/**
	 * Does not work yet.
	 */
	private static void renderWithGlobalRegion() {
		Bundle bundle = new Bundle("Four Pages");
		bundle.createAndAddSheets(4, "Lined Notes", new Inches(8.5), new Inches(11));

		TextRegion submitRegion = new TextRegion("Submit", "Submit Form", new Font("Trebuchet MS",
				Font.PLAIN, 23), new Inches(6), new Inches(9));
		submitRegion.setActive(true);
		bundle.addGlobalRegion(submitRegion);

		List<Sheet> sheets = bundle.getSheets();
		for (Sheet sheet : sheets) {
			Region r = new Region("Square_" + sheet.getName(), 2, 2, 4, 5);
			r.setActive(true); // get some pattern on there!
			sheet.addRegion(r);
		}

		BundleRenderer renderer = new BundleRenderer(bundle);
		renderer.renderToIndividualPDFs(new File("data/Bundles"), "FourPages");
	}
}
