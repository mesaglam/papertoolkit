package edu.stanford.hci.r3.tools.debug;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.util.DebugUtils;

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
	private FlashCommunicationServer flash;

	/**
	 * @param paperApp
	 */
	public DebuggingEnvironment(Application paperApp) {
		app = paperApp;

		DebugUtils.println("Starting to debug " + app);

		// ---------------------------------
		// out first try used Piccolo
		// startPiccoloDebugView(paperApp);
		// ---------------------------------

		// now, we'll use Flash as our GUI
		// Start the local messaging server
		flash = new FlashCommunicationServer();
		
		// start the Flash GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File eventVizHTML = new File(r3RootPath, "flash/bin/EventViz.html");
		flash.openFlashGUI(eventVizHTML);
		
		// start sending over messages...
	}

	private void startPiccoloDebugView(Application paperApp) {
		// set up a GUI
		frame = new DebugFrame(paperApp.getName());
		canvas = frame.getCanvas();

		// add visual components to GUI
		canvas.addVisualComponents(paperApp);
	}

}
