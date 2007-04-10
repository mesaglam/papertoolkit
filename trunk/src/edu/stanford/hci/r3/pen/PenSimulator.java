package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Opens a JFrame/JPanel that the user can draw on with his mouse or Tablet stylus. This will simulate a
 * digital pen. We can make the pen activate either by toggling (via left-cli
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PenSimulator extends PenInput {

	private boolean liveMode;

	@Override
	public void addLivePenListener(PenListener listener) {

	}

	@Override
	public String getName() {
		return "Pen Simulator";
	}

	/**
	 * @return if this pen in live mode.
	 */
	@Override
	public boolean isLive() {
		return liveMode;
	}

	@Override
	public void removeLivePenListener(PenListener listener) {

	}

	@Override
	public void startLiveMode() {
		// open the JFrame

		liveMode = true;
	}

	@Override
	public void stopLiveMode() {
		// close the JFrame

		liveMode = false;
	}
}
