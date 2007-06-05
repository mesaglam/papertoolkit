package edu.stanford.hci.r3.tools.develop.inkapibrowser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.flash.FlashPenListener;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.batch.PenSynch;
import edu.stanford.hci.r3.pen.batch.PenSynchManager;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkUtils;
import edu.stanford.hci.r3.util.ArrayUtils;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * The API browser allows you to play with ink methods so that you can learn about them visually. It comprises
 * a Flash GUI and a Java server backend. It exposes a number of ink-related methods, and allows us to call
 * them through Flash.
 * 
 * The method calls all happen in java, and we send XML over to decorate or display Ink in the right way, so
 * that the Ink API Browser can present it!
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * 1) create a simple one for highlighting all strokes that go UP, LEFT, DOWN, RIGHT... Add this to InkUtils
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class InkAPIBrowser {

	/**
	 * Opens the HTML/Flash version... the tool can also be accessed through ToolExplorer.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		InkAPIBrowser inkAPIBrowser = new InkAPIBrowser();
	}

	private int currentSynchedFileIndex = -1;
	private List<Method> exposedMethods;
	private FlashCommunicationServer flash;
	private HashMap<String, Method> methodsHashMap = new HashMap<String, Method>();
	private Ink mostRecentInkObject;
	private List<File> synchedFiles;

	/**
	 * The Ink API Browser should ideally work with both synched pen files, and in real-time.
	 * 
	 * @param paperApp
	 */
	public InkAPIBrowser() {

		// retrieve the synched files
		PenSynchManager penSynchManager = new PenSynchManager();
		synchedFiles = penSynchManager.getFiles();
		// DebugUtils.println(synchedFiles);

		// use Flash as our GUI
		startFlashAPIBrowser();

		Pen p = new Pen(); // local pen
		p.addLivePenListener(new FlashPenListener(flash));
		p.startLiveMode();
	}

	
	/**
	 * The Flash GUI tells us to call a series of methods, and then return the results to the GUI...
	 * Basically, we end up applying a list of methods to the Ink, and get a resulting ink object that we send
	 * back to the GUI.
	 * 
	 * @param commands
	 */
	private void callTheseMethods(String[] commands) {
		ArrayUtils.printArray(commands);

		Ink ink = mostRecentInkObject;
		if (ink == null) {
			return;
		}

		Ink inkResult = new Ink();
		for (String methodName : commands) {
			Method method = methodsHashMap.get(methodName);
			try {
				inkResult = (Ink) method.invoke(null, ink);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		DebugUtils.println("Original Ink: " + ink.getNumStrokes());
		DebugUtils.println("New Ink: " + inkResult.getNumStrokes());

		// send this ink back to the Flash GUI, to highlight in red!
		flash.sendMessage("<highlight>" + inkResult.toXMLString(false) + "</highlight>");
	}

	/**
	 * Allows us to get the current synched file... from the penSynch directory.
	 * 
	 * @return
	 */
	private File getCurrentFile() {
		if (currentSynchedFileIndex < 0) {
			// if we never advanced...
			currentSynchedFileIndex = 0;
		}
		return synchedFiles.get(currentSynchedFileIndex);
	}

	/**
	 * Navigate to the next synch file.
	 */
	private void nextFile() {
		currentSynchedFileIndex++;
		if (currentSynchedFileIndex == synchedFiles.size()) {
			currentSynchedFileIndex = 0;
		}
		DebugUtils.println(getCurrentFile().getName());
	}

	/**
	 * Navigate to the previous synch file.
	 */
	private void prevFile() {
		currentSynchedFileIndex--;
		if (currentSynchedFileIndex < 0) {
			currentSynchedFileIndex = synchedFiles.size() - 1;
		}
		DebugUtils.println(getCurrentFile().getName());
	}

	/**
	 * This allows us to preview the ink, by rendering it to a JPEG, and putting it in a temporary HTML file
	 * for display in our browser.
	 */
	protected void saveInkFromCurrentFileToDiskAndDisplayIt() {
		PenSynch penSynch = new PenSynch(getCurrentFile());
		List<Ink> importedInk = penSynch.getImportedInk();
		int countOfInk = 0;
		List<File> renderedImages = new ArrayList<File>();
		for (Ink ink : importedInk) {
			DebugUtils.println(ink.getName());
			ink.setName(ink.getName() + countOfInk);
			File file = ink.renderToJPEGFile();
			renderedImages.add(file);
			// DebugUtils.println("Sending ink from: " + ink.getSourcePageAddress());
			flash.sendMessage("Rendered to file: " + file.getAbsolutePath());
			countOfInk++;
		}

		String html = FileUtils.readFileIntoStringBuffer(
				PaperToolkit.getResourceFile("/templates/Preview.html")).toString();
		StringBuilder sb = new StringBuilder();
		for (File f : renderedImages) {
			
			// TODO / XXX: Change this to the actual Desktop's file path
			sb.append("<img src=\"file:///C|/Documents and Settings/Ron Yeh/Desktop/" + f.getName() + "\"/>");
		}
		html = html.replace("__IMAGES__", sb.toString());

		File homeDir = FileSystemView.getFileSystemView().getHomeDirectory();
		File destFile = new File(homeDir, "Preview.html");
		FileUtils.writeStringToFile(html, destFile);
		try {
			Desktop.getDesktop().browse(destFile.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the current synch file's ink to the Flash GUI.
	 */
	private void sendInkFromCurrentFile() {
		final PenSynch penSynch = new PenSynch(getCurrentFile());
		final List<Ink> importedInk = penSynch.getImportedInk();
		for (Ink ink : importedInk) {
			DebugUtils.println("Sending ink from: " + ink.getSourcePageAddress());
			flash.sendMessage(ink.toXMLString(false));

			// keep track of this ink object
			mostRecentInkObject = ink;
		}
	}

	/**
	 * Forwards a list of method names to the Flash GUI.
	 */
	private void sendListOfExposedMethods() {
		StringBuilder sb = new StringBuilder();
		sb.append("<methods>");

		// add methods to a hashtable from name --> method object

		// enumerate InkUtils
		exposedMethods = InkUtils.getExposedMethods();
		for (Method m : exposedMethods) {
			sb.append("<method name='" + m.getName() + "' className='"
					+ m.getDeclaringClass().getSimpleName() + "'/>");

			// TODO: Will have to fix the lowercase problem =)
			methodsHashMap.put(m.getName().toLowerCase(), m);
		}
		sb.append("</methods>");
		flash.sendMessage(sb.toString());
	}

	/**
	 * Launches the Flash GUI. This is the standard HTML/Flash version... The apollo version is launched from
	 * ToolExplorer.exe. We should make sure this can work with both Apollo and HTML/Flash.
	 */
	public void showFlashView() {
		// start the Flash GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File apiBrowserHTML = new File(r3RootPath, "flash/bin/ToolWrapper.html");
		flash.addQueryParameter("toolToLoad=APIBrowser");
		flash.openFlashHTMLGUI(apiBrowserHTML);

		// HACK: for now..., so we always have one flash listener
		flash.removeAllFlashClientListeners();
		flash.addFlashClientListener(new FlashListener() {
			@Override
			public boolean messageReceived(String command) {
				if (command.equals("apibrowserclient connected")) {
					DebugUtils.println("Flash Client Connected!");
					sendListOfExposedMethods();
					return CONSUMED;
				} else if (command.equals("next")) {
					nextFile();
					sendInkFromCurrentFile();
					return CONSUMED;
				} else if (command.equals("prev")) {
					prevFile();
					sendInkFromCurrentFile();
					return CONSUMED;
				} else if (command.equals("saveimage")) {
					saveInkFromCurrentFileToDiskAndDisplayIt();
					return CONSUMED;
				} else if (command.startsWith("callmethods")) {
					String listOfCommands = command.substring(command.indexOf("[") + 1, command.indexOf("]"));
					String[] commands = listOfCommands.split(",");
					callTheseMethods(commands);
					return CONSUMED;
				} else {
					DebugUtils.println("Unhandled command: " + command);
					return NOT_CONSUMED;
				}
			}

		});
	}

	/**
	 * Launches the HTML/Flash version.
	 */
	private void startFlashAPIBrowser() {
		// Start the local messaging server
		flash = new FlashCommunicationServer();
		showFlashView();
	}
}
