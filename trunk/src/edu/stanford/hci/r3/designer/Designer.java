package edu.stanford.hci.r3.designer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * Basically, a JFrame with a scrollable document panel that can render Sheets and Regions. This
 * class is incomplete, but it was inspired by the original GIGAprints designer. It should provide
 * debugging and design options for the designer/developer.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Designer {

	private static final String TITLE = "R3 Paper UI Designer";

	public static void main(String[] args) {
		new Designer();
	}

	private JComboBox documentDropDown;

	private JFrame fullScreenFrame;

	private int lastWindowX = 0;

	private int lastWindowY = 0;

	private double lastZoomFactor = 1.0;

	private JFrame mainFrame;

	private JPanel mainPanel;

	private JTextField printerDPITF;

	private JComboBox printerDropDown;

	private JScrollPane scrollPane;

	private JComboBox userDropDown;

	private JTextField zoomTextField;

	public Designer() {
		this(0, 0);
	}

	/**
	 * @param designerWindowY
	 * @param designerWindowX
	 */
	private Designer(int designerWindowX, int designerWindowY) {
		lastWindowX = designerWindowX;
		lastWindowY = designerWindowY;
		setupGUI();
	}

	/**
	 * @return the listener that saves where the window is moved.
	 * 
	 * @created Feb 26, 2006
	 * @author Ron Yeh
	 */
	private WindowListener getCloseHandler() {
		return new WindowListener() {

			private void updateWindowLocation() {
				lastWindowX = mainFrame.getLocation().x;
				lastWindowY = mainFrame.getLocation().y;
			}

			public void windowActivated(WindowEvent arg0) {
				updateWindowLocation();
			}

			public void windowClosed(WindowEvent arg0) {

			}

			public void windowClosing(WindowEvent we) {
				updateWindowLocation();
			}

			public void windowDeactivated(WindowEvent arg0) {
				updateWindowLocation();
			}

			public void windowDeiconified(WindowEvent arg0) {

			}

			public void windowIconified(WindowEvent arg0) {

			}

			public void windowOpened(WindowEvent arg0) {

			}
		};
	}

	/**
	 * @return
	 * 
	 * @created Feb 15, 2006
	 * @author Ron Yeh
	 */
	private Container getDocumentPanel() {
		if (mainPanel == null) {
			mainPanel = new DocumentPanel(new Sheet(8.5, 11));
			mainPanel.setBackground(Color.WHITE);
			mainPanel.setLayout(new BorderLayout());
		}
		return mainPanel;
	}

	/**
	 * @return Where the window was last positioned (X Coordinate)
	 * @created Mar 1, 2006
	 * @author Ron Yeh
	 */
	public int getLastWindowX() {
		return lastWindowX;
	}

	/**
	 * @return Where the window was last positioned (Y Coordinate)
	 * @created Mar 1, 2006
	 * @author Ron Yeh
	 */
	public int getLastWindowY() {
		return lastWindowY;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * Creates the Frame and components.
	 * 
	 * @created Feb 15, 2006
	 * @author Ron Yeh
	 */
	private void setupGUI() {

		// JGoodies Look and Feel
		try {
			UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
		} catch (Exception e) {
		}

		mainFrame = new JFrame();
		mainFrame.setTitle(TITLE);
		mainFrame.setSize(1024, 768);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(getDocumentPanel());
		// mainFrame.pack();

		if (lastWindowX == 0 && lastWindowY == 0) {
			mainFrame.setLocation(WindowUtils
					.getWindowOrigin(mainFrame, WindowUtils.DESKTOP_CENTER));
		} else {
			mainFrame.setLocation(lastWindowX, lastWindowY);
		}

		// mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

		mainFrame.addWindowListener(getCloseHandler());

		// last thing
		mainFrame.setVisible(true);

	}

	/**
	 * 
	 * @created Mar 31, 2006
	 * @author Ron Yeh
	 */
	public void startFullScreenView(int displayNumber) {
		if (fullScreenFrame == null) {
			fullScreenFrame = new JFrame();
			fullScreenFrame.setUndecorated(true);
			fullScreenFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			fullScreenFrame.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent e) {
				}

				public void windowClosed(WindowEvent e) {
				}

				public void windowClosing(WindowEvent e) {

				}

				public void windowDeactivated(WindowEvent e) {
					final Container contentPane = fullScreenFrame.getContentPane();
					contentPane.removeAll();
					System.out.println("Window Deactivated... Adding back to the old panel...");
					scrollPane.setViewportView(new JPanel());
					scrollPane.repaint();
				}

				public void windowDeiconified(WindowEvent e) {

				}

				public void windowIconified(WindowEvent e) {

				}

				public void windowOpened(WindowEvent e) {

				}
			});
		}

		final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		final GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
		if (displayNumber >= 0 && displayNumber < screenDevices.length) {
			screenDevices[displayNumber].setFullScreenWindow(fullScreenFrame);
		} else {
			screenDevices[0].setFullScreenWindow(fullScreenFrame);
		}

		fullScreenFrame.setVisible(true);
	}

}
