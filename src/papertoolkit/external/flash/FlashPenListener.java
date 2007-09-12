package papertoolkit.external.flash;

import papertoolkit.external.ExternalCommunicationServer;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;

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
	 * Send pen samples over to this Flash GUI. 
	 */
	private ExternalCommunicationServer flash;

	public FlashPenListener(ExternalCommunicationServer f) {
		flash = f;
	}

	public void penDown(PenSample sample) {
		flash.sendMessage("<penDownEvent/>");
		flash.sendMessage(sample.toXMLString());
	}

	public void penUp(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
		flash.sendMessage("<penUpEvent/>");
	}

	public void sample(PenSample sample) {
		flash.sendMessage(sample.toXMLString());
	}
}
