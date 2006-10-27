package edu.stanford.hci.r3.design;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.design.toolbar.DebuggingTasks;
import edu.stanford.hci.r3.design.toolbar.DocumentTasks;
import edu.stanford.hci.r3.design.toolbar.UserTestTasks;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.components.ribbons.RibbonPanel;
import edu.stanford.hci.r3.util.components.ribbons.RibbonToolbar;

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

	private DebuggingTasks debuggingTasks;

	private DocumentPanel documentPanel;

	private DocumentTasks documentTasks;

	private JFrame fullScreenFrame;

	private int lastWindowX = 0;

	private int lastWindowY = 0;

	private JFrame mainFrame;

	private JPanel mainPanel;

	private JScrollPane scrollPane;

	private RibbonToolbar toolbar;

	private UserTestTasks userTestTasks;

	/**
	 * 
	 */
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
		return new WindowAdapter() {
			private void updateWindowLocation() {
				lastWindowX = mainFrame.getLocation().x;
				lastWindowY = mainFrame.getLocation().y;
			}

			public void windowActivated(WindowEvent arg0) {
				updateWindowLocation();
			}

			public void windowClosing(WindowEvent we) {
				updateWindowLocation();
			}

			public void windowDeactivated(WindowEvent arg0) {
				updateWindowLocation();
			}
		};
	}

	/**
	 * @return
	 */
	private List<RibbonPanel> getDebuggingTasksComponents() {
		if (debuggingTasks == null) {
			debuggingTasks = new DebuggingTasks();
		}
		return debuggingTasks.getPanels();
	}

	/**
	 * @return
	 * 
	 * @created Feb 15, 2006
	 * @author Ron Yeh
	 */
	private DocumentPanel getDocumentPanel() {
		if (documentPanel == null) {
			documentPanel = new DocumentPanel();
		}
		return documentPanel;
	}

	/**
	 * @return
	 */
	private List<RibbonPanel> getDocumentTasksComponents() {
		if (documentTasks == null) {
			documentTasks = new DocumentTasks();
		}
		return documentTasks.getPanels();
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

	/**
	 * @return
	 */
	public JFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new JFrame();
			mainFrame.setTitle(TITLE);
			mainFrame.setSize(1024, 768);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setContentPane(getMainPanel());
			mainFrame.pack();
			// position the window
			if (lastWindowX == 0 && lastWindowY == 0) {
				mainFrame.setLocation(WindowUtils.getWindowOrigin(mainFrame,
						WindowUtils.DESKTOP_CENTER));
			} else {
				mainFrame.setLocation(lastWindowX, lastWindowY);
			}
			mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
			mainFrame.addWindowListener(getCloseHandler());
			mainFrame.setVisible(true);
		}
		return mainFrame;
	}

	/**
	 * @return
	 */
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getRibbonToolbar(), BorderLayout.NORTH);
			mainPanel.add(getScrollableDocumentPanel(), BorderLayout.CENTER);
		}
		return mainPanel;
	}

	/**
	 * @return
	 */
	private JScrollPane getScrollableDocumentPanel() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getDocumentPanel(),
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			getDocumentPanel().setParentScrollPane(scrollPane);
			getDocumentPanel().setParentFrame(mainFrame);
		}
		return scrollPane;
	}

	/**
	 * @return the toolbar
	 */
	private Component getRibbonToolbar() {
		if (toolbar == null) {
			toolbar = new RibbonToolbar();

			final String documentTasksName = "Application, Sheets, and Regions";
			final String userTestName = "User Testing";
			final String debuggingName = "Debugging";
			toolbar.addCategories(documentTasksName, userTestName, debuggingName);
			toolbar.addToolsToCategoryPanel(documentTasksName, getDocumentTasksComponents());
			toolbar.addToolsToCategoryPanel(userTestName, getUserTestTasksComponents());
			toolbar.addToolsToCategoryPanel(debuggingName, getDebuggingTasksComponents());
		}
		return toolbar;
	}

	/**
	 * @return
	 */
	private List<RibbonPanel> getUserTestTasksComponents() {
		if (userTestTasks == null) {
			userTestTasks = new UserTestTasks();
		}
		return userTestTasks.getPanels();
	}

	/**
	 * Creates the Frame and components.
	 * 
	 * @created Feb 15, 2006
	 * @author Ron Yeh
	 */
	private void setupGUI() {
		PaperToolkit.initializeLookAndFeel();
		getMainFrame();
	}

	/**
	 * View the Designer in FullScreen Mode.
	 * 
	 * @created Mar 31, 2006
	 * @author Ron Yeh
	 */
	public void startFullScreenView(int displayNumber) {
		if (fullScreenFrame == null) {
			fullScreenFrame = new JFrame();
			fullScreenFrame.setUndecorated(true);
			fullScreenFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			fullScreenFrame.addWindowListener(new WindowAdapter() {
				public void windowDeactivated(WindowEvent e) {
					final Container contentPane = fullScreenFrame.getContentPane();
					contentPane.removeAll();
					DebugUtils.println("Window Deactivated... Adding back to the old panel...");
					scrollPane.setViewportView(new JPanel());
					scrollPane.repaint();
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
