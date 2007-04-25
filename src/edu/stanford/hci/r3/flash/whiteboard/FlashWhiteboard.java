package edu.stanford.hci.r3.flash.whiteboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.flash.FlashListener;
import edu.stanford.hci.r3.flash.FlashPenListener;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.tools.ToolExplorer;
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

	private List<Pen> pens = new ArrayList<Pen>();

	private int port;

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
	 * 
	 */
	public void load() {
		toolExplorer = new ToolExplorer(new PaperToolkit(), "Whiteboard", port);
		toolExplorer.addFlashClientListener(new FlashListener() {
			@Override
			public boolean messageReceived(String command) {
				if (command.equals("Whiteboard")) {
					DebugUtils.println("Whiteboard Connected!");
					toolExplorer.sendMessage("<swatchColor r='" + swatchColor.getRed() + "' g='"
							+ swatchColor.getGreen() + "' b='" + swatchColor.getBlue() + "'/>");
					toolExplorer.sendMessage("<title value='" + title + "'/>");

					for (Pen p : pens) {
						DebugUtils.println("Adding Pen Listener");
						p.addLivePenListener(new FlashPenListener(toolExplorer.getFlashServer()));
					}
					
					return true;
				} else {
					DebugUtils.println("Unhandled command: " + command);
					return false;
				}
			}
		});
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
