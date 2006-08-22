package edu.stanford.hci.r3.core;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.File;

import org.junit.Test;

import edu.stanford.hci.r3.core.regions.ImageRegion;
import edu.stanford.hci.r3.core.regions.PolygonalRegion;
import edu.stanford.hci.r3.core.regions.TextRegion;
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
	 * 
	 */
	@Test
	public void printingTest() {
		System.out.println(new Sheet());
	}

	/**
	 * Add and print out some regions
	 */
	@Test
	public void addRegions() {
		Sheet sheet = new Sheet();
		sheet.addRegion(new Region(0, 0, 8.5, 11));
		Region reg = new Region(1, 1, 2, 3);
		sheet.addRegion(reg);
		reg.scaleRegionUniformly(0.75);

		PolygonalRegion poly = new PolygonalRegion(new Inches(), new Point2D.Double(1, 1),
				new Point2D.Double(2, 2), new Point2D.Double(3, 1));
		sheet.addRegion(poly);
		poly.scaleRegionUniformly(.5);

		ImageRegion img = new ImageRegion(new File("testData/dragon.jpg"), new Inches(3),
				new Inches(4));
		sheet.addRegion(img);
		img.scaleRegionUniformly(.25);

		sheet.addRegion(new TextRegion("!!!", new Font("Tahoma", Font.PLAIN, 72), new Inches(1),
				new Inches(2)));

		System.out.println(sheet);
	}
}
