package edu.stanford.hci.r3.tools.develop.inkapibrowser;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.pen.batch.PenSynchManager;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Flash GUI, Java Server backend. It exposes a number of ink-related methods, and allows us to call
 * them through Flash.
 * 
 * BIG IDEA... the method calls all happen in java, and we send XML over to decorate the Ink in the
 * right way, so that the Ink API Browser can present it!
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
	private List<File> synchedFiles;
	private int currentSynchedFileIndex = 0;

	/**
	 * @param paperApp
	 */
	public InkAPIBrowser() {
		PenSynchManager penSynchManager = new PenSynchManager();
		synchedFiles = penSynchManager.getFiles();
		// DebugUtils.println(synchedFiles);

		// use Flash as our GUI
		startFlashAPIBrowser();
	}

	/**
	 * 
	 */
	private void nextFile() {
		currentSynchedFileIndex++;
		if (currentSynchedFileIndex == synchedFiles.size()) {
			currentSynchedFileIndex = 0;
		}
		DebugUtils.println(synchedFiles.get(currentSynchedFileIndex).getName());
	}

	/**
	 * 
	 */
	private void prevFile() {
		currentSynchedFileIndex--;
		if (currentSynchedFileIndex == -1) {
			currentSynchedFileIndex = synchedFiles.size() - 1;
		}
		DebugUtils.println(synchedFiles.get(currentSynchedFileIndex).getName());
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
		final File apiBrowserTML = new File(r3RootPath, "flash/bin/APIBrowserDefault.html");
		flash.openFlashGUI(apiBrowserTML);
		flash.removeAllFlashClientListeners(); // HACK: for now..., so we always have one flash
		// listener
		flash.addFlashClientListener(new FlashListener() {
			@Override
			public void messageReceived(String command) {
				if (command.equals("apibrowserclient connected")) {
					DebugUtils.println("Flash Client Connected!");
					sendLocationOfLastSynchedInk();
				} else if (command.equals("next")) {
					nextFile();
				} else if (command.equals("prev")) {
					prevFile();
				} else {
					// DebugUtils.println("Unhandled command: " + command);
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
