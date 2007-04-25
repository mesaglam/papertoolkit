package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.flash.FlashPenListener;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.tools.ToolExplorer;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Opens the Whiteboard Flash/Flex application.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashWhiteboard {

	private List<Pen> pens = new ArrayList<Pen>();

	private int port;

	private Color swatchColor;

	private String title;

	private ToolExplorer toolExplorer;

	private FlashCommunicationServer flash;

	/**
	 * @param portNum
	 */
	public FlashWhiteboard(int portNum) {
		port = portNum;
	}

	/**
	 * @param pen
	 */
	public void addPen(Pen pen) {
		pens.add(pen);
		pen.startLiveMode();
	}

	/**
	 * 
	 */
	public void loadApolloGUI() {
		toolExplorer = new ToolExplorer(new PaperToolkit(), "Whiteboard", port);
		flash = toolExplorer.getFlashServer();
		toolExplorer.addFlashClientListener(getFlashListener());
	}

	private FlashListener getFlashListener() {
		return new FlashListener() {
			@Override
			public boolean messageReceived(String command) {
				if (command.equals("Whiteboard")) {
					DebugUtils.println("Whiteboard Connected!");
					
					DebugUtils.println("Color: " + swatchColor);
					
					flash.sendMessage("<swatchColor r='" + swatchColor.getRed() + "' g='"
							+ swatchColor.getGreen() + "' b='" + swatchColor.getBlue() + "'/>");
					flash.sendMessage("<title value='" + title + "'/>");

					for (Pen p : pens) {
						DebugUtils.println("Adding Pen Listener");
						p.addLivePenListener(new FlashPenListener(flash));
					}
					
					return true;
				} else {
					DebugUtils.println("Flash Whiteboard Unhandled command: " + command);
					return false;
				}
			}
		};
	}

	/**
	 * Loads the HTML Version...
	 */
	public void load() {
		// start the local server for sending ink over to the Flash client app
		flash = new FlashCommunicationServer(port);
		flash.addFlashClientListener(getFlashListener());

		// generate the HTML file on the fly, to contain our SWF
		final File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File inputWhiteBoardHTML = new File(r3RootPath, "flash/bin/ToolWrapper.html");
		String fileStr = FileUtils.readFileIntoStringBuffer(inputWhiteBoardHTML, true).toString();
		fileStr = fileStr.replace("PORT_NUM", port + "");
		final File outputWhiteBoardHTML = new File(r3RootPath, "flash/bin/Whiteboard_" + port + ".html");
		FileUtils.writeStringToFile(fileStr, outputWhiteBoardHTML);
		URI uri = outputWhiteBoardHTML.toURI();
		try {
			DebugUtils.println("Loading the Flash GUI...");
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param color
	 */
	public void setSwatchColor(Color color) {
		swatchColor = color;
	}

	/**
	 * @param titleStr
	 */
	public void setTitle(String titleStr) {
		title = titleStr;
	}
}
