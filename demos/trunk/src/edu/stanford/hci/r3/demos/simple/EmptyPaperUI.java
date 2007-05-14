package edu.stanford.hci.r3.demos.simple;

import java.awt.Font;
import java.io.File;

import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.paper.regions.TextRegion;
import edu.stanford.hci.r3.render.SheetRenderer;
import edu.stanford.hci.r3.units.Inches;

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
public class EmptyPaperUI {

	/**
	 * @return
	 */
	@SuppressWarnings("unused")
	private static void createHugeSimpleSheet() {
		Sheet s = new Sheet(44, 36);
		Region r = new Region("Huge", 0, 0, 44, 36);
		r.setActive(true); // layer with pattern!
		s.addRegion(r);
		SheetRenderer renderer = new SheetRenderer(s);
		renderer.renderToPDF(new File("data/General/EmptyBigPaperUI.pdf"));
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unused")
	private static void createBigSimpleSheet() {
		// 44 inches wide by 2 inches tall
		Sheet s = new Sheet(44, 2);
		Region r = new Region("Big", 0, 0, 44, 2);
		r.setActive(true); // layer with pattern!
		s.addRegion(r);
		SheetRenderer renderer = new SheetRenderer(s);
		renderer.renderToPDF(new File("data/General/EmptyLargePaperUI.pdf"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Sheet s = new Sheet(4, 4);
		
		String myString = "This is a really long string that should get wrapped lots and lots I hope.  We'll see how it goes, I guess.\n\nHopefully it'll work out...";
		Font myFont = new Font("Helvetica", Font.BOLD, 12);
		Inches origX = new Inches(0.5); 
		Inches origY = new Inches(0.5);
		Inches width = new Inches(3);
		Inches height = new Inches(2);
		
		TextRegion r = new TextRegion("Wrapping_Test", myString, myFont, origX, origY, width, height);
		r.setLineWrapped(true);
		//r.setActive(true);
		r.setMaxLines(5);
		
		s.addRegion(r);
		SheetRenderer renderer = new SheetRenderer(s);
		renderer.renderToPDF(new File("data/General/4x4.pdf"));
		
		
	}
}
