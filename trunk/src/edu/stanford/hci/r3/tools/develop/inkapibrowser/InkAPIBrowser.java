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
import edu.stanford.hci.r3.pen.batch.PenSynch;
import edu.stanford.hci.r3.pen.batch.PenSynchManager;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkUtils;
import edu.stanford.hci.r3.util.ArrayUtils;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

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

	private int currentSynchedFileIndex = -1;
	private FlashCommunicationServer flash;
	private List<File> synchedFiles;
	private List<Method> exposedMethods;
	private HashMap<String, Method> methodsHashMap = new HashMap<String, Method>();
	private Ink mostRecentInkObject;

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
	 * 
	 */
	private void nextFile() {
		currentSynchedFileIndex++;
		if (currentSynchedFileIndex == synchedFiles.size()) {
			currentSynchedFileIndex = 0;
		}
		DebugUtils.println(getCurrentFile().getName());
	}

	/**
	 * 
	 */
	private void prevFile() {
		currentSynchedFileIndex--;
		if (currentSynchedFileIndex < 0) {
			currentSynchedFileIndex = synchedFiles.size() - 1;
		}
		DebugUtils.println(getCurrentFile().getName());
	}

	/**
	 * 
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
			sb.append("<img src=\"file:///C|/Documents and Settings/Ron Yeh/Desktop/" + f.getName()
					+ "\"/>");
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
	 * 
	 */
	private void sendInkFromCurrentFile() {
		PenSynch penSynch = new PenSynch(getCurrentFile());
		List<Ink> importedInk = penSynch.getImportedInk();
		for (Ink ink : importedInk) {
			DebugUtils.println("Sending ink from: " + ink.getSourcePageAddress());
			flash.sendMessage(ink.toXMLString(false));

			mostRecentInkObject = ink;
		}
	}

	/**
	 * 
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

	public void showFlashView() {
		// start the Flash GUI
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File apiBrowserTML = new File(r3RootPath, "flash/bin/APIBrowserDefault.html");
		flash.openFlashHTMLGUI(apiBrowserTML);
		flash.removeAllFlashClientListeners(); // HACK: for now..., so we always have one flash
		// listener
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
					String listOfCommands = command.substring(command.indexOf("[") + 1, command
							.indexOf("]"));
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
	 * 
	 */
	private void startFlashAPIBrowser() {
		// Start the local messaging server
		flash = new FlashCommunicationServer();
		showFlashView();
	}
}
