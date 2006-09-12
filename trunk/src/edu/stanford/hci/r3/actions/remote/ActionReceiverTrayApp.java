package edu.stanford.hci.r3.actions.remote;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
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
 * Run this as a tray application. It will wait and listen for actions to run. Provide a remote (or
 * local) server to listen to... and then browse URLs, run actions, as they come in over the wire...
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
	private static ActionClient client;

	/**
	 * It connects/reconnects when you press enter in the text field.
	 */
	private static boolean clientRunning = false;

	/**
	 * 
	 */
	private static String currentStatus;

	/**
	 * 
	 */
	private static final String DESCRIPTION = "Action Client (right-click for options; double-click to turn ON/OFF)\n";

	/**
	 * 
	 */
	private static ActionListener iconListener;

	/**
	 * 
	 */
	private static Image imageOFF;

	/**
	 * 
	 */
	private static Image imageON;

	/**
	 * 
	 */
	private static MenuItem onOffItem;

	/**
	 * 
	 */
	private static JFrame serverIPAddrFrame;

	private static SuperJTextField serverNameTextField;

	/**
	 * 
	 */
	private static final String START_MSG = "Start the Action Client";

	/**
	 * 
	 */
	private static final String STATUS_OFF = "The Action Client is off.";

	/**
	 * 
	 */
	private static final String STATUS_ON = "The Action Client is now running.";

	/**
	 * 
	 */
	private static final String STOP_MSG = "Stop the Action Client";

	/**
	 * 
	 */
	private static TrayIcon trayIcon;

	/**
	 * @return whether we connected successfully.
	 */
	private static boolean connectToServer() {
		// The server to connect to. Defaults to the local host.
		String serverNameOrIPAddr = serverNameTextField.getText();

		System.out.println("Creating a ClientActionHandler that connects to " + serverNameOrIPAddr);
		client = new ActionClient(serverNameOrIPAddr, ActionServer.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);
		client.connect();

		if (!client.isRunning()) {
			return false; // did not work out
		}

		client.addActionHandler(new ActionHandler());
		return true;
	}

	/**
	 * 
	 */
	private static void disconnectFromServer() {
		if (client != null) {
			client.disconnect();
			client = null;
		}
	}

	/**
	 * @return
	 */
	private static ActionListener getExitListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Exiting the Action Client Tray App...");
				System.exit(0);
			}
		};
	}

	/**
	 * @return the action to run when we double click the tray icon.
	 */
	private static ActionListener getIconDoubleClickListener() {
		if (iconListener == null) {
			iconListener = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (clientRunning) {
						trayIcon.displayMessage("Action Client is Offline",
								"Not listening for new actions.", TrayIcon.MessageType.INFO);

						disconnectFromServer();
						currentStatus = STATUS_OFF;
						trayIcon.setImage(imageOFF);
						onOffItem.setLabel(START_MSG);
						clientRunning = false;
					} else {

						boolean connected = connectToServer();
						if (!connected) {
							// do not change any of the state
							trayIcon.displayMessage("Connection to "
									+ serverNameTextField.getText() + " Failed.",
									"Make sure the server is running.", TrayIcon.MessageType.ERROR);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							trayIcon.displayMessage("Action Client is Offline",
									"Not listening for new actions.", TrayIcon.MessageType.INFO);
							return;
						}

						trayIcon.displayMessage("Action Client is Online",
								"Your wish is my command.", TrayIcon.MessageType.INFO);
						currentStatus = STATUS_ON;
						trayIcon.setImage(imageON);
						onOffItem.setLabel(STOP_MSG);
						clientRunning = true;
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
	private static JPanel getMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout());

		// the text field for the IP Addr
		serverNameTextField = new SuperJTextField("localhost", 30);
		serverNameTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				serverIPAddrFrame.setVisible(false);
			}
		});
		serverNameTextField.setBorder(BorderFactory.createCompoundBorder(serverNameTextField
				.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// a message to tell the user what to do
		JLabel message = new JLabel(
				"<html>Enter the Action Server's <b>DNS name</b> or <b>IP Address</b> here. <br/>"
						+ "Press Enter to save the value. Then, double click the Action Client (Saturn icon)<br/>"
						+ " in your System Tray to connect.</html>");
		message.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		message.setFont(new Font("Tahoma", Font.PLAIN, 16));

		mainPanel.add(serverNameTextField, BorderLayout.CENTER);
		mainPanel.add(message, BorderLayout.NORTH);
		return mainPanel;
	}

	/**
	 * @return
	 */
	private static PopupMenu getPopupMenu() {
		PopupMenu popup = new PopupMenu();

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(getExitListener());

		onOffItem = new MenuItem(START_MSG);
		onOffItem.addActionListener(getIconDoubleClickListener());

		MenuItem setServerIPAddr = new MenuItem("Set the Server's IP Address");
		setServerIPAddr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				serverIPAddrFrame.setVisible(true);
			}
		});

		popup.add(setServerIPAddr);
		popup.add(onOffItem);
		popup.add(exitItem);

		return popup;
	}

	/**
	 * Run a tray app that allows you to connect to an action server. This app will invoke actions
	 * that come over the wire.
	 */
	public static void main(String[] args) {
		// look like windows, mac, or whatever...
		PaperToolkit.initializeLookAndFeel();

		// check that the system tray is supported (Java 6)
		if (!SystemTray.isSupported()) {
			System.err.println("The System Tray is not supported. "
					+ "Exiting the ActionClientTrayApp.");
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
		serverIPAddrFrame = new JFrame("Action Handler Client Application");
		serverIPAddrFrame.setContentPane(getMainPanel());
		serverIPAddrFrame.pack();
		serverIPAddrFrame.setLocation(WindowUtils.getWindowOrigin(serverIPAddrFrame,
				WindowUtils.DESKTOP_CENTER));
		serverIPAddrFrame.setVisible(true);
	}
}
