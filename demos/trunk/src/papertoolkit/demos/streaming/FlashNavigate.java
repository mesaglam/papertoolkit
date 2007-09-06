package papertoolkit.demos.streaming;

import papertoolkit.flash.FlashCommunicationServer;
import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Talks to a Flash GUI that connects via an XML socket. The Flash GUI listens for text commands such as
 * [[Circle Map]], and responds appropriately.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashNavigate {

	private static PenSample down;
	private static FlashCommunicationServer server;
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
					server.sendLine("[[Circle Map]]");
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
		server = new FlashCommunicationServer();
		Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(getDebugPenListener());
	}

}
