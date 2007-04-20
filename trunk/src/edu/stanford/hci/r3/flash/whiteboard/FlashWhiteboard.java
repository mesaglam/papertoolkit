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

	private FlashCommunicationServer flash;

	private List<Pen> pens = new ArrayList<Pen>();

	private int port;

	private Color swatchColor;

	private String title;

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
		pen.addLivePenListener(new FlashPenListener(flash));
	}

	/**
	 * 
	 */
	public void load() {
		// start the local server for sending ink over to the Flash client app
		flash = new FlashCommunicationServer(port);
		flash.addFlashClientListener(new FlashListener() {
			@Override
			public void messageReceived(String command) {
				if (command.equals("Flash Client Connected")) {
					DebugUtils.println("Connected!");
					flash.sendMessage("<swatchColor r='" + swatchColor.getRed() + "' g='"
							+ swatchColor.getGreen() + "' b='" + swatchColor.getBlue() + "'/>");
					flash.sendMessage("<title value='" + title + "'/>");
				} else {
					DebugUtils.println("Unhandled command: " + command);
				}
			}
		});

		File r3RootPath = PaperToolkit.getToolkitRootPath();
		File whiteBoardHTML = new File(r3RootPath, "flash/bin/Whiteboard.html");
		String fileStr = FileUtils.readFileIntoStringBuffer(whiteBoardHTML, true).toString();
		fileStr = fileStr.replace("PORT_NUM", port + "");
		whiteBoardHTML = new File(r3RootPath, "flash/bin/Whiteboard_" + port + ".html");
		FileUtils.writeStringToFile(fileStr, whiteBoardHTML);
		URI uri = whiteBoardHTML.toURI();
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
