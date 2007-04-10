package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Applications need some way to get Pen Input. We can either provide this through the Pen class (which
 * implements this as a digital streaming pen), OR we can simulate this with a graphics tablet.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public interface PenInput {

	public void addLivePenListener(PenListener listener);

	public String getName();

	public boolean isLive();

	public void removeLivePenListener(PenListener listener);

	public void startLiveMode();

	public void stopLiveMode();

}
