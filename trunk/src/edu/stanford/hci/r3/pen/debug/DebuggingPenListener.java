package edu.stanford.hci.r3.pen.debug;

import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Allows us to debug one or more pens, to see if they are streaming properly. Add an instance to a Pen object
 * using pen.addLivePenListener().
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class DebuggingPenListener implements PenListener {

	private String name = "Debug Pen";

	/**
	 * For printing out the value of pen samples that are streamed wirelessly.
	 */
	public DebuggingPenListener() {
		name = "Debugging Pen Listener";
	}

	/**
	 * Uses the pen's name, so we can identify which pen we are attached to...
	 * @param pen
	 */
	public DebuggingPenListener(Pen pen) {
		name = "Debugging Pen Listener for [" + pen.getName() + "]";
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#penDown(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void penDown(PenSample sample) {
		System.out.println("Down [" + name + "]: " + sample);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#penUp(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void penUp(PenSample sample) {
		System.out.println("Up [" + name + "]: " + sample + "\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#sample(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void sample(PenSample sample) {
		System.out.println("[" + name + "]: " + sample);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
}
