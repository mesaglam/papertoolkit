package edu.stanford.hci.r3.actions.remote;

import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * Deals with actions that have been unserialized by ActionReceiver. It provides a useful default,
 * but you can override the functionality if you want to make more interesting things happen (e.g.,
 * batch actions until a certain time).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionHandler {

	/**
	 * Invoke the action object that was received over the wire.
	 * 
	 * @param action
	 *            sent from a remote machine (or possibly from the localhost)
	 */
	public void receivedAction(R3Action action) {
		action.invoke();
	}

	/**
	 * @param line
	 *            a line of text sent over the wire. We just print it out. This is useful for
	 *            debugging the Action Sender/Receiver architecture.
	 */
	public void receivedActionText(String line) {
		System.out.println(line);
	}
}
