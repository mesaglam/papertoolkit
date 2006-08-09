/**
 * version 0.1 -- Created for the FlowVis project (Winter 2004). ronyeh
 * version 0.2 -- Added Desktop location stuff (Summer 2004). ronyeh
 * version 0.3 -- Added Screen Size for BNet (Summer 2005). ronyeh
 * 
 * <p>
 * This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron Yeh</a> (
 *         ronyeh(AT)cs.stanford.edu )
 */

package edu.stanford.hci.r3.util;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * @author Ron B Yeh <ronyeh(AT)cs.stanford.edu>
 * @comment This class contains useful methods for positioning, sizing, and manipulating JFrames and
 *          other Window-like elements.
 */
public class WindowUtils {

	// defaults for screen width and height are very small
	private static int cachedScreenHeight = 480;

	// defaults for screen width and height are very small
	private static int cachedScreenWidth = 640;

	public static final int DESKTOP_CENTER = 0;

	public static final int DESKTOP_EAST = 2;

	public static final int DESKTOP_NORTH = 3;

	public static final int DESKTOP_NORTHEAST = 6;

	public static final int DESKTOP_NORTHWEST = 5;

	public static final int DESKTOP_SOUTH = 4;

	public static final int DESKTOP_SOUTHEAST = 8;

	public static final int DESKTOP_SOUTHWEST = 7;

	public static final int DESKTOP_WEST = 1;

	// the rectangle that is available for drawing on the desktop
	// discounts start menus, finder menu bar, etc.
	private static Rectangle desktopBounds = new Rectangle(0, 0, 640, 480);

	private static Point fsFrameLocation;

	private static boolean fsFrameResizable;

	private static Dimension fsFrameSize;

	// cached for the last frame that was full-screened
	private static boolean fsFrameUndecorated;

	public static final int INVALID_MAX = 18;

	// constants for locations on the screen
	public static final int INVALID_MIN = -1;

	public static final int SCREEN_CENTER = 9;

	public static final int SCREEN_EAST = 11;

	public static final int SCREEN_NORTH = 12;

	public static final int SCREEN_NORTHEAST = 15;

	public static final int SCREEN_NORTHWEST = 14;

	public static final int SCREEN_SOUTH = 13;

	public static final int SCREEN_SOUTHEAST = 17;

	public static final int SCREEN_SOUTHWEST = 16;

	public static final int SCREEN_WEST = 10;

	// called upon reference to this class
	static {
		initCachedWindowState();
	}

	/**
	 * Plops the frame into the center of the desktop.
	 * 
	 * @param frame
	 */
	public static void centerWindow(JFrame frame) {
		frame.setLocation(getWindowOrigin(frame.getWidth(), frame.getHeight(),
				WindowUtils.DESKTOP_CENTER));
	}

	/**
	 * @param mainAppFrame
	 *           window to be made full screen.
	 */
	public static void enterFullScreenIfPossible(JFrame mainAppFrame) {
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice defaultScreenDevice = ge.getDefaultScreenDevice();
		// System.out.println(defaultScreenDevice.isFullScreenSupported());
		// System.out.println(defaultScreenDevice.getDisplayMode());
		// System.out.println(defaultScreenDevice.getDisplayMode().getWidth());
		// System.out.println(defaultScreenDevice.getDisplayMode().getHeight());
		// System.out.println(defaultScreenDevice.getDisplayMode()
		// .getRefreshRate());
		// System.out.println(defaultScreenDevice.getDisplayMode().getBitDepth());
		if (defaultScreenDevice.isFullScreenSupported()) {
			try {
				mainAppFrame.dispose();

				fsFrameSize = mainAppFrame.getSize();
				mainAppFrame.setSize(getScreenSize());

				fsFrameUndecorated = mainAppFrame.isUndecorated();
				mainAppFrame.setUndecorated(true);

				fsFrameResizable = mainAppFrame.isResizable();
				mainAppFrame.setResizable(false);

				fsFrameLocation = mainAppFrame.getLocation();
				mainAppFrame.setLocation(0, 0);

				defaultScreenDevice.setFullScreenWindow(mainAppFrame);
			} finally {
				defaultScreenDevice.setFullScreenWindow(null);
			}
		} else {
			System.err.println("Fullscreen mode is not supported on this monitor.");
		}
	}

