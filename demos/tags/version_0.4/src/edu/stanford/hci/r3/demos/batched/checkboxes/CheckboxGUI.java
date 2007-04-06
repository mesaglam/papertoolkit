package edu.stanford.hci.r3.demos.batched.checkboxes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jgoodies.looks.Options;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.WindowUtils;

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
public class CheckboxGUI extends JFrame {

	private static final String CLICK_ON_ADD_MSG = "Click on Add to create a new Checkbox";

	static {
		PaperToolkit.initializeLookAndFeel();
	}

	private JButton addCheckBoxButton;

	private JPanel checkboxView;

	private JLabel designDirectionsLabel;

	private JPanel designPanel;

	private JLabel statusText;

	private JTabbedPane tabbedPane;

	private JPanel testPanel;

	private JPanel topPanel;

	private Checkboxes model;

	private static final Font LABEL_FONT = new Font("Trebuchet MS", Font.PLAIN, 14);

	public CheckboxGUI(Checkboxes checkboxes) {
		model = checkboxes;
		setupComponents();
		setTitle("Paper Checkboxes");
		setVisible(true);
		setSize(640, 480);
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private Component getAddCheckboxButton() {
		if (addCheckBoxButton == null) {
			addCheckBoxButton = new JButton("Add New Checkbox");
			addCheckBoxButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					checkboxView.add(createNewCheckBoxPanel());
					checkboxView.revalidate();
				}
			});
		}
		return addCheckBoxButton;
	}

	/**
	 * @return
	 */
	private Component getCheckboxView() {
		if (checkboxView == null) {
			checkboxView = new JPanel();
			checkboxView.setLayout(new FlowLayout());
		}
		return checkboxView;
	}

	private Component createNewCheckBoxPanel() {
		JPanel cbPanel = new JPanel();
		final JCheckBox cb = new JCheckBox("Unnamed Checkbox");
		cb.setEnabled(false);
		cbPanel.add(cb);
		return cbPanel;
	}

	private Component getDesignDirectionsLabel() {
		if (designDirectionsLabel == null) {
			designDirectionsLabel = new JLabel(
					"<html>Put your pen is in STREAMING mode. Click on "
							+ "'Add Checkbox'. Draw a Box on paper. Name this Checkbox. "
							+ "Add as many checkboxes as you would like, and then test them by going to the Test tab.</html>");
			designDirectionsLabel.setFont(LABEL_FONT);
		}
		return designDirectionsLabel;
	}

	private Component getDesignPanel() {
		if (designPanel == null) {
			designPanel = new JPanel();
			designPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
			designPanel.setLayout(new BorderLayout());
			designPanel.add(getTopPanel(), BorderLayout.NORTH);
			designPanel.add(getCheckboxView(), BorderLayout.CENTER);
			designPanel.add(getStatusText(), BorderLayout.SOUTH);
		}
		return designPanel;
	}

	private Component getStatusText() {
		if (statusText == null) {
			statusText = new JLabel();
			statusText.setFont(LABEL_FONT);
			statusText.setBackground(Color.DARK_GRAY);
			statusText.setOpaque(true);
			setStatusText(CLICK_ON_ADD_MSG);
		}
		return statusText;
	}

	private Component getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, true);
			tabbedPane.addTab("Design", getDesignPanel());
			tabbedPane.addTab("Test", getTestPanel());
		}
		return tabbedPane;
	}

	private Component getTestPanel() {
		if (testPanel == null) {
			testPanel = new JPanel();
			testPanel.setLayout(new BorderLayout());
		}
		return testPanel;
	}

	private Component getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			topPanel.add(getDesignDirectionsLabel(), BorderLayout.NORTH);
			topPanel.add(getAddCheckboxButton(), BorderLayout.SOUTH);
		}

		return topPanel;
	}

	private void setStatusText(String text) {
		statusText.setText("<html>" + text + "</html>");
	}

	private void setupComponents() {
		getContentPane().setLayout(new BorderLayout());
		add(getTabbedPane(), BorderLayout.CENTER);
	}
}
