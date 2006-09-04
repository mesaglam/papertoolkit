package edu.stanford.hci.r3.pattern.coordinates;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.paper.regions.PolygonalRegion;
import edu.stanford.hci.r3.paper.regions.TextRegion;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PatternLocationToSheetLocationMappingTest {
	public static Sheet createSheet() {
		Sheet sheet = new Sheet(new Inches(8.5), new Inches(11));

		// define some regions
		Region reg0 = new Region(0, 0, 1.5, 1.5);
		reg0.setActive(true);

		Region reg1 = new Region(1, 1, 2, 3);
		reg1.scaleRegionUniformly(0.75);

		PolygonalRegion poly0 = new PolygonalRegion(new Inches(), new Point2D.Double(1, 7),
				new Point2D.Double(2, 8), new Point2D.Double(3, 7));
		poly0.scaleRegionUniformly(.33);

		PolygonalRegion poly1 = new PolygonalRegion(new Inches(), new Point2D.Double(4, 10),
				new Point2D.Double(6, 10), new Point2D.Double(5, 7), new Point2D.Double(3, 6));
		poly1.scaleRegionUniformly(.66);

		ImageRegion img = new ImageRegion(new File("data/testFiles/dragon.jpg"), new Inches(3.5),
				new Inches(4));
		img.scaleRegionUniformly(.25);

		TextRegion text = new TextRegion("The Dragon is\nGreeeen!", new Font("Trebuchet MS",
				Font.BOLD, 72), new Inches(1), new Inches(2));

		// add regions to the sheet
		sheet.addRegion(reg0);
		sheet.addRegion(reg1);
		sheet.addRegion(poly0);
		sheet.addRegion(poly1);
		sheet.addRegion(img);
		sheet.addRegion(text);

		return sheet;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping(
				createSheet());
		mapping.printMapping();
	}
}
