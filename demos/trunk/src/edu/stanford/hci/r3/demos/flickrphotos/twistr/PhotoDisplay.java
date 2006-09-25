package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.graphics.ImageCache;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;

/**
 * <p>
 * A frame that chooses four photos from the rather large set that we have.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PhotoDisplay extends JFrame {

	public enum ActionKeys {
		SPACE_BAR_NEXT_TURN;
	}

	private static final int H_PADDING = 10;

	private static final int HSPACE = 50;

	private static final Font LOWER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 52);

	// private static final Color TWISTR_COLOR = new Color(254, 153, 41);
	// private static final Color TWISTR_COLOR = new Color(120, 198, 121);
	private static final Color TWISTR_COLOR = new Color(116, 169, 207);

	private static final Font UPPER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 72);

	private static final int V_PADDING = 20;

	private JLabel infoLabel;

	private JPanel infoPanel;

	private JPanel mainPanel;

	private double maxImageH;

	private double maxImageW;

	private AbstractAction nextTurnAction;

	private PImageWithHighlight p1Limg;

	private PImageWithHighlight p1Rimg;

	private JPanel p1Score;

	private PImageWithHighlight p2Limg;

	private PImageWithHighlight p2Rimg;

	private JPanel p2Score;

	private double panelH;

	private double panelW;

	private PCanvas picCanvas;

	private PLayer pictures;

	private JLabel pointsPanelP1;

	private JLabel pointsPanelP2;

	private JPanel scorePanel;

	private Twistr twistr;

	/**
	 * @param twistr
	 * 
	 */
	public PhotoDisplay(Twistr twistrParent) {
		twistr = twistrParent;
		setContentPane(getMainPanel());

		setupInputHandling();

		setSize(800, 600);
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private Component getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel("Twistr");
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
			mainPanel.add(getScorePanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	private Action getNextTurnAction() {
		if (nextTurnAction == null) {
			nextTurnAction = new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					DebugUtils.println("NEXT TURN");
					twistr.getPhotos();
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
	private Component getPlayer1Score() {
		if (p1Score == null) {
			p1Score = new JPanel();
			p1Score.setOpaque(false);
			final JLabel playerName = new JLabel("Player 1");
			playerName.setFont(LOWER_PANEL_FONT);
			playerName.setForeground(TWISTR_COLOR);
			final JLabel points = getPointsPanelP1();
			p1Score.add(playerName);
			p1Score.add(Box.createHorizontalStrut(HSPACE));
			p1Score.add(points);
		}
		return p1Score;
	}

	private Component getPlayer2Score() {
		if (p2Score == null) {
			p2Score = new JPanel();
			p2Score.setOpaque(false);
			final JLabel playerName = new JLabel("Player 2");
			playerName.setFont(LOWER_PANEL_FONT);
			playerName.setForeground(TWISTR_COLOR);
			final JLabel points = getPointsPanelP2();
			p2Score.add(points);
			p2Score.add(Box.createHorizontalStrut(HSPACE));
			p2Score.add(playerName);

		}
		return p2Score;
	}

	/**
	 * @return
	 */
	private JLabel getPointsPanelP1() {
		if (pointsPanelP1 == null) {
			pointsPanelP1 = new JLabel("23");
			pointsPanelP1.setFont(LOWER_PANEL_FONT);
			pointsPanelP1.setForeground(Color.LIGHT_GRAY);
		}
		return pointsPanelP1;
	}

	/**
	 * @return
	 */
	private JLabel getPointsPanelP2() {
		if (pointsPanelP2 == null) {
			pointsPanelP2 = new JLabel("2");
			pointsPanelP2.setFont(LOWER_PANEL_FONT);
			pointsPanelP2.setForeground(Color.LIGHT_GRAY);
		}
		return pointsPanelP2;
	}

	/**
	 * @return
	 */
	private Component getScorePanel() {
		if (scorePanel == null) {
			scorePanel = new JPanel();
			scorePanel.setOpaque(false);
			scorePanel.setLayout(new BorderLayout());
			scorePanel.add(getPlayer1Score(), BorderLayout.WEST);
			scorePanel.add(getPlayer2Score(), BorderLayout.EAST);
		}
		return scorePanel;
	}

	/**
	 * @param p1Left
	 * @param p1Right
	 * @param p2Left
	 * @param p2Right
	 */
	public void placeFourPhotos(File p1Left, File p1Right, File p2Left, File p2Right) {
		final Dimension size = picCanvas.getSize();
		panelW = size.getWidth();
		panelH = size.getHeight();
		maxImageW = (panelW / 2) - (H_PADDING * 2);
		maxImageH = (panelH / 2) - (V_PADDING * 2);

		if (Math.random() < 0.5) {
			// place a single photo in each of the four corners!
			p1Limg = placeSinglePhoto(p1Limg, p1Left, 0); // upper left
			p1Rimg = placeSinglePhoto(p1Rimg, p1Right, 1); // upper right
			p2Limg = placeSinglePhoto(p2Limg, p2Left, 2); // lower left
			p2Rimg = placeSinglePhoto(p2Rimg, p2Right, 3); // lower right
		} else { // to be fair... =) in case placing photos is slow
			// place a single photo in each of the four corners!
			p2Limg = placeSinglePhoto(p2Limg, p2Left, 2); // lower left
			p2Rimg = placeSinglePhoto(p2Rimg, p2Right, 3); // lower right
			p1Limg = placeSinglePhoto(p1Limg, p1Left, 0); // upper left
			p1Rimg = placeSinglePhoto(p1Rimg, p1Right, 1); // upper right
		}
	}

	/**
	 * @param pimg
	 * @param imgFile
	 * @param location
	 * @return
	 */
	private PImageWithHighlight placeSinglePhoto(PImageWithHighlight pimg, File imgFile, int location) {
		if (pimg != null) {
			pictures.removeChild(pimg);
		}
		pimg = new PImageWithHighlight(ImageCache.loadBufferedImage(imgFile));

		double w = pimg.getWidth();
		double h = pimg.getHeight();

		double scaleX = maxImageW / w;
		double scaleY = maxImageH / h;

		// choose the smaller of the two...
		double scaleFactor = Math.min(scaleX, scaleY);

		pimg.setScale(scaleFactor);

		// for centering
		double centeringOffsetX = (maxImageW - pimg.getWidth() * scaleFactor) / 2;
		double centeringOffsetY = (maxImageH - pimg.getHeight() * scaleFactor) / 2;

		// DebugUtils.println(centeringOffsetX + " " + centeringOffsetY);

		switch (location) {
		case 0:
			pimg.setOffset(H_PADDING + centeringOffsetX, V_PADDING + centeringOffsetY);
			break;
		case 1:
			pimg.setOffset(maxImageW + 3 * H_PADDING + centeringOffsetX, V_PADDING
					+ centeringOffsetY);
			break;
		case 2:
			pimg.setOffset(H_PADDING + centeringOffsetX, maxImageH + 3 * V_PADDING
					+ centeringOffsetY);
			break;
		case 3:
			pimg.setOffset(maxImageW + 3 * H_PADDING + centeringOffsetX, maxImageH + 3 * V_PADDING
					+ centeringOffsetY);
			break;
		}
		
		pictures.addChild(pimg);
		return pimg;
	}

	private void setupInputHandling() {
		InputMap inputMap = getMainPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), ActionKeys.SPACE_BAR_NEXT_TURN);

		ActionMap actionMap = getMainPanel().getActionMap();
		actionMap.put(ActionKeys.SPACE_BAR_NEXT_TURN, getNextTurnAction());
	}
}
