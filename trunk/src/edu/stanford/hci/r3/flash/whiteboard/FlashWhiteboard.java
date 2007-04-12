package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.DebugUtils;

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

	private FlashInkRelayServer flash;

	private List<Pen> pens = new ArrayList<Pen>();

	public FlashWhiteboard() {

		// start the local server for sending ink over to the Flash client app
		flash = new FlashInkRelayServer();

		File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File whiteBoardHTML = new File(r3RootPath, "flash/bin/Whiteboard.html");
		try {
			DebugUtils.println("Loading the Flash GUI...");
			Desktop.getDesktop().browse(whiteBoardHTML.toURI());
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
}
