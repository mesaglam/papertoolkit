package papertoolkit.pen.streaming;

import papertoolkit.pen.PenSample;

/**
 * <p>
 * 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class PenJitterFilter {

	private static final long MILLIS_TO_DELAY = 30;

	public interface PenUpCallback {
		public void penUp(PenSample s);
	}

	private class PenUpNotifier implements Runnable {

		private boolean doNotNotify;
		private PenSample s;

		public PenUpNotifier(PenSample mySample) {
			s = mySample;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				Thread.sleep(MILLIS_TO_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (doNotNotify) {
				// someone told us to cancel
				return;
			}

			penUpCallback.penUp(s);
		}

		/**
		 * @param b
		 */
		public void setDoNotNotify(boolean b) {
			doNotNotify = b;
		}
	}

	private PenUpCallback penUpCallback;
	private PenUpNotifier lastPenUpNotifier;

	/**
	 * @param pucb
	 */
	public PenJitterFilter(PenUpCallback pucb) {
		penUpCallback = pucb;
	}

	/**
	 * @param s
	 */
	public void triggerPenUpAfterADelay(PenSample s) {
		lastPenUpNotifier = new PenUpNotifier(s);
		new Thread(lastPenUpNotifier).start();
	}

	public boolean happenedTooCloseToLastPenUp() {
		// TODO: Turn this into a real time check later...
		return true;
	}

	public void cancelLastPenUp() {
		// just filter this out by canceling the notifier
		if (lastPenUpNotifier != null) {
			lastPenUpNotifier.setDoNotNotify(true);
			lastPenUpNotifier = null;
		}
	}
}
