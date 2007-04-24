package edu.stanford.hci.r3.demos.flash;

import java.awt.Color;

import edu.stanford.hci.r3.flash.whiteboard.FlashWhiteboard;
import edu.stanford.hci.r3.pen.Pen;

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
 */
public class LiveWhiteboardOnePen {

	public static void main(String[] args) {
		new LiveWhiteboardOnePen();
	}

	private FlashWhiteboard flashWhiteboard1;

	public LiveWhiteboardOnePen() {
		// in R3, there are two ways to make real-time pen and paper applications... One is to use
		// the Application framework. A second one, which we will use here, is to just attach a pen
		// listener to a live (local) Pen.
		Pen pen1 = new Pen(); // local pen

		// load the Flash component that listens for real-time ink!
		// basically, just open the HTML page that contains the flash component! =)
		flashWhiteboard1 = new FlashWhiteboard(8989);
		flashWhiteboard1.addPen(pen1);
		flashWhiteboard1.setSwatchColor(new Color(120, 220, 120));
		flashWhiteboard1.setTitle("Admit Weekend Whiteboard");
		flashWhiteboard1.load();
	}

}
