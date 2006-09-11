package edu.stanford.hci.r3.actions.remote.transport;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionPlainTextMessenger implements ActionMessenger {

	/**
	 * The OS specific newline string.
	 */
	private static final String LINE_SEPARATOR = SystemUtils.LINE_SEPARATOR;

	/**
	 * 
	 */
	private BufferedOutputStream bos;

	/**
	 * 
	 */
	private Socket socket;

	/**
	 * @param sock
	 * @throws IOException
	 */
	public ActionPlainTextMessenger(Socket sock) throws IOException {
		socket = sock;
		bos = new BufferedOutputStream(sock.getOutputStream());
	}

	/**
	 * @see edu.stanford.hci.r3.actions.remote.transport.ActionMessenger#destroy()
	 */
	public void destroy() {
		try {
			if (bos != null) {
				bos.close();
				bos = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException ioe) {
			System.out.println("Got exception when destroying PlainTextServerOutput: "
					+ ioe.getLocalizedMessage());
		}
	}

	/**
	 * @param action
	 * @throws IOException
	 */
	public void sendAction(R3Action action) throws IOException {
		bos.write((action.toString() + LINE_SEPARATOR).getBytes());
		bos.flush();
	}
}
