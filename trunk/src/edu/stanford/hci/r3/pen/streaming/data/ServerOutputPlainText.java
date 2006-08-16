package edu.stanford.hci.r3.pen.streaming.data;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 */
public class ServerOutputPlainText implements PenServerOutput {

	private Socket socket;

	private BufferedOutputStream bos;

	/**
	 * @param sock
	 * @throws IOException
	 */
	public ServerOutputPlainText(Socket sock) throws IOException {
		socket = sock;
		bos = new BufferedOutputStream(sock.getOutputStream());
	}

	/**
	 * @see edu.stanford.hci.r3.pen.streaming.data.PenServerOutput#sendSample(edu.stanford.hci.r3.pen.streaming.PenSample)
	 */
	public void sendSample(PenSample as) throws IOException {
		bos.write((as.toCommaSeparatedString() + SystemUtils.LINE_SEPARATOR).getBytes());
		bos.flush();
	}

	/**
	 * @see edu.stanford.hci.r3.pen.streaming.data.PenServerOutput#destroy()
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
}
