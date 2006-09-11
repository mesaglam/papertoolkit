package edu.stanford.hci.r3.actions.remote;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.util.components.SuperJTextField;

/**
 * <p>
 * Provide a Server to listen to... and then browse URLs as they come in over the wire...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionClientTrayApp {

	/**
	 * Apr 1, 2006
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Action Handler Client Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());

		final JTextField serverNameTextField = new SuperJTextField("localhost", 30);
		JButton connectButton = new JButton("Connect to Server");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String server = serverNameTextField.getText();
				System.out.println("Creating a ClientActionHandler that connects to " + server);
				new ActionClientTrayApp(server);
			}
		});

		mainPanel.add(serverNameTextField);
		mainPanel.add(connectButton);
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * 
	 */
	private ActionClient client;

	/**
	 * @param hostNameOrIPAddr
	 */
	public ActionClientTrayApp(String hostNameOrIPAddr) {
		client = new ActionClient(hostNameOrIPAddr, ActionServer.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);
		client.connect();
		client.addActionHandler(new ActionHandler());
	}
}
