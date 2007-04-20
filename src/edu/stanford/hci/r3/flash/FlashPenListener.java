package edu.stanford.hci.r3.flash;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Sends XML of streaming ink information to the Flash GUI.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashPenListener implements PenListener {

	private FlashCommunicationServer flash;

	public FlashPenListener(FlashCommunicationServer f) {
		flash = f;
	}

	@Override
	public void penDown(PenSample sample) {
		flash.sendMessage("<penDownEvent/>");
		flash.sendMessage(sample.toXMLString());
	}

	@Override
	public void penUp(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
		flash.sendMessage("<penUpEvent/>");
	}

	@Override
	public void sample(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
	}

}
