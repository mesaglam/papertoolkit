package papertoolkit.external.flash;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import papertoolkit.PaperToolkit;
import papertoolkit.external.ExternalCommunicationServer;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.components.SuperJTextField;

/**
 * <p>
 * A GUI for testing out the various Flash connectivity features. Currently, an SWF file cannot connect to a
 * socket on the local machine. However, it seems to work through Flash 9's debugging environment. This may be
 * an older issue that has since been cleared up with the FlashSecurityRegistration class.
 * 
 * TODO: We will have to change the implementation to Flash's XMLSocket(...).
 * 
 * We should also be able to send more interesting events over to the Flash UI. (PenEvents and EventHandlers?)
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

	private JFrame control;
	private JPanel mainPanel;
	private JButton okButton;

	/**
	 * Sends the message over the wire to your telnet or Flash GUI client.
	 */
	private ActionListener sendActionListener;

	/**
	 * The server that clients can connect to (localhost:8545)
	 */
	private ExternalCommunicationServer server;

	/**
	 * Contains the message to send.
	 */
	private SuperJTextField textField;

	/**
	 * Use this to manually control a Flash GUI that listens for text events. It's a barebones app that sends
	 * strings across the wire.
	 */
	public FlashControlDebugger() {
		PaperToolkit.initializeLookAndFeel();
		server = new ExternalCommunicationServer();

		control = new JFrame("Flash UI Controller");
		control.setSize(640, 80);
		control.setLocation(WindowUtils.getWindowOrigin(control, WindowUtils.DESKTOP_CENTER));
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
					server.sendLine(textField.getText());
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