	/**
	 * @param mainAppFrame
	 */
	public static void exitFullScreen(JFrame mainAppFrame) {
		mainAppFrame.dispose();
		mainAppFrame.setUndecorated(fsFrameUndecorated);
		mainAppFrame.setResizable(fsFrameResizable);
		mainAppFrame.setSize(fsFrameSize);
		mainAppFrame.setLocation(fsFrameLocation);
		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.setFullScreenWindow(null);
		mainAppFrame.setVisible(true);
	}

	/**
	 * Determines where to put windows based on the cached state
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @param where
	 * @see util.WindowUtilities.initCachedWindowState()
	 */
	public static Point getCachedWindowOrigin(int windowWidth, int windowHeight, int where) {
		Point p = new Point(0, 0);
		if ((where >= INVALID_MAX) || (where <= INVALID_MIN)) {
			// invalid locations
			return p;
		} else if ((windowWidth < 0) || (windowHeight < 0)) {
			// invalid window sizes
			return p;
		}

		int x = 0;
		int y = 0;

		switch (where) {

		case DESKTOP_CENTER:
			x = desktopBounds.x + desktopBounds.width / 2 - windowWidth / 2;
			y = desktopBounds.y + desktopBounds.height / 2 - windowHeight / 2;
			break;
		case DESKTOP_NORTH:
			x = desktopBounds.x + desktopBounds.width / 2 - windowWidth / 2;
			y = desktopBounds.y;
			break;
		case DESKTOP_EAST:
			x = desktopBounds.x + desktopBounds.width - windowWidth;
			y = desktopBounds.y + desktopBounds.height / 2 - windowHeight / 2;
			break;
		case DESKTOP_WEST:
			x = desktopBounds.x;
			y = desktopBounds.y + desktopBounds.height / 2 - windowHeight / 2;
			break;
		case DESKTOP_SOUTH:
			x = desktopBounds.x + desktopBounds.width / 2 - windowWidth / 2;
			y = desktopBounds.y + desktopBounds.height - windowHeight;
			break;
		case DESKTOP_NORTHWEST:
			x = desktopBounds.x;
			y = desktopBounds.y;
			break;
		case DESKTOP_NORTHEAST:
			x = desktopBounds.x + desktopBounds.width - windowWidth;
			y = desktopBounds.y;
			break;
		case DESKTOP_SOUTHWEST:
			x = desktopBounds.x;
			y = desktopBounds.y + desktopBounds.height - windowHeight;
			break;
		case DESKTOP_SOUTHEAST:
			x = desktopBounds.x + desktopBounds.width - windowWidth;
			y = desktopBounds.y + desktopBounds.height - windowHeight;
			break;
		// //////////////////////////////////////////////
		case SCREEN_CENTER:
			x = cachedScreenWidth / 2 - windowWidth / 2;
			y = cachedScreenHeight / 2 - windowHeight / 2;
			break;
		case SCREEN_NORTH:
			x = cachedScreenWidth / 2 - windowWidth / 2;
			y = 0;
			break;
		case SCREEN_EAST:
			x = cachedScreenWidth - windowWidth;
			y = cachedScreenHeight / 2 - windowHeight / 2;
			break;
		case SCREEN_WEST:
			x = 0;
			y = cachedScreenHeight / 2 - windowHeight / 2;
			break;
		case SCREEN_SOUTH:
			x = cachedScreenWidth / 2 - windowWidth / 2;
			y = cachedScreenHeight - windowHeight;
			break;
		case SCREEN_NORTHWEST:
			x = 0;
			y = 0;
			break;
		case SCREEN_NORTHEAST:
			x = cachedScreenWidth - windowWidth;
			y = 0;
			break;
		case SCREEN_SOUTHWEST:
			x = 0;
			y = cachedScreenHeight - windowHeight;
			break;
		case SCREEN_SOUTHEAST:
			x = cachedScreenWidth - windowWidth;
			y = cachedScreenHeight - windowHeight;
			break;
		default:
			x = 0;
			y = 0;
			break;
		}

		// window too large
		if (windowHeight > cachedScreenHeight) {
			y = 0;
		}
		if (windowWidth > cachedScreenWidth) {
			x = 0;
		}

		p.setLocation(x, y);
		return p;
	}

	/**
	 * @return the rectangle describing the user's desktop
	 */
	public static Rectangle getDesktopBounds() {
		return desktopBounds;
	}

	/**
	 * @return the size of the user's desktop
	 */
	public static Dimension getDesktopSize() {
		return desktopBounds.getSize();
	}

	/**
	 * @return
	 */
	public static int getScreenHeight() {
		return cachedScreenHeight;
	}

