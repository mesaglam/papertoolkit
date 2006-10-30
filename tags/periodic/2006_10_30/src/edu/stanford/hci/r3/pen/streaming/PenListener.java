package edu.stanford.hci.r3.pen.streaming;

/**
 * <p>
 * Listens for Pen events, such as when a pen touches pattern, or when samples are sent wirelessly.
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
	 * @param sample
	 */
	public void penDown(PenSample sample);

	/**
	 * Samples for penUp have x & y set to 0.
	 * 
	 * @param sample
	 */
	public void penUp(PenSample sample);

	/**
	 * A sample happened.
	 * 
	 * @param sample
	 */
	public void sample(PenSample sample);
}