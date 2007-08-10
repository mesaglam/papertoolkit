package papertoolkit.actions;

/**
 * <p>
 * R3 Actions encompass high-level actions that can be invoked as the result of an event handler.
 * They can be arbitrarily complex, but the prepackaged R3Actions will be simple ones, like opening
 * a default browser, or playing a sound.
 * </p>
 * <p>
 * One nice thing about R3 Actions is that they can be sent across the wire to a remote action
 * server. That means we can make an auxiliary device listen for actions and invoke them.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface R3Action {

	/**
	 * Invoke this action on the local machine. If you want to invoke this on a remote machine, you
	 * will need to send it across the wire. That machine will then unserialize the action object
	 * and call invoke() on ITS local machine.
	 */
	public void invoke();

	/**
	 * @return a description of the action.
	 */
	public String toString();
}
