package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashCommunicationServer;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.flash.FlashPenListener;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
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

	private InkStroke currInkStroke;

	private FlashCommunicationServer flash;

	private Ink inkWell = new Ink();

	private List<Pen> pens = new ArrayList<Pen>();

	private int port;

	/**
	 * A little block of color on the upper right corner, for helping us match the output window with the
	 * input pen (if you have color-coded your pens).
	 */
	private Color swatchColor;

	private String title;

	private ToolExplorer toolExplorer;

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
	 * @return
	 */
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
						p.addLivePenListener(getInkListener());
					}
					return CONSUMED;
				} else if (command.equals("LoadInk")) {
					DebugUtils.println("LoadInk Not Implemented");
					return CONSUMED;
				} else if (command.equals("SaveInk")) {
					DebugUtils.println("SaveInk Not Implemented");
					DebugUtils.println("Saving " + inkWell.getNumStrokes() + " strokes");
					String fileName = title + "_" + FileUtils.getCurrentTimeForUseInASortableFileName()
							+ ".xml";
					File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
					DebugUtils.println(desktopDir + "/" + fileName);
					inkWell.saveToXMLFile(new File(desktopDir, fileName));
					return CONSUMED;
				} else {
					DebugUtils.println("Flash Whiteboard Unhandled command: " + command);
					return NOT_CONSUMED;
				}
			}

		};
	}

	/**
	 * @return the listener for collecting ink strokes.
	 */
	private PenListener getInkListener() {
		return new PenListener() {
			@Override
			public void penDown(PenSample sample) {
				currInkStroke = new InkStroke();
				currInkStroke.addSample(sample);
			}

			@Override
			public void penUp(PenSample sample) {
				inkWell.addStroke(currInkStroke);
			}

			@Override
			public void sample(PenSample sample) {
				currInkStroke.addSample(sample);
			}
		};
	}

	/**
	 * Loads the HTML Version... You can launch multiple instances of this.
	 */
	public void load() {
		// start the local server for sending ink over to the Flash client app
		flash = new FlashCommunicationServer(port);
		flash.addFlashClientListener(getFlashListener());
		final File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File inputWhiteBoardHTML = new File(r3RootPath, "flash/bin/ToolWrapper.html");
		flash.openFlashHTMLGUI(inputWhiteBoardHTML);
	}

	/**
	 * Loads the Apollo version. It's a native EXE that you have compiled with the Apollo builder. However, it
	 * seems you can't launch multiple instances. Boo.
	 */
	public void loadApolloGUI() {
		toolExplorer = new ToolExplorer(new PaperToolkit(), "Whiteboard", port);
		flash = toolExplorer.getFlashServer();
		toolExplorer.addFlashClientListener(getFlashListener());
	}

	/**
	 * @param color
	 *            shows up in a box in the upper right corner of the display.
	 */
	public void setSwatchColor(Color color) {
		swatchColor = color;
	}

	/**
	 * @param titleStr
	 *            shows up in the display as the page's title.
	 */
	public void setTitle(String titleStr) {
		title = titleStr;
	}
}
