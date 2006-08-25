package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Use this to display Pen Coordinates to the console. Useful for debugging.
 */
public class PenCoordinateDebugger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(getDebugPenListener());
	}

	/**
	 * @return
	 */
	private static PenListener getDebugPenListener() {
		return new PenListener() {
			public void penDown(PenSample sample) {
				System.out.println("Pen Down: " + sample);
			}

			public void penUp(PenSample sample) {
				System.out.println("Pen Up: " + sample);
			}

			public void sample(PenSample sample) {
				System.out.println(sample);
			}
		};
	}
}
