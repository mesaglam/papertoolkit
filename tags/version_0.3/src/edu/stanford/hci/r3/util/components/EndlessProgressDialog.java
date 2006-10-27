package edu.stanford.hci.r3.util.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * For telling the user something is happening.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EndlessProgressDialog extends JDialog {

	private String text;

	/**
	 * @param parentFrame
	 * @param title
	 * @param message
	 */
	public EndlessProgressDialog(JFrame parentFrame, String title, String message) {
		super(parentFrame, title, false /* not modal */);
		text = message;
		setContentPane(getMainPanel());
		pack();
		setLocation(WindowUtils
				.getWindowOrigin(getWidth(), getHeight(), WindowUtils.DESKTOP_CENTER));
		setVisible(true);
	}

	/**
	 * @return
	 */
	private JLabel getLabel() {
		final JLabel label = new JLabel(text);
		label.setFont(new Font("Tahoma", Font.PLAIN, 16));
		return label;
	}

	/**
	 * @return
	 */
	private Container getMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(getLabel(), BorderLayout.NORTH);
		mainPanel.add(getProgressBar(), BorderLayout.SOUTH);
		return mainPanel;
	}

	/**
	 * @return
	 */
	private Container getProgressBar() {
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		return bar;
	}
}
