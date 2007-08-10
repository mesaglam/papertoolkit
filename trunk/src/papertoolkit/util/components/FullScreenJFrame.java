package papertoolkit.util.components;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @author Joel Brandt
 */
public class FullScreenJFrame extends JFrame {

	/**
	 * 
	 */
	private GraphicsDevice screenDevice;

	/**
	 * 
	 */
	public FullScreenJFrame() {
		setUndecorated(true);
		final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		screenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
	}

	/**
	 * @param deviceIndex
	 */
	public FullScreenJFrame(int deviceIndex) {
		setUndecorated(true);
		final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
		screenDevice = screenDevices[deviceIndex];
	}

	/**
	 * 
	 */
	private void chooseBestDisplayMode() {
		if (screenDevice.isDisplayChangeSupported()) {
			DisplayMode best = getBestDisplayMode();
			if (best != null) {
				screenDevice.setDisplayMode(best);
			}
		}
	}

	/**
	 * @return
	 */
	private DisplayMode getBestDisplayMode() {
		final DisplayMode[] availableModes = screenDevice.getDisplayModes();
		int maxWidth = 0;
		int maxDepth = 0;
		DisplayMode bestSoFar = null;
		for (DisplayMode m : availableModes) {
			System.out.println(m.getWidth() + " x " + m.getHeight() + " " + m.getBitDepth());
			if (m.getWidth() >= maxWidth) {
				maxWidth = m.getWidth();
				if (m.getBitDepth() >= maxDepth) {
					maxDepth = m.getBitDepth();
					bestSoFar = m;
				}
			}
		}
		return bestSoFar;
	}

	/**
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		if (b) {
			screenDevice.setFullScreenWindow(this);
			chooseBestDisplayMode();
			super.setVisible(true);
		} else {
			screenDevice.setFullScreenWindow(null);
			super.setVisible(false);
		}
	}

}
