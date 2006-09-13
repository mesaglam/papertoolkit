package edu.stanford.hci.r3.actions.remote;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * Given an R3Action, the messenger will package it up and send it across the wire, so that the
 * receiver can perform the designated action.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class ActionMessenger {

	/**
	 * The OS-specific newline string.
	 */
	protected static final String LINE_SEPARATOR = SystemUtils.LINE_SEPARATOR;

	/**
	 * Write data through this output stream.
	 */
	private BufferedOutputStream bos;

	/**
	 * Destination Socket.
	 */
	private Socket sock;

	/**
	 * @param s
	 */
	public ActionMessenger(Socket s) {
		try {
			sock = s;
			bos = new BufferedOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean up resources.
	 */
	public void destroy() {
		try {
			if (bos != null) {
				bos.close();
				bos = null;
			}
			if (sock != null) {
				sock.close();
				sock = null;
			}
		} catch (IOException ioe) {
			DebugUtils.println("Got exception when destroying messenger: "
					+ ioe.getLocalizedMessage());
		}
	}

	/**
	 * Get the byte array to send across the socket.
	 * 
	 * @param action
	 * @return
	 */
	public abstract byte[] getMessage(R3Action action);

	/**
	 * Sends the bytes over the wire.
	 * 
	 * @param action
	 */
	public void sendAction(R3Action action) {
		try {
			bos.write(getMessage(action));
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
