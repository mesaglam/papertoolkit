package papertoolkit.pen;

import java.util.ArrayList;
import java.util.List;

import papertoolkit.pen.replay.SaveAndReplay;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.util.DebugUtils;

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
 * @see Pen.java
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class InputDevice {

	private int id = -1;

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
	 * The full list of live pen listeners...
	 */
	protected List<PenListener> penListeners = new ArrayList<PenListener>();

	/**
	 * Cached pen listeners, so we can "add" them when/if you go live.
	 */
	protected List<PenListener> penListenersToRegisterWhenLive = new ArrayList<PenListener>();

	/**
	 * 
	 */
	private SaveAndReplay saveAndReplay;
	private int uniquePenIDs = 0;

	/**
	 * @param penName
	 */
	public InputDevice(String penName) {
		id = uniquePenIDs++;
		setName(penName);
		saveAndReplay = SaveAndReplay.getInstance();
		PenListener penListener = saveAndReplay.getPenListener(this);

		// for subclasses who dispatch info by iterating through this list
		penListeners.add(penListener);

		// for subclasses who process penListeners at runtime.
		penListenersToRegisterWhenLive.add(penListener);
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
		// always keep track of penListeners, for save & replay
		penListeners.add(penListener);

		if (!isLive()) {
			// DebugUtils.println("We are not registering this listener [" + penListener.toString()
			// + "] with the event dispatcher at the moment. The Pen is not in Live Mode.");
			// DebugUtils.println("We will keep this listener around until you startLiveMode().");
			penListenersToRegisterWhenLive.add(penListener);
		}
	}

	/**
	 * @return
	 */
	public int getID() {
		return id;
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
		if (penListenersToRegisterWhenLive.contains(penListener)) {
			penListenersToRegisterWhenLive.remove(penListener);
		}
		if (penListeners.contains(penListener)) {
			penListeners.remove(penListener);
		}
		DebugUtils.println("Removed " + penListener + " from the list of PenListeners.");
	}

	/**
	 * @param nomDePlume
	 *            For differentiating pens during debugging.
	 */
	public void setName(String nomDePlume) {
		name = nomDePlume;
	}

	/**
	 * Start Listening to the Physical Pen / Simulator.
	 */
	public abstract void startLiveMode();

	/**
	 * Stop Listening to the Physical Pen / Simulator.
	 */
	public abstract void stopLiveMode();

}
