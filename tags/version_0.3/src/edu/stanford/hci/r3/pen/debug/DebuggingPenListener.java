package edu.stanford.hci.r3.pen.debug;

import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;

/**
 * <p>
 * Allows us to debug multiple pens.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DebuggingPenListener implements PenListener {

	private String name;

	public DebuggingPenListener() {
		this("Local Debugging Pen Listener");
	}

	public DebuggingPenListener(String myName) {
		name = myName;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void penDown(PenSample sample) {
		System.out.println("Pen Down [" + name + "]: " + sample);
	}

	public void penUp(PenSample sample) {
		System.out.println("Pen Up [" + name + "]: " + sample);
	}

	public void sample(PenSample sample) {
		System.out.println("[" + name + "]: " + sample);
	}

	public String toString() {
		return name;
	}
}
