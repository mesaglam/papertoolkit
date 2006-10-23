package edu.stanford.hci.r3.pen.handwriting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;
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

	private HandwritingCaptureApp app;

	private JPanel buttonPanel;

	private JButton calibrateButton;

	private JPanel mainPanel;

	private JButton saveButton;

	private JPanel statusBarPanel;

	private JLabel statusMessageLabel;

	public HandwritingCaptureFrame() {
		PaperToolkit.initializeLookAndFeel();
		initGUI();
		startApp();
	}

	/**
	 * @return
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getCalibrateButton());
			buttonPanel.add(getSaveButton());
		}
		return buttonPanel;
	}

	/**
	 * @return
	 */
	private JButton getCalibrateButton() {
		if (calibrateButton == null) {
			calibrateButton = new JButton();
			calibrateButton.setText("Calibrate");
			calibrateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					DebugUtils.println("Calibrate: Choose Top Left and Bottom Right Corners...");
					app.addCalibrationHandler();
				}
			});
		}
		return calibrateButton;
	}

	/**
	 * @return
	 */
	private Component getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(800, 600));
			mainPanel.setBackground(Color.WHITE);
		}
		return mainPanel;
	}

	/**
	 * @return
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText("Save");
		}
		return saveButton;
	}

	/**
	 * @return
	 */
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

	/**
	 * @return
	 */
	private JLabel getStatusMessageLabel() {
		if (statusMessageLabel == null) {
			statusMessageLabel = new JLabel();
			statusMessageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			statusMessageLabel.setText("After clicking Calibrate, tap once on the upper left "
					+ "corner of your page, and then once on the lower right corner of your page.");
		}
		return statusMessageLabel;
	}

	/**
	 * 
	 */
	private void initGUI() {
		setTitle("Handwriting Recognition Debugger");
		getContentPane().add(getStatusBarPanel(), BorderLayout.SOUTH);
		getContentPane().add(getMainPanel(), BorderLayout.CENTER);
		pack();
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_NORTH));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * 
	 */
	private void startApp() {
		app = new HandwritingCaptureApp();
		PaperToolkit p = new PaperToolkit();
		app.setToolkitReference(p);
		p.startApplication(app);
	}

}
