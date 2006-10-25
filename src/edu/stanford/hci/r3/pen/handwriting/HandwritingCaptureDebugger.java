package edu.stanford.hci.r3.pen.handwriting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.ink.InkPanel;
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
public class HandwritingCaptureDebugger extends JFrame {

	public static void main(String[] args) {
		new HandwritingCaptureDebugger();
	}

	private CaptureApplication app;

	private JPanel buttonPanel;

	private JButton calibrateButton;

	/**
	 * JPanel that renders Ink.
	 */
	private InkPanel mainPanel;

	private JButton saveButton;

	private JPanel statusBarPanel;

	private JLabel statusMessageLabel;

	private JTextArea textOutput;

	private JButton recognizeButton;

	private JButton recognizeAndClearButton;

	public HandwritingCaptureDebugger() {
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
			buttonPanel.add(getRecognizeButton());
			buttonPanel.add(getRecognizeAndClearButton());
		}
		return buttonPanel;
	}

	private Component getRecognizeAndClearButton() {
		if (recognizeAndClearButton == null) {
			recognizeAndClearButton = new JButton("Recognize and Clear");
			recognizeAndClearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DebugUtils.println("Recognize and Clear!");
					getInkPanel().clear();
				}
			});
		}
		return recognizeAndClearButton;
	}

	private Component getRecognizeButton() {
		if (recognizeButton == null) {
			recognizeButton = new JButton("Recognize Ink");
			recognizeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DebugUtils.println("Recognize!");
				}
			});
		}
		return recognizeButton;
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
	InkPanel getInkPanel() {
		if (mainPanel == null) {
			mainPanel = new InkPanel();
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
			saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DebugUtils.println("Save");
				}
			});
		}
		return saveButton;
	}

	/**
	 * @return
	 */
	private JPanel getToolBarPanel() {
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
		getContentPane().add(getToolBarPanel(), BorderLayout.NORTH);
		getContentPane().add(getInkPanel(), BorderLayout.CENTER);
		getContentPane().add(getTextOutputPanel(), BorderLayout.SOUTH);
		pack();
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_NORTH));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private Component getTextOutputPanel() {
		if (textOutput == null) {
			textOutput = new JTextArea(1, 50);
			textOutput.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
			textOutput.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			textOutput.setText("No Text Yet");
			textOutput.setForeground(Color.WHITE);
			textOutput.setEditable(false);
		}
		return textOutput;
	}

	/**
	 * Create the Paper App and ask the toolkit to start it up.
	 */
	private void startApp() {
		app = new CaptureApplication(this);
		PaperToolkit p = new PaperToolkit();
		app.setToolkitReference(p);
		p.startApplication(app);
	}

}
