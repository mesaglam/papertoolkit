package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.wraplog.SystemLogger;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.actions.types.OpenURL2Action;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
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

	public FlashWhiteboard(int portNum) {
		port = portNum;
	}

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

	public void addPen(Pen pen) {
		pens.add(pen);
		pen.startLiveMode();
		pen.addLivePenListener(new PenListener() {

			@Override
			public void penDown(PenSample sample) {
				flash.sendMessage(sample.toXMLString());
			}

			@Override
			public void penUp(PenSample sample) {
				flash.sendMessage(sample.toXMLString());
			}

			@Override
			public void sample(PenSample sample) {
				flash.sendMessage(sample.toXMLString());
			}

		});
	}

	public void setSwatchColor(Color color) {
		swatchColor = color;
	}
}
