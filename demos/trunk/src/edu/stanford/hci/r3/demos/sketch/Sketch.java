package edu.stanford.hci.r3.demos.sketch;

import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.sheets.PDFSheet;

/**
 * <p>
 * A Hello World application, that allows a user to sketch on a piece of paper and choose various
 * colors.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Sketch {

	public static void main(String[] args) {

		PDFSheet s = new PDFSheet(new File("data/Sketch/SketchUI.pdf"));
		s.addRegions(new File("data/Sketch/SketchUI.regions.xml"));
		
		for (Region r : s.getRegions()) {
			System.out.println(r);
		}
	}
}
