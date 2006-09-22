package edu.stanford.hci.r3.demos.sketch;

import java.io.File;

import edu.stanford.hci.r3.paper.Sheet;

/**
 * <p>
 * The Sheet is 44" wide by 24" tall (within arms reach while seated). One the right, there are one
 * or two columns of cool photos that people can share by tapping the pen. The main area is just a
 * huge patterned area where we can draw...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BuddySketchPaperUI extends Sheet {

	public BuddySketchPaperUI() {
		super(44, 24); // inches
		addDrawingRegion();
		addPhotoRegions();
	}

	private void addPhotoRegions() {
		// TODO Auto-generated method stub
	}

	private void addDrawingRegion() {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	public void renderToPDFFile() {
		getRenderer().renderToPDF(new File("data/Sketch/BuddySketchUI.pdf"));
	}
}
