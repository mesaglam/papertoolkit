package edu.stanford.hci.r3.pen.streaming.listeners;

import edu.stanford.hci.r3.pen.PenSample;

/**
 * <p>
 * Listens for streaming Pen events, such as when a pen touches down on patterned paper (penDown), or when
 * samples are sent while the pen is down (sample) or when the pen is lifted (penUp).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface PenListener {

	/**
	 * The first callback that is fired when a pen touches patterned paper.
	 * This is basically an event that occurs on the first sample the pen receives.
	 * 
	 * @param sample
	 */
	public void penDown(PenSample sample);

	/**
	 * When a pen is lifted, this callback is fired. Samples for penUp have x & y set to 0.
	 * 
	 * @param sample
	 */
	public void penUp(PenSample sample);

	/**
	 * A sample happened. While the user is writing, this callback is fired.
	 * 
	 * @param sample
	 */
	public void sample(PenSample sample);
}