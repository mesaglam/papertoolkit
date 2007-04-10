package edu.stanford.hci.r3.pen;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.DebugUtils;

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
 */
public abstract class PenInput {

	/**
	 * TRUE if the PenInput object is currently connected to the underlying (physical or simulated) pen in
	 * streaming mode.
	 */
	protected boolean liveMode = false;

	/**
	 * A simple default name.
	 */
	private String name;

	/**
	 * Cached pen listeners, so we can add them when/if you go live.
	 * 
	 * TODO: How will we handle batched events later on?
	 */
	protected List<PenListener> penListenersToAdd = new ArrayList<PenListener>();

	/**
	 * @param penName
	 */
	public PenInput(String penName) {
		setName(penName);
	}

	/**
	 * Adds a low-level pen data listener to the live pen. You SHOULD call this after starting live mode....
	 * However, we can cache the listener for you, if you really want. This is to eliminate annoying ordering
	 * constraints.
	 * 
	 * Subclasses *should* override this, and call the super, to actually make use of PenListeners.
	 * 
	 * @param penListener
	 * @return true if we cached the pen listener on the penListenersToAdd list
	 */
	public void addLivePenListener(PenListener penListener) {
		if (!isLive()) {
			DebugUtils.println("We are not registering this listener [" + penListener.toString()
					+ "] with the event dispatcher at the moment. The Pen is not in Live Mode.");
			DebugUtils.println("We will keep this listener around until you startLiveMode().");
			penListenersToAdd.add(penListener);
		}
	}

	/**
	 * @return the name of this pen
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return if this pen in live mode.
	 */
	public boolean isLive() {
		return liveMode;
	}

	/**
	 * Removes the pen listener from the live pen. Subclasses *should* override this, and call the super's
	 * implementation if necessary, to actually make use of PenListeners.
	 * 
	 * @param penListener
	 */
	public void removeLivePenListener(PenListener penListener) {
		if (penListenersToAdd.contains(penListener)) {
			penListenersToAdd.remove(penListener);
			DebugUtils.println("Removed " + penListener + " from the cache of listeners.");
		}
	}

	/**
	 * @param nomDePlume
	 *            For differentiating pens during debugging.
	 */
	public void setName(String nomDePlume) {
		name = nomDePlume;
	}

	public abstract void startLiveMode();

	public abstract void stopLiveMode();

}
