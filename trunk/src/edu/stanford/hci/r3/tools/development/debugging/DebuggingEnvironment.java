package edu.stanford.hci.r3.tools.development.debugging;

import edu.stanford.hci.r3.Application;

/**
 * <p>
 * To help you visualize the event handlers and otherwise debug the paper UI and application. This
 * class contains the bulk of the debugging support, whereas the other classes represent the GUI.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DebuggingEnvironment {

	private Application app;
	private DebugFrame frame;
	private DebugPCanvas canvas;

	public DebuggingEnvironment(Application paperApp) {
		app = paperApp;

		// set up a GUI
		frame = new DebugFrame(paperApp.getName());
		canvas = frame.getCanvas();
		
		// add visual components to GUI
		canvas.addVisualComponents(paperApp);
	}

}
