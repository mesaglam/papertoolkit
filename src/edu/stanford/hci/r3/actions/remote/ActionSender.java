package edu.stanford.hci.r3.actions.remote;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.networking.ClientServerType;

/**
 * <p>
 * Create this object, and connect it to a remote ActionReceiver (which is a server). Then, whenever
 * you invoke a remote action, this will send the action across the wire to the ActionReceiver, so
 * that the receiver can invoke the action on its local machine.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionSender {

	/**
	 * Send the messages through this output object.
	 */
	private ActionMessenger messengerOutput;

	/**
	 * Our client socket through which we can talk to the ActionReceiver.
	 */
	private Socket socket;

	/**
	 * @param serverNameOrIPAddr
	 * @param port
	 * @param type
	 */
	public ActionSender(String serverNameOrIPAddr, int port, ClientServerType type) {
		try {
			socket = new Socket(serverNameOrIPAddr, port);
			if (type == ClientServerType.PLAINTEXT) {
				messengerOutput = new ActionPlainTextMessenger(socket);
			} else {
				messengerOutput = new ActionJavaObjectXMLMessenger(socket);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public synchronized void disconnect() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param action
	 */
	public void invokeRemoteAction(R3Action action) {
		try {
			messengerOutput.sendAction(action);
		} catch (IOException ioe) {
			System.out.println("Error sending action. Closing messenger output. "
					+ ioe.getLocalizedMessage());
		}
	}

}
