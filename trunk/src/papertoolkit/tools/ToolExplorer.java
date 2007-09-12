package papertoolkit.tools;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import papertoolkit.PaperToolkit;
import papertoolkit.application.config.Constants;
import papertoolkit.flash.FlashCommunicationServer;
import papertoolkit.flash.FlashListener;
import papertoolkit.flash.FlashPenListener;
import papertoolkit.pen.Pen;
import papertoolkit.tools.design.sketch.SketchToPaperUI;
import papertoolkit.util.DebugUtils;


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

	private Pen currentPen;
	private FlashCommunicationServer flash;
	private PaperToolkit paperToolkit;
	private SketchToPaperUI sketchToPaperUI;

	/**
	 * @param paperToolkit
	 */
	public ToolExplorer(PaperToolkit tk) {
		this(tk, null, Constants.Ports.FLASH_COMMUNICATION_SERVER);
	}

	/**
	 * @param ptk
	 * @param startupMode
	 */
	public ToolExplorer(PaperToolkit ptk, String startupMode, int port) {
		// ToolExplorer is Work in Progress: it doesn't do anything much yet...
		
		paperToolkit = ptk;
		// start the Flash Communications Server, and register our listeners...
		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File toolExplorerHTML = new File(r3RootPath, "flash/bin/ToolExplorer.html");
		
		flash = new FlashCommunicationServer();
		flash.addFlashClientListener(this);
		flash.openFlashHTMLGUI(toolExplorerHTML);
	}

	/**
	 * @param listener
	 */
	public void addFlashClientListener(FlashListener listener) {
		flash.addFlashClientListener(listener);
	}

	/**
	 * @return
	 */
	public FlashCommunicationServer getFlashServer() {
		return flash;
	}

	/*
	 * @see edu.stanford.hci.r3.flash.FlashListener#messageReceived(java.lang.String)
	 */
	public boolean messageReceived(String command, String...args) {
		DebugUtils.println(command);
		if (command.equals("Connected")) {
			DebugUtils.println("ToolExplorer Connected");
			StringBuilder pens = new StringBuilder();
			pens.append("<pens>");
			final List<Pen> frequentlyUsedPens = Pen.getQuickList();
			currentPen = frequentlyUsedPens.get(0);
			for (Pen p : frequentlyUsedPens) {
				pens.append("<pen name='" + p.getName() + "' server='" + p.getPenServerName() + "' port='"
						+ p.getPenServerPort() + "'/>");
			}
			pens.append("</pens>");
			flash.sendMessage(pens.toString());
			return CONSUMED;
		} else if (command.equals("Design")) {
			sketchToPaperUI = new SketchToPaperUI(currentPen);
			sketchToPaperUI.addPenListener(new FlashPenListener(flash));
			return CONSUMED;
		} else if (command.equals("Main Menu")) {
			if (sketchToPaperUI != null) {
				sketchToPaperUI.exit();
				sketchToPaperUI = null;
			} else {

			}
			return CONSUMED;
		} else if (command.startsWith("<pen")) {
			final Pattern penPattern = Pattern.compile("<pen name='(.*?)' server='(.*?)' port='(.*?)'/>");
			final Matcher matcherPen = penPattern.matcher(command);
			if (matcherPen.find()) {
				String penName = matcherPen.group(1);
				String penServer = matcherPen.group(2);
				String penPort = matcherPen.group(3);
				DebugUtils.println("Matched: " + penName + " " + penServer + " " + penPort);
				final List<Pen> frequentlyUsedPens = Pen.getQuickList();
				for (Pen p : frequentlyUsedPens) {
					DebugUtils.println("Testing: " + p.getName() + " " + p.getPenServerName() + " "
							+ p.getPenServerPort());
					if (p.getName().equals(penName) && p.getPenServerName().equals(penServer)
							&& penPort.equals(p.getPenServerPort() + "")) {
						DebugUtils.println("Found Pen! " + p.toString());
						currentPen = p;
					}
				}
			}
			return CONSUMED;
		} else if (command.equals("exitServer")) {
			DebugUtils.println("Exiting the Application");
			System.exit(0);
			return CONSUMED;
		} else {
			return NOT_CONSUMED;
		}
	}
}
