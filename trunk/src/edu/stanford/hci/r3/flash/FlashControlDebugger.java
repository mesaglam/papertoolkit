package edu.stanford.hci.r3.flash;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.components.SuperJTextField;

/**
 * <p>
 * A GUI for testing out the various Flash connectivity features. Currently, an SWF file cannot
 * connect to a socket on the local machine. However, it seems to work through Flash 9's debugging
 * environment.
 * 
 * TODO: We will have to change the implementation to Flash's XMLSocket(...).
 * 
 * We should also be able to send more interesting events over to the Flash UI. (PenEvents and
 * EventHandlers?)
 * </p>
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashControlDebugger {

	/**
	 * Aug 30, 2006
	 */
	public static void main(String[] args) {
		new FlashControlDebugger();
	}

	/**
	 * 
	 */
	private JFrame control;

	/**
	 * 
	 */
	private JPanel mainPanel;

	/**
	 * 
	 */
	private JButton okButton;

	/**
	 * 
	 */
	private ActionListener sendActionListener;

	/**
	 * 
	 */
	private FlashControlServer server;

	/**
	 * 
	 */
	private SuperJTextField textField;

	/**
	 * 
	 */
	public FlashControlDebugger() {
		PaperToolkit.initializeLookAndFeel();

		server = new FlashControlServer();

		control = new JFrame("Flash UI Controller");
		control.setSize(640, 80);
		control.add(getMainPanel());
		control.setVisible(true);
		control.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * @return
	 */
	private Component getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
		}
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(getTextField(), BorderLayout.CENTER);
		mainPanel.add(getOKButton(), BorderLayout.EAST);
		return mainPanel;
	}

	/**
	 * @return
	 */
	private Component getOKButton() {
		if (okButton == null) {
			okButton = new JButton("Send Command");
			okButton.addActionListener(getSendAction());
		}
		return okButton;
	}

	/**
	 * @return
	 */
	private ActionListener getSendAction() {
		if (sendActionListener == null) {
			sendActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					server.sendMessage(textField.getText());
				}
			};
		}
		return sendActionListener;
	}

	/**
	 * @return
	 */
	private Component getTextField() {
		if (textField == null) {
			textField = new SuperJTextField();
			textField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
			textField.addActionListener(getSendAction());
		}
		return textField;
	}
}
