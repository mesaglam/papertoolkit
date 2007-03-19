package edu.stanford.hci.r3.demos.flash;

import edu.stanford.hci.r3.flash.whiteboard.FlashInkRelayServer;
import edu.stanford.hci.r3.flash.whiteboard.FlashWhiteboard;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Shows how to use the Flash components to assemble a Live Whiteboard that will get data from the
 * local pen device in real time.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class LiveWhiteboard implements PenListener {

	public static void main(String[] args) {
		new LiveWhiteboard();
	}

	private FlashInkRelayServer flash;

	public LiveWhiteboard() {
		// in R3, there are two ways to make real-time pen and paper applications... One is to use
		// the Application framework. A second one, which we will use here, is to just attach a pen
		// listener to a live (local) Pen.
		Pen pen = new Pen();
		pen.startLiveMode();

		// just to see the pen coordinates
		pen.addLivePenListener(this);

		// start the server for sending ink over to Flash
		flash = new FlashInkRelayServer();

		// load the Flash component that listens for real-time ink!
		// basically, just open the HTML page that contains the flash component! =)

		new FlashWhiteboard();
	}

	@Override
	public void penDown(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
	}

	@Override
	public void penUp(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
	}

	@Override
	public void sample(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
	}
}
