/**
 * 
 */
package edu.stanford.hci.r3.actions.remote;

import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * A useful default, but you can override if you want.
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
	 * @param action
	 */
	public void receivedAction(R3Action action) {
		action.invoke();
	}

	/**
	 * @param line
	 */
	public void receivedActionText(String line) {
		System.out.println(line);
	}
}
