package edu.stanford.hci.r3.tools.develop.inkapibrowser;

import java.io.File;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Flash GUI, Java Server backend. It exposes a number of ink-related methods, and allows us to call
 * them through Flash.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * 1) create a simple one for highlighting all strokes that go UP, LEFT, DOWN, RIGHT... Add this to
 * InkUtils
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class InkAPIBrowser {

	public static void main(String[] args) {
		new InkAPIBrowser();
	}

	private FlashCommunicationServer flash;

	/**
	 * @param paperApp
	 */
	public InkAPIBrowser() {
		// use Flash as our GUI
		startFlashAPIBrowser();
	}

	/**
	 * 
	 */
	private void sendLocationOfLastSynchedInk() {
		flash.sendMessage("<lastPenSynch fileName='TESTESTBlah'/>");
	}

	public void showFlashView() {
		// start the Flash GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File apiBrowserTML = new File(r3RootPath, "flash/bin/APIBrowser.html");
		flash.openFlashGUI(apiBrowserTML);
		flash.removeAllFlashClientListeners(); // HACK: for now..., so we always have one flash
												// listener
		flash.addFlashClientListener(new FlashListener() {
			@Override
			public void messageReceived(String command) {
				if (command.equals("apibrowserclient connected")) {
					DebugUtils.println("Flash Client Connected!");
					sendLocationOfLastSynchedInk();
				}
			}
		});
	}

	/**
	 * 
	 */
	private void startFlashAPIBrowser() {
		// Start the local messaging server
		flash = new FlashCommunicationServer();
		showFlashView();
	}
}
