package edu.stanford.hci.r3.pen.streaming;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.imageio.ImageIO;

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

	private interface IconListener extends ActionListener, MouseListener {

	}

	private static IconListener iconListener;

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

	private static IconListener getIconListener() {
		if (iconListener == null) {
			iconListener = new IconListener() {
				public void actionPerformed(ActionEvent ae) {
					trayIcon.displayMessage("Action Event", "An Action Event Has Been Performed",
							TrayIcon.MessageType.INFO);
				}

				public void mouseClicked(MouseEvent arg0) {
					System.out.println("Mouse Clicked");
				}

				public void mouseEntered(MouseEvent arg0) {
					System.out.println("Mouse Entered");
				}

				public void mouseExited(MouseEvent arg0) {
					System.out.println("Mouse Exited");
				}

				public void mousePressed(MouseEvent arg0) {
					System.out.println("Mouse Pressed");
				}

				public void mouseReleased(MouseEvent arg0) {
					System.out.println("Mouse Released");
				}
			};
		}
		return iconListener;
	}

	public static void main(String[] args) {
		if (!SystemTray.isSupported()) {
			System.err
					.println("The System Tray is not supported. Exiting the Pen Server Tray App.");
			return;
		}
		SystemTray systemTray = SystemTray.getSystemTray();
		Image image = ImageCache.loadBufferedImage(PenServerTrayApp.class
				.getResource("/icons/sun.png"));
		PopupMenu popup = new PopupMenu();
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(getExitListener());
		popup.add(exitItem);

		trayIcon = new TrayIcon(image, "Pen Server", popup);
		trayIcon.addActionListener(getIconListener());
		trayIcon.addMouseListener(getIconListener());
		
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		// start the pen server!
		PenServer.startJavaServer();
	}
}
