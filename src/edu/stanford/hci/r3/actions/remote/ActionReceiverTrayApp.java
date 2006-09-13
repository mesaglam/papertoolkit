package edu.stanford.hci.r3.actions.remote;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.pen.streaming.PenServerTrayApp;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.components.SuperJTextField;
import edu.stanford.hci.r3.util.graphics.ImageCache;

/**
 * <p>
 * Run the ActionReceiver as a tray application. It will wait and listen for actions to run. Provide
 * a list of trusted remote (or local) clients to listen to... and then browse URLs, run actions, as
 * they come in over the wire...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionReceiverTrayApp {

	/**
	 * 
	 */
	private static final String DESCRIPTION = "Action Receiver (right-click for options; double-click to turn ON/OFF)\n";

	/**
	 * 
	 */
	private static final String START_MSG = "Start the Action Receiver";

	/**
	 * 
	 */
	private static final String STATUS_OFF = "The Action Receiver is off.";

	/**
	 * 
	 */
	private static final String STATUS_ON = "The Action Receiver is now running.";

	/**
	 * 
	 */
	private static final String STOP_MSG = "Stop the Action Receiver";

	/**
	 * Run a tray app that allows you to connect to an action server. This app will invoke actions
	 * that come over the wire.
	 */
	public static void main(String[] args) {
		new ActionReceiverTrayApp();
	}

	/**
	 * 
	 */
	private ActionReceiver actionReceiver;

	/**
	 * 
	 */
	private String currentStatus;

	/**
	 * 
	 */
	private ActionListener iconListener;

	/**
	 * 
	 */
	private Image imageOFF;

	/**
	 * 
	 */
	private Image imageON;

	/**
	 * 
	 */
	private MenuItem onOffItem;

	/**
	 * It connects/reconnects when you press enter in the text field.
	 */
	private boolean receiverRunning = false;

	/**
	 * 
	 */
	private TrayIcon trayIcon;

	/**
	 * 
	 */
	private JFrame trustedClientsFrame;

	/**
	 * Comma delimited list of trusted clients.
	 */
	private SuperJTextField trustedClientsTextField;

	public ActionReceiverTrayApp() {
		// look like windows, mac, or whatever...
		PaperToolkit.initializeLookAndFeel();

		// check that the system tray is supported (Java 6)
		if (!SystemTray.isSupported()) {
			System.err.println("The System Tray is not supported. "
					+ "Exiting the ActionReceiverTrayApp.");
			return;
		}

		// the on and off icons
		imageON = ImageCache.loadBufferedImage(ActionReceiverTrayApp.class
				.getResource("/icons/planet.png"));
		imageOFF = ImageCache.loadBufferedImage(PenServerTrayApp.class
				.getResource("/icons/planetOff.png"));

		// the action client is OFF by default
		currentStatus = STATUS_OFF;

		// this is the icon that sits in our tray...
		trayIcon = new TrayIcon(imageOFF, DESCRIPTION + currentStatus, getPopupMenu());
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(getIconDoubleClickListener());

		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}

		// the UI to request for the server IP addr
		trustedClientsFrame = new JFrame("Action Receiver Application");
		trustedClientsFrame.setContentPane(getMainPanel());
		trustedClientsFrame.pack();
		trustedClientsFrame.setLocation(WindowUtils.getWindowOrigin(trustedClientsFrame,
				WindowUtils.DESKTOP_CENTER));
		trustedClientsFrame.setVisible(true);
	}

	/**
	 * @return
	 */
	private ActionListener getExitListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Exiting the Action Receiver Tray App...");
				SystemTray.getSystemTray().remove(trayIcon);
				System.exit(0);
			}
		};
	}

	/**
	 * @return the action to run when we double click the tray icon.
	 */
	private ActionListener getIconDoubleClickListener() {
		if (iconListener == null) {
			iconListener = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (receiverRunning) {
						trayIcon.displayMessage("Action Receiver is Offline",
								"Not listening for new actions.", TrayIcon.MessageType.INFO);
						actionReceiver.stopDaemon();
						currentStatus = STATUS_OFF;
						trayIcon.setImage(imageOFF);
						onOffItem.setLabel(START_MSG);
						receiverRunning = false;
					} else {
						String trustedClientsList = trustedClientsTextField.getText();
						String[] clientNames = trustedClientsList.split(",");
						for (int i = 0; i < clientNames.length; i++) {
							clientNames[i] = clientNames[i].trim();
						}

						actionReceiver = new ActionReceiver(ActionReceiver.DEFAULT_JAVA_PORT,
								ClientServerType.JAVA, clientNames);

						actionReceiver
								.setConnectionListener(new ActionReceiverConnectionListener() {
									public void newConnectionFrom(String hostName, String ipAddr) {
										trayIcon.displayMessage("New Connection", hostName + ipAddr
												+ " has connected.", TrayIcon.MessageType.INFO);
									}
								});
						actionReceiver.addActionHandler(new ActionHandler()); // invokes the
						// actions

						trayIcon.displayMessage("Action Receiver is Online",
								"Waiting for commands.", TrayIcon.MessageType.INFO);
						currentStatus = STATUS_ON;
						trayIcon.setImage(imageON);
						onOffItem.setLabel(STOP_MSG);
						receiverRunning = true;
					}
					trayIcon.setToolTip(DESCRIPTION + currentStatus);
				}

			};
		}
		return iconListener;
	}

	/**
	 * @return
	 */
	private JPanel getMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout());

		// the text field for the IP Addr
		trustedClientsTextField = new SuperJTextField("localhost", 30);
		trustedClientsTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				trustedClientsFrame.setVisible(false);
			}
		});
		trustedClientsTextField.setBorder(BorderFactory.createCompoundBorder(
				trustedClientsTextField.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// a message to tell the user what to do
		JLabel message = new JLabel(
				"<html>Enter a comma-separated list of trusted machines. You may use whole or partial <b>DNS names</b> or <b>IP Addresses</b>.<br/>"
						+ "\tExample: <b>localhost, .stanford.edu, 192.168, 128.123.*.*</b><br/><br/>"
						+ "Press Enter to save the value. Then, double click the Action Receiver (Saturn icon) in your System Tray to connect.</html>");
		message.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		message.setFont(new Font("Tahoma", Font.PLAIN, 16));

		JPanel controls = new JPanel();
		controls.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		controls.setLayout(new BorderLayout());
		JButton hideButton = new JButton("Minimize this Window to the System Tray");
		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				trustedClientsFrame.setVisible(false);
			}
		});
		JButton exitButton = new JButton("Exit the Action Receiver");
		exitButton.addActionListener(getExitListener());
		controls.add(hideButton, BorderLayout.CENTER);
		controls.add(exitButton, BorderLayout.EAST);

		// add the components
		mainPanel.add(trustedClientsTextField, BorderLayout.CENTER);
		mainPanel.add(message, BorderLayout.NORTH);
		mainPanel.add(controls, BorderLayout.SOUTH);
		return mainPanel;
	}

	/**
	 * @return
	 */
	private PopupMenu getPopupMenu() {
		PopupMenu popup = new PopupMenu();

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(getExitListener());

		onOffItem = new MenuItem(START_MSG);
		onOffItem.addActionListener(getIconDoubleClickListener());

		MenuItem setTrustedClients = new MenuItem("Set Trusted Clients");
		setTrustedClients.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				trustedClientsFrame.setVisible(true);
			}
		});

		popup.add(setTrustedClients);
		popup.add(onOffItem);
		popup.add(new MenuItem("-"));
		popup.add(exitItem);

		return popup;
	}
}
