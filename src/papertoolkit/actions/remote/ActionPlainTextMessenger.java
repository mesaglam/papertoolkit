package papertoolkit.actions.remote;

import java.net.Socket;

import papertoolkit.actions.R3Action;


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
	 * @param sock Communicate over this socket.
	 */
	public ActionPlainTextMessenger(Socket sock) {
		super(sock);
	}

	/**
	 * Just returns the string representation of this action.
	 * 
	 * @param action
	 * @return the toString() of this action object. Useful for debugging.
	 */
	public byte[] getMessage(R3Action action) {
		return (action.toString() + LINE_SEPARATOR).getBytes();
	}
}
