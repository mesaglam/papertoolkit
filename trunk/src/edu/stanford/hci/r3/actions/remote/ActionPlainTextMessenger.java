package edu.stanford.hci.r3.actions.remote;

import java.net.Socket;

import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * Useful for debugging. Sends actions as text.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionPlainTextMessenger extends ActionMessenger {

	/**
	 * @param sock
	 */
	public ActionPlainTextMessenger(Socket sock) {
		super(sock);
	}

	/**
	 * @param action
	 * @return
	 */
	public byte[] getMessage(R3Action action) {
		return (action.toString() + LINE_SEPARATOR).getBytes();
	}
}
