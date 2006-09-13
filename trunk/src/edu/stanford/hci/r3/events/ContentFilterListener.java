package edu.stanford.hci.r3.events;

/**
 * <p>
 * Listens to a content filter, and allows us to process the content once it arrives.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface ContentFilterListener {

	/**
	 * Get notified of new content.
	 */
	public void contentArrived();

	// TODO: ADD MORE HERE
}
