package papertoolkit.pen.streaming;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import papertoolkit.PaperToolkit;
import papertoolkit.application.config.Constants;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.communications.COMPort;
import papertoolkit.util.graphics.ImageCache;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * <p>
 * A little application that you can deploy to other machines that will host physical pens. It will run with
 * an icon in the system tray.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenServerTrayApp {

	/**
	 * Label for the Menu...
	 */
	private static final String START_PEN_SERVER_MSG = "Start the Pen Server";

	/**
	 * Label for the Menu...
	 */
	private static final String STOP_PEN_SERVER_MSG = "Stop the Pen Server";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PaperToolkit.initializeLookAndFeel();

		// pop up a dialog box asking which serial port and which TCP/IP port to use...
		final JDialog dialog = new JDialog();

		FormLayout layout = new FormLayout( //
				"right:pref, 3dlu, pref", // columns
				"p, 3dlu, p, 9dlu, p" // rows
		);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		final JComboBox comPortComboBox = new JComboBox(COMPort.PORTS);
		comPortComboBox.setSelectedItem(COMPort.COM5);
		final JTextField serialPortTextField = new JTextField(15);
		serialPortTextField.setText(Constants.Ports.PEN_SERVER_JAVA + "");
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				COMPort selectedCOMPort = (COMPort) comPortComboBox.getSelectedItem();
				int selectedTCPIPPort = Integer.parseInt(serialPortTextField.getText());
				DebugUtils.println(selectedCOMPort + " " + selectedTCPIPPort);
				new PenServerTrayApp(selectedCOMPort, selectedTCPIPPort);
				dialog.dispose();
			}
		});

		builder.addLabel("Bluetooth COM Port (e.g., COM5)", cc.xy(1, 1));
		builder.add(comPortComboBox, cc.xyw(3, 1, 1));

		builder.addLabel("Pen Server Serial Port (e.g., 11025)", cc.xy(1, 3));
		builder.add(serialPortTextField, cc.xyw(3, 3, 1));

		builder.add(button, cc.xyw(1, 5, 3));

		dialog.getContentPane().add(builder.getPanel());
		dialog.setTitle("Pen Server Options");
		dialog.pack();
		dialog.setLocation(WindowUtils.getWindowOrigin(dialog.getWidth(), dialog.getHeight(),
				WindowUtils.DESKTOP_CENTER));
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * 
	 */
	private COMPort comPort;

	/**
	 * 
	 */
	private ActionListener iconListener;

	/**
	 * Pen Server is stopped.
	 */
	private Image imageOFF;

	/**
	 * Tells us the pen server is running (default).
	 */
	private Image imageON;

	/**
	 * 
	 */
	private MenuItem onOffItem;

	/**
	 * 
	 */
	private boolean serverRunning;

	/**
	 * 
	 */
	private int tcpipPort;

	/**
	 * 
	 */
	private TrayIcon trayIcon;

	public PenServerTrayApp(COMPort btDongleComPortT, int penServerTcpIpPorT) {
		if (!SystemTray.isSupported()) {
			System.err.println("The System Tray is not supported. " + "Exiting the Pen Server Tray App.");
			return;
		}
		final SystemTray systemTray = SystemTray.getSystemTray();

		comPort = btDongleComPortT;
		tcpipPort = penServerTcpIpPorT;

		// the on/off icons
		imageON = ImageCache.loadBufferedImage(PenServerTrayApp.class.getResource("/icons/sun.png"));
		imageOFF = ImageCache.loadBufferedImage(PenServerTrayApp.class.getResource("/icons/sunOff.png"));

		final PopupMenu popup = new PopupMenu();
		final MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(getExitListener());
		onOffItem = new MenuItem(STOP_PEN_SERVER_MSG);
		onOffItem.addActionListener(getOnOffListener());
		popup.add(onOffItem);
		popup.add(exitItem);

		InetAddress localHost = null;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		final String tooltip = "Pen Server at " + localHost + "\n[double-click to turn ON/OFF]";
		// the icon in the system tray
		trayIcon = new TrayIcon(imageON, tooltip, popup);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(getOnOffListener());

		try {
			systemTray.add(trayIcon);
		} catch (final AWTException e) {
			e.printStackTrace();
		}

		// start the pen server!
		PenServer.startJavaServer(comPort, tcpipPort);
		serverRunning = true;
	}

	/**
	 * @return the menu item's listener for closing the tray app...
	 */
	private ActionListener getExitListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exiting the Pen Server Tray App...");
				System.exit(0);
			}
		};
	}

	/**
	 * @return The listener for toggling on/off the pen server.
	 */
	private ActionListener getOnOffListener() {
		if (iconListener == null) {
			iconListener = new ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					if (serverRunning) {
						trayIcon.displayMessage("Pen is Offline", "Pen Server stopped.",
								TrayIcon.MessageType.INFO);
						PenServer.stopServers();
						trayIcon.setImage(imageOFF);
						onOffItem.setLabel(START_PEN_SERVER_MSG);
						serverRunning = false;
					} else {
						trayIcon.displayMessage("Pen is Online",
								"Server started. The pen is now in live mode.", TrayIcon.MessageType.INFO);
						PenServer.startJavaServer(comPort, tcpipPort);
						trayIcon.setImage(imageON);
						onOffItem.setLabel(STOP_PEN_SERVER_MSG);
						serverRunning = true;
					}
				}
			};
		}
		return iconListener;
	}
}
