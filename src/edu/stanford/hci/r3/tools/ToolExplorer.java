package edu.stanford.hci.r3.tools;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.flash.FlashPenListener;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.tools.design.sketch.SketchToPaperUI;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * If you run the PaperToolkit.main, you will invoke the ToolExplorer, which helps you to figure out what the
 * toolkit offers.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ToolExplorer implements FlashListener {

	private FlashCommunicationServer flash;
	private SketchToPaperUI sketchToPaperUI;

	/**
	 * 
	 */
	public ToolExplorer() {
		// start the Flash Communications Server, and register our listeners...
		// Start the Apollo GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File toolExplorerApollo = new File(r3RootPath, "flash/bin/ToolExplorer.exe");
		flash = new FlashCommunicationServer(8989);
		flash.addFlashClientListener(this);
		flash.openFlashApolloGUI(toolExplorerApollo);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.stanford.hci.r3.flash.FlashListener#messageReceived(java.lang.String)
	 */
	@Override
	public void messageReceived(String command) {
		DebugUtils.println(command);
		if (command.equals("Design Clicked")) {
			sketchToPaperUI = new SketchToPaperUI();
			sketchToPaperUI.addPenListener(new FlashPenListener(flash));
		} else if (command.equals("Back Clicked")) {
			if (sketchToPaperUI != null) {
				sketchToPaperUI.exit();
				sketchToPaperUI = null;
			} else {

			}
		} else if (command.equals("exitServer")) {
			DebugUtils.println("Exiting the Application");
			System.exit(0);
		}
	}
}
