package edu.stanford.hci.r3.paper;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.ImageRegion;
import edu.stanford.hci.r3.paper.regions.PolygonalRegion;
import edu.stanford.hci.r3.paper.regions.TextRegion;
import edu.stanford.hci.r3.units.Inches;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SheetTest {

	/**
	 * @return a test sheet with a few regions of different types.
	 */
	@Ignore
	public static Sheet createAndPopulateSheet() {
		Sheet sheet = new Sheet(new Inches(5), new Inches(4));

		// define some regions
		Region reg0 = new Region(0, 0, 8.5, 11);
		reg0.setActive(true);

		Region reg1 = new Region(1, 1, 2, 3);
		reg1.scaleRegionUniformly(0.75);

		PolygonalRegion poly = new PolygonalRegion(new Inches(), new Point2D.Double(1, 7),
				new Point2D.Double(2, 8), new Point2D.Double(3, 7));
		poly.scaleRegionUniformly(.33);

		PolygonalRegion poly2 = new PolygonalRegion(new Inches(), new Point2D.Double(4, 10),
				new Point2D.Double(6, 10), new Point2D.Double(5, 7), new Point2D.Double(3, 6));
		poly2.scaleRegionUniformly(.66);

		ImageRegion img = new ImageRegion(new File("data/testFiles/dragon.jpg"), new Inches(3.5),
				new Inches(4));
		img.scaleRegionUniformly(.25);

		TextRegion text = new TextRegion("The Dragon is\nGreeeen!", new Font("Trebuchet MS",
				Font.BOLD, 72), new Inches(1), new Inches(2));

		// add regions to the sheet
		sheet.addRegion(reg0);
		sheet.addRegion(reg1);
		sheet.addRegion(poly);
		sheet.addRegion(poly2);
		sheet.addRegion(img);
		sheet.addRegion(text);

		return sheet;
	}

	/**
	 * Prints a test sheet (with regions) to the console.
	 */
	@Test
	public void addRegions() {
		Sheet sheet = createAndPopulateSheet();
		System.out.println(sheet);
	}

	/**
	 * Prints an empty test sheet to the console.
	 */
	@Test
	public void printingTest() {
		System.out.println(new Sheet());
	}
}
