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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.networking.ClientServerType;
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
 * WARNING: If this app crashes, it MAY break msvcr71.dll, which means that it won't be able to run
 * again until you reboot. Sorry... Maybe it's a Java 6 Bug? NOTE: One way to get it running again
 * is to run the Test_ActionSenderAndReceiver. Weeeiiird. Perhaps it is because in the latter case,
 * the main() isn't bootstrapped by the Eclipse IDE?
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionReceiverTrayApp {

	private static final String DESCRIPTION = "Action Receiver (right-click for options; double-click to turn ON/OFF)\n";

	private static final String DIRECTIONS_FOR_SETTING_LIST_OF_TRUSTED_CLIENTS = "<html>Enter a comma-separated list of trusted machines. "
			+ "You may use whole or partial <b>DNS names</b> or <b>IP Addresses</b>.<br/>"
			+ "\tExample: <b>localhost, .stanford.edu, 192.168, 128.123.*.*</b><br/><br/>"
			+ "Press Enter to save the value. Then, double click the Action Receiver "
			+ "(Saturn icon) in your System Tray to connect.</html>";

	private static final String START_MSG = "Start the Action Receiver";

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
	 * The server that listens for and invokes incoming actions.
	 */
	private ActionReceiver actionReceiver;

	/**
	 * 
	 */
	private String currentStatus;

	/**
	 * Turns the server on or off. Activated by double-clicking the tray icon.
	 */
	private ActionListener iconListener;

	/**
	 * Icon for the disabled server.
	 */
	private Image imageOFF;

	/**
	 * Tray icon for the running server.
	 */
	private Image imageON;

	/**
	 * Toggle the server
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

	/**
	 * Initializes an app that sits in the tray.
	 */
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
		imageOFF = ImageCache.loadBufferedImage(ActionReceiverTrayApp.class
				.getResource("/icons/planetOff.png"));

		// the action client is OFF by default
		currentStatus = STATUS_OFF;

		// this is the icon that sits in our tray...
		trayIcon = new TrayIcon(imageOFF, DESCRIPTION + currentStatus, getPopupMenu());
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(getToggleServerStateListener());

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
	 * @return action that exits the tray app.
	 */
	private ActionListener getExitListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Exiting the Action Receiver Tray App...");
				SystemTray.getSystemTray().remove(trayIcon);
				System.exit(0);
			}
		};
	}

	/**
	 * @return the panel for the GUI that pops up when we first run this tray app. This GUI allows
	 *         you to set the list of trusted clients. You can minimize (or close) the GUI without
	 *         exiting the app. It remains in your system tray as a Saturn icon.
	 */
	private JPanel getMainPanel() {
		final JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout());

		// the text field for the IP Addr
		trustedClientsTextField = new SuperJTextField("localhost", 30);
		trustedClientsTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				trustedClientsFrame.setVisible(false);
			}
		});
		trustedClientsTextField.setBorder(BorderFactory.createCompoundBorder(trustedClientsTextField
				.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// a message to tell the user what to do
		final JLabel message = new JLabel(DIRECTIONS_FOR_SETTING_LIST_OF_TRUSTED_CLIENTS);
		message.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		message.setFont(new Font("Tahoma", Font.PLAIN, 16));

		final JPanel controls = new JPanel();
		controls.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		controls.setLayout(new BorderLayout());
		JButton hideButton = new JButton("Minimize this Window to the System Tray");
		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				trustedClientsFrame.setVisible(false);
			}
		});
		final JButton exitButton = new JButton("Exit the Action Receiver");
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
	 * @return the menu when you right-click the system tray icon.
	 */
	private PopupMenu getPopupMenu() {
		final PopupMenu popup = new PopupMenu();

		final MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(getExitListener());

		onOffItem = new MenuItem(START_MSG);
		onOffItem.addActionListener(getToggleServerStateListener());

		// modify the list of trusted clients
		final MenuItem setTrustedClients = new MenuItem("Set Trusted Clients");
		setTrustedClients.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				trustedClientsFrame.setVisible(true);
			}
		});

		popup.add(setTrustedClients);
		popup.add(onOffItem);
		popup.add(new MenuItem("-")); // separator
		popup.add(exitItem);

		return popup;
	}

	/**
	 * @return the action to run when we double click the tray icon or select the menu item to turn
	 *         the server on/off.
	 */
	private ActionListener getToggleServerStateListener() {
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

						actionReceiver.setConnectionListener(new ActionReceiverConnectionListener() {
							public void newConnectionFrom(String hostName, String ipAddr) {
								trayIcon.displayMessage("New Connection", hostName + ipAddr
										+ " has connected.", TrayIcon.MessageType.INFO);
							}
						});

						// invokes the actions
						actionReceiver.addActionHandler(new ActionHandler());

						final String hostAddress = actionReceiver.getHostAddress();
						final String hostName = actionReceiver.getHostName();

						// show a balloon in the windows tray.
						trayIcon.displayMessage("Action Receiver is Online",
								"Waiting for commands. This receiver's name/address is: " + hostName + "/"
										+ hostAddress, TrayIcon.MessageType.INFO);
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
}
