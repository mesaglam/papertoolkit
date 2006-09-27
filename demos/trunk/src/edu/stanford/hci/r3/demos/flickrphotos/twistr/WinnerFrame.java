package edu.stanford.hci.r3.demos.flickrphotos.twistr;

import java.awt.*;

import javax.swing.*;

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
public class WinnerFrame extends JFrame {
	private int winningScore;

	private int losingScore;

	private String name;

	public WinnerFrame(int topScore, int loserScore, String winnerName) {
		winningScore = topScore;
		losingScore = loserScore;
		name = winnerName;

		setContentPane(getWinnerPanel());

		setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * 
	 */
	public WinnerFrame(int tieScore) {
		winningScore = tieScore;
		losingScore = tieScore;
		
		setContentPane(getTiePanel());

		setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * @return
	 */
	private Container getTiePanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		JLabel message1 = new JLabel("OH NOOOOO......");
		JLabel message2 = new JLabel("It was a TIE GAME!!!!  " +);
		panel.setLayout(new FlowLayout());
		panel.add(message1);
		panel.add(message2);
		return panel;
	}

	/**
	 * @return
	 */
	private Container getWinnerPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		JLabel message1 = new JLabel("The winner, by a score of ");
		JLabel message2 = new JLabel("" + winningScore);
		JLabel message3 = new JLabel(" to ");
		JLabel message4 = new JLabel("" + losingScore);
		JLabel message5 = new JLabel(" is none other than...");
		JLabel message6 = new JLabel(name + "!!!!");
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
