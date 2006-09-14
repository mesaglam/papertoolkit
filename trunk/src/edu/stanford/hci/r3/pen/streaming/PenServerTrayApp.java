package edu.stanford.hci.r3.pen.streaming;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.stanford.hci.r3.util.graphics.ImageCache;

/**
 * <p>
 * A little application that you can deploy to other machines that will host physical pens. It will
 * run with an icon in the system tray.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenServerTrayApp {

	private static ActionListener iconListener;

	private static Image imageOFF;

	private static Image imageON;

	private static MenuItem onOffItem;

	private static boolean serverRunning;

	private static final String START_PEN_SERVER_MSG = "Start the Pen Server";

	private static final String STOP_PEN_SERVER_MSG = "Stop the Pen Server";

	private static TrayIcon trayIcon;

	/**
	 * @return
	 */
	private static ActionListener getExitListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Exiting the Pen Server Tray App...");
				System.exit(0);
			}
		};
	}

	/**
	 * @return
	 */
	private static ActionListener getOnOffListener() {
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
								"Server started. The pen is now in live mode.",
								TrayIcon.MessageType.INFO);
						PenServer.startJavaServer();
						trayIcon.setImage(imageON);
						onOffItem.setLabel(STOP_PEN_SERVER_MSG);
						serverRunning = true;
					}
				}
			};
		}
		return iconListener;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (!SystemTray.isSupported()) {
			System.err
					.println("The System Tray is not supported. Exiting the Pen Server Tray App.");
			return;
		}
		SystemTray systemTray = SystemTray.getSystemTray();
		imageON = ImageCache
				.loadBufferedImage(PenServerTrayApp.class.getResource("/icons/sun.png"));
		imageOFF = ImageCache.loadBufferedImage(PenServerTrayApp.class
				.getResource("/icons/sunOff.png"));

		PopupMenu popup = new PopupMenu();
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(getExitListener());
		onOffItem = new MenuItem(STOP_PEN_SERVER_MSG);
		onOffItem.addActionListener(getOnOffListener());
		popup.add(exitItem);
		popup.add(onOffItem);

		trayIcon = new TrayIcon(imageON, "Pen Server (double-click to turn ON/OFF)", popup);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(getOnOffListener());

		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}

		// start the pen server!
		PenServer.startJavaServer();
		serverRunning = true;
	}
}
