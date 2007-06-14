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

	/**
	 * 
	 */
	private FlashCommunicationServer flash;

	/**
	 * @param f
	 */
	public FlashPenListener(FlashCommunicationServer f) {
		flash = f;
	}

	/* (non-Javadoc)
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#penDown(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void penDown(PenSample sample) {
		flash.sendMessage("<penDownEvent/>");
		flash.sendMessage(sample.toXMLString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#penUp(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void penUp(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
		flash.sendMessage("<penUpEvent/>");
	}

	/* (non-Javadoc)
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#sample(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void sample(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
	}
}
