package papertoolkit.pen.streaming;

import papertoolkit.pen.PenSample;

/**
 * <p>
 * Filter pens at the lowest level... The problem is that many of the newer Nokia pens have a jitter issue,
 * resulting in many PENUPs interspersed with real data. We will try to weed out those PENUP events.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenJitterFilter {

	/**
	 * <p>
	 * </p>
	 */
	public interface PenUpCallback {

		/**
		 * @param s
		 */
		public void penUp(PenSample s);
	}

	/**
	 * <p>
	 * </p>
	 */
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

	/**
	 * 
	 */
	private static final long MILLIS_TO_DELAY = 30;

	/**
	 * 
	 */
	private static final long FILTER_THRESHOLD_MILLIS = 20;
	
	/**
	 * 
	 */
	private PenUpNotifier lastPenUpNotifier;

	/**
	 * 
	 */
	private PenUpCallback penUpCallback;

	/**
	 * The timestamp of the last penup trigger. It can be used to check if the newest penup event is too close
	 * to the last triggered penup.
	 */
	private long lastPenUpTimeStamp = 0L;

	/**
	 * @param pucb
	 */
	public PenJitterFilter(PenUpCallback pucb) {
		penUpCallback = pucb;
	}

	/**
	 * 
	 */
	public void cancelLastPenUp() {
		// just filter this out by canceling the notifier
		if (lastPenUpNotifier != null) {
			lastPenUpNotifier.setDoNotNotify(true);
			lastPenUpNotifier = null;
		}
	}

	/**
	 * @return if we are still too close to the last call to triggerPenUpAfterADelay(...)
	 */
	public boolean happenedTooCloseToLastPenUp() {
		return (System.currentTimeMillis() - lastPenUpTimeStamp < FILTER_THRESHOLD_MILLIS);
	}

	/**
	 * @param s
	 */
	public void triggerPenUpAfterADelay(PenSample s) {
		lastPenUpTimeStamp = System.currentTimeMillis();
		lastPenUpNotifier = new PenUpNotifier(s);
		new Thread(lastPenUpNotifier).start();
	}
}
