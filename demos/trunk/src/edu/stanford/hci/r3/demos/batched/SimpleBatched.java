package edu.stanford.hci.r3.demos.batched;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A simple demo to allow the user to create ONE region on ARBITRARY pattern (in streaming mode) by
 * drawing a big rectangle. This is saved as a regions XML file. Then, the user turns off streaming
 * to write on the region. Finally, the pen is synchronized, and we are presented with a timeline
 * control that allows us to animate the strokes that we wrote in that region...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SimpleBatched {

	public static void main(String[] args) {
		DebugUtils.println(PaperToolkit.getToolkitRootPath());
		
		SimpleBatched batched = new SimpleBatched();
		batched.askForNewRectangularRegion();
	}

	private Application app;

	private PaperToolkit toolkit;

	/**
	 * 
	 */
	public SimpleBatched() {
		app = new Application("Simple Batched Test");
		toolkit = new PaperToolkit();
		toolkit.startApplication(app);
	}

	/**
	 * 
	 */
	private void askForNewRectangularRegion() {

	}
}
