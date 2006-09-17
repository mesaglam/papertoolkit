package edu.stanford.hci.r3.examples.paperui;

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
		Region r = new Region("4x4", 0, 0, 4, 4);
		r.setActive(true);
		s.addRegion(r);
		SheetRenderer renderer = new SheetRenderer(s);
		renderer.renderToPDF(new File("data/General/4x4.pdf"));
		
		
	}
}
