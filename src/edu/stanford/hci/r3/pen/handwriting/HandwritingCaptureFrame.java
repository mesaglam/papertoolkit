package edu.stanford.hci.r3.pen.handwriting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * This assumes capture on a single patterned page (it does not utilize the PaperToolkit's Tiling).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class HandwritingCaptureFrame extends JFrame {
	public static void main(String[] args) {
		new HandwritingCaptureFrame();
	}

	private JPanel buttonPanel;

	private JButton calibrateButton;

	private JPanel mainPanel;

	private JButton saveButton;

	private JPanel statusBarPanel;

	private JLabel statusMessageLabel;

	private HandwritingCaptureApp app;

	public HandwritingCaptureFrame() {
		PaperToolkit.initializeLookAndFeel();
		initGUI();
		startApp();
	}

	private void startApp() {
		app = new HandwritingCaptureApp();
		PaperToolkit.runApplication(app);
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getCalibrateButton());
			buttonPanel.add(getSaveButton());
		}
		return buttonPanel;
	}

	private JButton getCalibrateButton() {
		if (calibrateButton == null) {
			calibrateButton = new JButton();
			calibrateButton.setText("Calibrate");
		}
		return calibrateButton;
	}

	private Component getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(800, 600));
			mainPanel.setBackground(Color.WHITE);
		}
		return mainPanel;
	}

	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText("Save");
		}
		return saveButton;
	}

	private JPanel getStatusBarPanel() {
		if (statusBarPanel == null) {
			statusBarPanel = new JPanel();
			BorderLayout statusBarPanelLayout = new BorderLayout();
			statusBarPanel.setLayout(statusBarPanelLayout);
			statusBarPanel.add(getStatusMessageLabel(), BorderLayout.CENTER);
			statusBarPanel.add(getButtonPanel(), BorderLayout.EAST);
		}
		return statusBarPanel;
	}

	private JLabel getStatusMessageLabel() {
		if (statusMessageLabel == null) {
			statusMessageLabel = new JLabel();
			statusMessageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			statusMessageLabel.setText("After clicking Calibrate, tap once on the upper left "
					+ "corner of your page, and then once on the lower right corner of your page.");
		}
		return statusMessageLabel;
	}

	private void initGUI() {
		setTitle("Handwriting Recognition Debugger");
		getContentPane().add(getStatusBarPanel(), BorderLayout.SOUTH);
		getContentPane().add(getMainPanel(), BorderLayout.CENTER);
		pack();
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
