package edu.stanford.hci.r3.demos.streaming;

import edu.stanford.hci.r3.flash.timelineControl.FlashControlServer;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashNavigate {

	private static PenSample down;

	private static FlashControlServer server;

	private static PenSample up;

	/**
	 * @return
	 */
	private static PenListener getDebugPenListener() {
		return new PenListener() {
			public void penDown(PenSample sample) {
				System.out.println("Pen Down: " + sample);
				down = sample;
			}

			public void penUp(PenSample sample) {
				System.out.println("Pen Up: " + sample);
				if (up.y > down.y) {
					System.out.println("Dragged Down");
					server.sendMessage("[[Circle Map]]");
				} else {
					System.out.println("Dragged Up");
				}

			}

			public void sample(PenSample sample) {
				System.out.println(sample);
				// last sample will be checked by the penUp method
				up = sample;
			}
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		server = new FlashControlServer();

		Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(getDebugPenListener());
	}

}