	/**
	 * @return the screen size of the last known monitor mode
	 */
	public static Dimension getScreenSize() {
		return new Dimension(getScreenWidth(), getScreenHeight());
	}

	/**
	 * @return
	 */
	public static int getScreenWidth() {
		return cachedScreenWidth;
	}

	/**
	 * Given a size (width, height) of a window, and an intended location on the desktop, it returns
	 * the x, y location of where the origin should reside. This method will be pretty resilient to
	 * the user changing his/her interface (taskbar locations, etc). If you want a faster method, but
	 * would sacrifice a (little) bit of assurances... then use getCachedWindowOrigin(...) directly
	 * (after calling initCachedWindowState() once)
	 * 
	 * @param windowWidth
	 * @param windowHeight
	 * @param where
	 * @return
	 */
	public static Point getWindowOrigin(int windowWidth, int windowHeight, int where) {
		initCachedWindowState();
		return getCachedWindowOrigin(windowWidth, windowHeight, where);
	}

	/**
	 * @param frame
	 * @param where
	 * @return
	 */
	public static Point getWindowOrigin(Frame frame, int where) {
		return getCachedWindowOrigin(frame.getWidth(), frame.getHeight(), where);
	}

	/**
	 * Call this before calling getCachedWindowOrigin(...) to get the current state of the user's
	 * screen and desktop size. The user may have changed it since the last call. This is called once
	 * when WindowUtils is first accessed.
	 */
	public static void initCachedWindowState() {
		// get the current Screen Size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		cachedScreenWidth = (int) screenSize.getWidth();
		cachedScreenHeight = (int) screenSize.getHeight();

		// get the current Desktop Size
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		desktopBounds = env.getMaximumWindowBounds();
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 * 
	 * @author Ron Yeh
	 */
	public static void main(String[] args) throws InterruptedException {
		JFrame f = new JFrame("Bob");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setLocation(10, 10);
		f.setVisible(true);
		Thread.sleep(3000);
		enterFullScreenIfPossible(f);
		Thread.sleep(3000);
		exitFullScreen(f);
	}

	/**
	 * Opens a container inside a JFrame. Useful for quick testing.
	 * 
	 * @param content
	 * @param width
	 * @param height
	 * @return
	 */
	public static JFrame openInJFrame(Container content, int width, int height) {
		return openInJFrame(content, width, height, content.getClass().getName(), Color.white);
	}

	/**
	 * Opens a container inside a JFrame. Useful for quick testing.
	 * 
	 * @param content
	 * @param width
	 * @param height
	 * @param title
	 * @return
	 */
	public static JFrame openInJFrame(Container content, int width, int height, String title) {
		return (openInJFrame(content, width, height, title, Color.white));
	}

	/**
	 * @param content
	 * @param width
	 * @param height
	 * @param title
	 * @param bgColor
	 * @return
	 */
	public static JFrame openInJFrame(Container content, int width, int height, String title,
			Color bgColor) {
		return (openInJFrame(content, width, height, title, bgColor, true));
	}

	/**
	 * Opens a container inside a JFrame. Useful for quick testing.
	 * 
	 * @param content
	 * @param width
	 * @param height
	 * @param title
	 * @param bgColor
	 * @param exitOnClose
	 * @return
	 */
	public static JFrame openInJFrame(Container content, int width, int height, String title,
			Color bgColor, boolean exitOnClose) {
		JFrame frame = new JFrame(title);
		frame.setBackground(bgColor);
		content.setBackground(bgColor);
		frame.setSize(width, height);
		frame.setContentPane(content);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		frame.setVisible(true);
		return (frame);
	}

	/**
	 * The pleasant-looking *cough* Java look and feel.
	 */
	public static void setJavaLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting Java LAF: " + e);
		}
	}

	/**
	 * Old Skool Look and Feel.
	 */
	public static void setMotifLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		} catch (Exception e) {
			System.out.println("Error setting Motif LAF: " + e);
		}
	}

	/**
	 * Windows, Mac, or Unix look and feels
	 */
	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}
	}

	/**
	 * @param frame
	 * 
	 * @created Feb 16, 2006
	 * @author Ron Yeh
	 */
	public static void fitToDesktop(final JFrame frame) {
		final int minWidth = (int) Math.min(frame.getBounds().getWidth(), desktopBounds.getWidth());
		final int minHeight = (int) Math.min(frame.getBounds().getHeight(), desktopBounds.getHeight());
		
		frame.setSize(minWidth, minHeight);
	}
}
