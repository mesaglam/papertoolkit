package papertoolkit.demos.gigaprints2006.twistr;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;

import papertoolkit.util.DebugUtils;
import papertoolkit.util.graphics.ImageCache;

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

	private static final Font LOWER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 36);

	// private static final Color TWISTR_COLOR = new Color(254, 153, 41);
	// private static final Color TWISTR_COLOR = new Color(120, 198, 121);
	private static final Color TWISTR_COLOR = new Color(116, 169, 207);

	private static final Font UPPER_PANEL_FONT = new Font("Trebuchet MS", Font.BOLD, 52);

	private static final int V_PADDING = 20;

	private JLabel infoLabel;

	private JPanel infoPanel;

	private JPanel mainPanel;

	private double maxImageH;

	private double maxImageW;

	private AbstractAction nextTurnAction;

	private int numPointsThisTurn = 0;

	private int numTurnsLeft = Twistr.MAX_NUM_TURNS;

	private JPanel numTurnsLeftPanel;

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

	private JLabel turnsRemaining;

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
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void clearHighlightP1LImage() {
		p1Limg.setSelected(false);
		p1Limg.setIncorrectlySelected(false);
	}

	public void clearHighlightP1RImage() {
		p1Rimg.setSelected(false);
		p1Rimg.setIncorrectlySelected(false);
	}

	public void clearHighlightP2LImage() {
		p2Limg.setSelected(false);
		p2Limg.setIncorrectlySelected(false);
	}

	public void clearHighlightP2RImage() {
		p2Rimg.setSelected(false);
		p2Rimg.setIncorrectlySelected(false);
	}

	/**
	 * @return
	 */
	private Component getBottomPanel() {
		if (scorePanel == null) {
			scorePanel = new JPanel();
			scorePanel.setOpaque(false);
			scorePanel.setLayout(new BorderLayout());
			scorePanel.add(getNumTurnsLeftPanel(), BorderLayout.WEST);
			scorePanel.add(getPlayer2Score(), BorderLayout.EAST);
		}
		return scorePanel;
	}

	private Component getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel();
			infoLabel.setFont(UPPER_PANEL_FONT);
			infoLabel.setForeground(TWISTR_COLOR);
			updateInfoLabel();
		}
		return infoLabel;
	}

	/**
	 * @return
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setBackground(Color.BLACK);
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getTopPanel(), BorderLayout.NORTH);
			mainPanel.add(getPictureCanvas(), BorderLayout.CENTER);
			mainPanel.add(getBottomPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	/**
	 * @return
	 */
	private Action getNextTurnAction() {
		if (nextTurnAction == null) {
			nextTurnAction = new AbstractAction() {

				public void actionPerformed(ActionEvent ae) {
					DebugUtils.println("NEXT TURN");
					updateScores();
					numTurnsLeft = twistr.nextTurn();
					if (numTurnsLeft == 0) {
						// declare the winner!
						DebugUtils.println("Declare Winner Now");
						twistr.declareWinner();
						return;
					}
					numPointsThisTurn = twistr.getPointsForThisTurn();

					updateTurnsRemainingLabel();
					updateInfoLabel();

					twistr.getPhotos();
				}
			};
		}
		return nextTurnAction;
	}

	private Component getNumTurnsLeftPanel() {
		if (numTurnsLeftPanel == null) {
			numTurnsLeftPanel = new JPanel();
			numTurnsLeftPanel.setOpaque(false);
			final JLabel turnsLabel = new JLabel("Turns Remaining: ");
			turnsLabel.setFont(LOWER_PANEL_FONT);
			turnsLabel.setForeground(TWISTR_COLOR);
			turnsRemaining = new JLabel(numTurnsLeft + " ");
			turnsRemaining.setFont(LOWER_PANEL_FONT);
			turnsRemaining.setForeground(TWISTR_COLOR);
			numTurnsLeftPanel.setLayout(new FlowLayout());
			numTurnsLeftPanel.add(turnsLabel);
			numTurnsLeftPanel.add(Box.createHorizontalStrut(HSPACE));
			numTurnsLeftPanel.add(turnsRemaining);

			updateTurnsRemainingLabel();
		}
		return numTurnsLeftPanel;
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
			p2Score.add(playerName);
			p2Score.add(Box.createHorizontalStrut(HSPACE));
			p2Score.add(points);

		}
		return p2Score;
	}

	/**
	 * @return
	 */
	private JLabel getPointsPanelP1() {
		if (pointsPanelP1 == null) {
			pointsPanelP1 = new JLabel("0");
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
			pointsPanelP2 = new JLabel("0");
			pointsPanelP2.setFont(LOWER_PANEL_FONT);
			pointsPanelP2.setForeground(Color.LIGHT_GRAY);
		}
		return pointsPanelP2;
	}

	/**
	 * @return
	 */
	private Component getTopPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			infoPanel.setOpaque(false);
			infoPanel.setLayout(new BorderLayout());
			infoPanel.add(getInfoLabel(), BorderLayout.WEST);
			infoPanel.add(getPlayer1Score(), BorderLayout.EAST);
		}
		return infoPanel;
	}

	public void highlightP1LImage(boolean OK) {
		if (p1Limg != null) {
			if (OK) {
				p1Limg.setSelected(true);
				p1Limg.setIncorrectlySelected(false);
			} else {
				p1Limg.setSelected(false);
				p1Limg.setIncorrectlySelected(true);
			}
		}
	}

	public void highlightP1RImage(boolean OK) {
		if (p1Rimg != null) {
			if (OK) {
				p1Rimg.setSelected(true);
				p1Rimg.setIncorrectlySelected(false);
			} else {
				p1Rimg.setSelected(false);
				p1Rimg.setIncorrectlySelected(true);
			}
		}
	}

	public void highlightP2LImage(boolean OK) {
		if (p2Limg != null) {
			if (OK) {
				p2Limg.setSelected(true);
				p2Limg.setIncorrectlySelected(false);
			} else {
				p2Limg.setSelected(false);
				p2Limg.setIncorrectlySelected(true);
			}
		}
	}

	public void highlightP2RImage(boolean OK) {
		if (p2Rimg != null) {
			if (OK) {
				p2Rimg.setSelected(true);
				p2Rimg.setIncorrectlySelected(false);
			} else {
				p2Rimg.setSelected(false);
				p2Rimg.setIncorrectlySelected(true);
			}
		}
	}

	public void nextTurn() {
		getNextTurnAction().actionPerformed(null);
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
		pictures.repaint();
	}

	/**
	 * @param pimg
	 * @param imgFile
	 * @param location
	 * @return
	 */
	private PImageWithHighlight placeSinglePhoto(PImageWithHighlight pimg, File imgFile,
			int location) {
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

	/**
	 * 
	 */
	private void updateInfoLabel() {
		infoLabel.setText("Twistr :: [" + numPointsThisTurn + "]");
	}

	private void updateScores() {
		pointsPanelP1.setText(twistr.getP1Score() + "");
		pointsPanelP2.setText(twistr.getP2Score() + "");
	}

	/**
	 * 
	 */
	private void updateTurnsRemainingLabel() {
		turnsRemaining.setText(numTurnsLeft + " ");
	}
}
