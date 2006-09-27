package edu.stanford.hci.r3.demos.sketch;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkPCanvas;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.graphics.ImageCache;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BuddySketchGUI extends JFrame {

	public enum ActionKeys {
		SPACE_BAR;
	}

	public static final Color DEFAULT_BUDDY_INK_COLOR = new Color(0.95f, 0.85f, .75f, 0.8f);

	public static final Color DEFAULT_MY_INK_COLOR = new Color(0.75f, 0.85f, .95f, 0.8f);

	private static final int H_PADDING = 10;

	private static final Font LOWER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 30);

	private static final String PICKED_PHOTO = "Your buddy picked a photo...";

	private static final String SKETCHING = "Your buddy is sketching...";

	// private static final Color TWISTR_COLOR = new Color(254, 153, 41);
	// private static final Color TWISTR_COLOR = new Color(120, 198, 121);
	private static final Color TWISTR_COLOR = new Color(116, 169, 207);

	private static final Font UPPER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 56);

	private static final int V_PADDING = 20;

	private static final String WAITING = "Waiting...";

	private BuddySketch buddyApp;

	private JLabel infoLabel;

	private JPanel infoPanel;

	private File lastBuddyFileDisplayed;

	private PImage lastBuddyImage;

	private File lastFileDisplayed;

	private PImage lastImage;

	private InkPCanvas mainCanvas;

	private PLayer mainCanvasContentLayer;

	private JPanel mainPanel;

	private double maxImageH = 200;

	private double maxImageW = 200;

	private JLabel message;

	final int MONITOR_WIDTH = 1024;

	private JPanel scorePanel;

	private JPanel status;

	private AbstractAction toggleZoomAction;

	/**
	 * @param buddyApp
	 * 
	 */
	public BuddySketchGUI(BuddySketch buddyParent) {
		buddyApp = buddyParent;
		setContentPane(getMainPanel());

		setupInputHandling();

		setSize(800, 600);
		setUndecorated(true);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * @param ink
	 */
	public void addBuddyInkToCanvas(Ink ink) {
		ink.setColor(DEFAULT_BUDDY_INK_COLOR);
		mainCanvas.addInk(ink);
	}

	/**
	 * @param ink
	 */
	public void addInkToCanvas(Ink ink) {
		ink.setColor(DEFAULT_MY_INK_COLOR);
		mainCanvas.addInk(ink);
	}

	/**
	 * @param f
	 */
	public void displayBuddyPhoto(File imgFile) {
		if (!imgFile.exists()) {
			DebugUtils.println("File Does not exist: " + imgFile.getAbsolutePath());
		}
		final PImage p = new PImage(ImageCache.loadBufferedImage(imgFile));
		placeBuddyPhoto(p, imgFile);
	}

	/**
	 * @param imgFile
	 */
	public void displayPhoto(File imgFile) {
		if (!imgFile.exists()) {
			DebugUtils.println("File Does not exist: " + imgFile.getAbsolutePath());
		}
		final PImage p = new PImage(ImageCache.loadBufferedImage(imgFile));
		placeMyPhoto(p, imgFile);
	}

	private Component getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel("BuddySketch");
			infoLabel.setFont(UPPER_PANEL_FONT);
			infoLabel.setForeground(TWISTR_COLOR);
		}
		return infoLabel;
	}

	private Component getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setOpaque(false);
			infoPanel.add(getInfoLabel());
			infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		}
		return infoPanel;
	}

	/**
	 * @return
	 */
	private PCanvas getMainCanvas() {
		if (mainCanvas == null) {
			mainCanvas = new InkPCanvas();
			mainCanvas.setBackground(new Color(40, 40, 40));
			mainCanvasContentLayer = mainCanvas.getLayer();
		}
		return mainCanvas;
	}

	/**
	 * @return
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.BLACK);
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getInfoPanel(), BorderLayout.NORTH);
			mainPanel.add(getMainCanvas(), BorderLayout.CENTER);
			mainPanel.add(getStatusPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	private Action getNextTurnAction() {
		if (toggleZoomAction == null) {
			toggleZoomAction = new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					DebugUtils.println("TOGGLE ZOOM...");
				}
			};
		}
		return toggleZoomAction;
	}

	/**
	 * @return
	 */
	private Component getStatusMessageFromOtherPerson() {
		if (status == null) {
			status = new JPanel();
			status.setOpaque(false);
			message = new JLabel(WAITING);
			message.setFont(LOWER_PANEL_FONT);
			message.setForeground(TWISTR_COLOR);
			status.add(message);
		}
		return status;
	}

	/**
	 * @return
	 */
	private Component getStatusPanel() {
		if (scorePanel == null) {
			scorePanel = new JPanel();
			scorePanel.setOpaque(false);
			scorePanel.setLayout(new BorderLayout());
			scorePanel.add(getStatusMessageFromOtherPerson(), BorderLayout.WEST);
		}
		return scorePanel;
	}

	/**
	 * @param pimg
	 * @param imgFile
	 */
	private void placeBuddyPhoto(PImage pimg, File imgFile) {
		message.setText(PICKED_PHOTO);

		if (lastBuddyImage != null) {
			mainCanvasContentLayer.getCamera(0).removeChild(lastBuddyImage);
			lastBuddyImage = null;
			if (imgFile.equals(lastBuddyFileDisplayed)) {
				// hide the file instead, because it was tapped twice
				return;
			}
		}
		pimg = new PImage(ImageCache.loadBufferedImage(imgFile));

		double w = pimg.getWidth();
		double h = pimg.getHeight();

		double scaleX = maxImageW / w;
		double scaleY = maxImageH / h;

		// choose the smaller of the two...
		double scaleFactor = Math.min(scaleX, scaleY);

		pimg.setScale(scaleFactor);

		pimg.setOffset(MONITOR_WIDTH - (scaleFactor * w) - H_PADDING, V_PADDING);

		mainCanvasContentLayer.getCamera(0).addChild(pimg);
		lastBuddyImage = pimg;
		mainCanvasContentLayer.getCamera(0).repaint(); // update the canvas

		lastBuddyFileDisplayed = imgFile;
	}

	/**
	 * @param pimg
	 * @param imgFile
	 * @param location
	 * @return
	 */
	private void placeMyPhoto(PImage pimg, File imgFile) {
		if (lastImage != null) {
			mainCanvasContentLayer.getCamera(0).removeChild(lastImage);
			lastImage = null;
			if (imgFile.equals(lastFileDisplayed)) {
				// hide the file instead, because it was tapped twice
				return;
			}
		}
		pimg = new PImage(ImageCache.loadBufferedImage(imgFile));

		double w = pimg.getWidth();
		double h = pimg.getHeight();

		double scaleX = maxImageW / w;
		double scaleY = maxImageH / h;

		// choose the smaller of the two...
		double scaleFactor = Math.min(scaleX, scaleY);

		pimg.setScale(scaleFactor);

		pimg.setOffset(H_PADDING, V_PADDING);

		mainCanvasContentLayer.getCamera(0).addChild(pimg);
		lastImage = pimg;
		mainCanvasContentLayer.getCamera(0).repaint(); // update the canvas

		lastFileDisplayed = imgFile;
	}

	private void setupInputHandling() {
		InputMap inputMap = getMainPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), ActionKeys.SPACE_BAR);

		ActionMap actionMap = getMainPanel().getActionMap();
		actionMap.put(ActionKeys.SPACE_BAR, getNextTurnAction());
	}
}
