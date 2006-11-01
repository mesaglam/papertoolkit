package edu.stanford.hci.r3.pen.streaming.listeners;

import edu.stanford.hci.r3.pen.PenSample;

/**
 * <p>
 * This lives in the streaming.listeners package because you cannot use this listener directly if your pattern
 * spans multiple sheets. This listener stores information in raw Streaming PenSamples, so you will have to do
 * the calculations yourself... This class is definitely for experts only!
 * </p>
 * <p>
 * The class also doesn't do any filtering, so if the pen is bad, you will get lots of PenDown/PenUps in rapid
 * succession.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenStrokeListener implements PenListener {

	public void penDown(PenSample sample) {

	}

	public void penUp(PenSample sample) {

	}

	public void sample(PenSample sample) {

	}
}
