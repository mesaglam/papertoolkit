package papertoolkit.flash.tools;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import papertoolkit.PaperToolkit;
import papertoolkit.flash.ExternalCommunicationServer;
import papertoolkit.flash.ExternalListener;
import papertoolkit.flash.FlashPenListener;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * Opens the Whiteboard Flex application.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashWhiteboard {

	/**
	 * Default to Black background.
	 */
	private Color bgColor = Color.BLACK;

	/**
	 * Initialize it with an empty stroke...
	 */
	private InkStroke currInkStroke = new InkStroke();

	/**
	 * The object that we use to communicate with the Flash GUI.
	 */
	private ExternalCommunicationServer flash;

	/**
	 * Default to white Ink.
	 */
	private Color inkColor = Color.WHITE;

	/**
	 * Stores the ink strokes.
	 */
	private Ink inkWell = new Ink();

	/**
	 * A list of the active pens. Their strokes are displayed in this whiteboard.
	 */
	private List<InputDevice> pens = new ArrayList<InputDevice>();

	/**
	 * Which socket port should we use to communicate over?
	 */
	private int port;

	/**
	 * TODO: We may want to flip or scale the incoming ink.
	 */
	private double rotation = 0;

	private double scaleX = 1;

	private double scaleY = 1;
	/**
	 * A little block of color on the upper right corner, for helping us match the output window with the
	 * input pen (if you have color-coded your pens).
	 */
	private Color swatchColor = Color.WHITE;
	/**
	 * Displayed in the GUI.
	 */
	private String title = "Ink Display";

	/**
	 * @param portNum
	 */
	public FlashWhiteboard(int portNum) {
		port = portNum;
	}

	/**
	 * We should be able to add multiple pens to a single whiteboard.
	 * 
	 * @param pen
	 */
	public void addPen(InputDevice pen) {
		pens.add(pen);
		pen.startLiveMode();
	}

	/**
	 * @return
	 */
	private ExternalListener getFlashListener() {
		return new ExternalListener() {
			public boolean messageReceived(String command, String... args) {
				if (command.equals("Whiteboard")) {
					// DebugUtils.println("Whiteboard Connected!");
					// DebugUtils.println("Color: " + swatchColor);
					flash.sendMessage("<swatchColor r='" + swatchColor.getRed() + "' g='"
							+ swatchColor.getGreen() + "' b='" + swatchColor.getBlue() + "'/>");
					flash.sendMessage("<inkColor r='" + inkColor.getRed() + "' g='" + inkColor.getGreen()
							+ "' b='" + inkColor.getBlue() + "'/>");
					flash.sendMessage("<bgColor r='" + bgColor.getRed() + "' g='" + bgColor.getGreen()
							+ "' b='" + bgColor.getBlue() + "'/>");
					flash.sendMessage("<title value='" + title + "'/>");

					for (InputDevice p : pens) {
						// DebugUtils.println("Adding Pen Listener");
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
			public void penDown(PenSample sample) {
				currInkStroke = new InkStroke();
				currInkStroke.addSample(sample);
			}

			public void penUp(PenSample sample) {
				inkWell.addStroke(currInkStroke);
			}

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
		flash = new ExternalCommunicationServer(port);
		flash.addFlashClientListener(getFlashListener());
		final File r3RootPath = PaperToolkit.getToolkitRootPath();
		final File inputWhiteBoardHTML = new File(r3RootPath, "flash/bin/ToolWrapper.html");
		flash.addQueryParameter("toolToLoad=Whiteboard");
		flash.openFlashHTMLGUI(inputWhiteBoardHTML);
	}

	/**
	 * @param c
	 */
	public void setBackgroundColor(Color c) {
		bgColor = c;
	}

	/**
	 * @param color
	 */
	public void setInkColor(Color color) {
		inkColor = color;
	}

	/**
	 * @param numDegrees
	 */
	public void setRotation(double numDegrees) {
		rotation = numDegrees;
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
