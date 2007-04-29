package edu.stanford.hci.r3.demos.colors;

import java.awt.Color;
import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.render.SheetRenderer;

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
public class ColoredPattern {
	public static void main(String[] args) {
		Color color = Color.BLACK;
		String colorName = "Black";

		//Color color = Color.ORANGE;
		//String colorName = "Orange";

		Sheet sheet = new Sheet(8.5, 11);
		sheet.addRegion(getRegion());
		SheetRenderer renderer = sheet.getRenderer();
		renderer.setPatternColor(color);
		renderer.renderToPDF(new File("data/Colors/" + colorName + ".pdf"));
	}

	private static Region getRegion() {
		Region r = new Region("Colored", 0, 0, 2, 2);
		r.setActive(true);
		return r;
	}
}
