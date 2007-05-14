package edu.stanford.hci.r3.demos.gigaprints2006.twistr;

import java.awt.*;

import javax.swing.*;

import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a>
 *         (ronyeh(AT)cs.stanford.edu)
 */
public class WinnerFrame extends JFrame {
	private int winningScore;

	private int losingScore;

	private String name;

	public WinnerFrame(int topScore, int loserScore, String winnerName) {
		winningScore = topScore;
		losingScore = loserScore;
		name = winnerName;

		setSize(800, 600);
		setUndecorated(true);

		setContentPane(getWinnerPanel());
		setLocation(WindowUtils.getWindowOrigin(this,
				WindowUtils.DESKTOP_CENTER));

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * 
	 */
	public WinnerFrame(int tieScore) {
		winningScore = tieScore;
		losingScore = tieScore;

		setSize(800, 600);
		setUndecorated(true);

		setContentPane(getTiePanel());
		setLocation(WindowUtils.getWindowOrigin(this,
				WindowUtils.DESKTOP_CENTER));

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static final Color TWISTR_COLOR = new Color(116, 169, 207);

	private static final Font UPPER_PANEL_FONT = new Font("Trebuchet MS",
			Font.BOLD, 52);

	/**
	 * @return
	 */
	private Container getTiePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
				Color.LIGHT_GRAY));
		panel.setBackground(Color.BLACK);
		JLabel message1 = new JLabel("OH NOOOOO......");
		message1.setFont(UPPER_PANEL_FONT);
		message1.setForeground(TWISTR_COLOR);
		JLabel message2 = new JLabel("It was a TIE GAME!!!!");
		message2.setFont(UPPER_PANEL_FONT);
		message2.setForeground(TWISTR_COLOR);
		JLabel message3 = new JLabel(winningScore + " points apiece!");
		message3.setFont(UPPER_PANEL_FONT);
		message3.setForeground(TWISTR_COLOR);
		panel.setLayout(new FlowLayout());
		panel.add(message1);
		panel.add(message2);
		panel.add(message3);
		return panel;
	}

	/**
	 * @return
	 */
	private Container getWinnerPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
				Color.LIGHT_GRAY));
		panel.setBackground(Color.BLACK);
		JLabel message1 = new JLabel("The winner, by a score of ");
		JLabel message2 = new JLabel("" + winningScore);
		JLabel message3 = new JLabel(" to ");
		JLabel message4 = new JLabel("" + losingScore);
		JLabel message5 = new JLabel(" is none other than...");
		JLabel message6 = new JLabel(name + "!!!!");

		message1.setFont(UPPER_PANEL_FONT);
		message1.setForeground(TWISTR_COLOR);
		message2.setFont(UPPER_PANEL_FONT);
		message2.setForeground(TWISTR_COLOR);
		message3.setFont(UPPER_PANEL_FONT);
		message3.setForeground(TWISTR_COLOR);
		message4.setFont(UPPER_PANEL_FONT);
		message4.setForeground(TWISTR_COLOR);
		message5.setFont(UPPER_PANEL_FONT);
		message5.setForeground(TWISTR_COLOR);
		message6.setFont(UPPER_PANEL_FONT);
		message6.setForeground(TWISTR_COLOR);

		panel.setLayout(new FlowLayout());
		panel.add(message1);
		panel.add(message2);
		panel.add(message3);
		panel.add(message4);
		panel.add(message5);
		panel.add(message6);
		return panel;
	}
}
