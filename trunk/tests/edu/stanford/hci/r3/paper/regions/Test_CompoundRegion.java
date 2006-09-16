package edu.stanford.hci.r3.paper.regions;

import java.awt.geom.Rectangle2D;
import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.coordinates.Coordinates;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_CompoundRegion {

	/**
	 * Shows that empty Rects are possible.
	 */
	@SuppressWarnings("unused")
	private static void emptyRectangle() {
		Rectangle2D rect = new Rectangle2D.Double(1, 1, 0, 0);
		System.out.println(rect);
	}

	public static void main(String[] args) {
		Sheet s = new Sheet(8.5, 11);

		// start it at 1,1
		CompoundRegion cr = new CompoundRegion("Test", new Centimeters(0), new Centimeters(0));
		System.out.println(cr);
		// add a 1x1 child, offset by 1,2
		// the final should be starting at 2,3 and have size 1x1
		cr.addChild(new Region("Fat", new Centimeters(5), new Centimeters(17), // origin
				new Centimeters(15), new Centimeters(5)), // w x h
				new Coordinates(new Centimeters(0), new Centimeters(0))); // offset
		cr.addChild(new Region("Tall", new Centimeters(1), new Centimeters(2), // origin
				new Centimeters(3), new Centimeters(15)), // w x h
				new Coordinates(new Centimeters(2), new Centimeters(1))); // offset

		s.addRegion(cr);
		s.getRenderer().renderToJPEG(new File("data/testFiles/output/Compound.jpg"));
	}
}
