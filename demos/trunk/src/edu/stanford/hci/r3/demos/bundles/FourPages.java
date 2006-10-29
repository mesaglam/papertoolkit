package edu.stanford.hci.r3.demos.bundles;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.paper.Bundle;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.render.BundleRenderer;
import edu.stanford.hci.r3.units.Inches;

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
public class FourPages {

	public static void main(String[] args) {
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
}
