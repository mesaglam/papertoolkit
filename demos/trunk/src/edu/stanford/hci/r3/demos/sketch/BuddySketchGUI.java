package edu.stanford.hci.r3.demos.sketch;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;

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
		SPACE_BAR_NEXT_TURN;
	}

	private static final int H_PADDING = 10;

	private static final int HSPACE = 50;

	private static final Font LOWER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 30);

	private static final String PICKED_PHOTO = "Your buddy picked a photo...";

	private static final String SKETCHING = "Your buddy is sketching...";

	// private static final Color TWISTR_COLOR = new Color(254, 153, 41);
	// private static final Color TWISTR_COLOR = new Color(120, 198, 121);
	private static final Color TWISTR_COLOR = new Color(116, 169, 207);

	private static final Font UPPER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 72);

	private static final int V_PADDING = 20;

	private static final String WAITING = "Waiting...";

	private BuddySketch buddyApp;

	private JLabel infoLabel;

	private JPanel infoPanel;

	private JPanel mainPanel;

	private double maxImageH = 200;

	private double maxImageW = 200;

	private AbstractAction nextTurnAction;

	private PImage p1Limg;

	private PImage p1Rimg;

	private JPanel p1Score;

	private PImage p2Limg;

	private PImage p2Rimg;

	private double panelH;

	private double panelW;

	private PCanvas picCanvas;

	private PLayer pictures;

	private JLabel pointsPanelP1;

	private JPanel scorePanel;

	private PImage lastImage;

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
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.BLACK);
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getInfoPanel(), BorderLayout.NORTH);
			mainPanel.add(getPictureCanvas(), BorderLayout.CENTER);
			mainPanel.add(getStatusPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	private Action getNextTurnAction() {
		if (nextTurnAction == null) {
			nextTurnAction = new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					DebugUtils.println("NEXT TURN");
				}
			};
		}
		return nextTurnAction;
	}

	/**
	 * @return
	 */
	private PCanvas getPictureCanvas() {
		if (picCanvas == null) {
			picCanvas = new PCanvas();
			picCanvas.setBackground(new Color(40, 40, 40));
			pictures = picCanvas.getLayer();
		}
		return picCanvas;
	}

	/**
	 * @return
	 */
	private Component getStatusMessageFromOtherPerson() {
		if (p1Score == null) {
			p1Score = new JPanel();
			p1Score.setOpaque(false);
			final JLabel playerName = new JLabel(WAITING);
			playerName.setFont(LOWER_PANEL_FONT);
			playerName.setForeground(TWISTR_COLOR);
			p1Score.add(playerName);
		}
		return p1Score;
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
	 * @param location
	 * @return
	 */
	private PImage placeSinglePhoto(PImage pimg, File imgFile) {
		if (lastImage != null) {
			pictures.removeChild(lastImage);
		}
		pimg = new PImage(ImageCache.loadBufferedImage(imgFile));

		double w = pimg.getWidth();
		double h = pimg.getHeight();

		double scaleX = maxImageW / w;
		double scaleY = maxImageH / h;

		// choose the smaller of the two...
		double scaleFactor = Math.min(scaleX, scaleY);

		pimg.setScale(scaleFactor);

		pimg.setOffset(0, 0);

		pictures.addChild(pimg);
		lastImage = pimg;
		pictures.repaint(); // update the canvas
		return pimg;
	}

	private void setupInputHandling() {
		InputMap inputMap = getMainPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), ActionKeys.SPACE_BAR_NEXT_TURN);

		ActionMap actionMap = getMainPanel().getActionMap();
		actionMap.put(ActionKeys.SPACE_BAR_NEXT_TURN, getNextTurnAction());
	}

	/**
	 * @param imgFile
	 */
	public void displayPhoto(File imgFile) {
		DebugUtils.println("File Exists: " + imgFile.exists());
		PImage p = new PImage(ImageCache.loadBufferedImage(imgFile));
		placeSinglePhoto(p, imgFile);
	}
}
