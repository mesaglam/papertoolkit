package papertoolkit.pen.streaming.data;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import papertoolkit.pen.PenSample;
import papertoolkit.util.SystemUtils;


/**
 * <p>
 * Sends plain text pen samples. Great for debugging.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenServerPlainTextSender implements PenServerSender {

	private Socket socket;

	private BufferedOutputStream bos;

	/**
	 * @param sock
	 * @throws IOException
	 */
	public PenServerPlainTextSender(Socket sock) throws IOException {
		socket = sock;
		bos = new BufferedOutputStream(sock.getOutputStream());
	}

	/**
	 * @see papertoolkit.pen.streaming.data.PenServerSender#sendSample(papertoolkit.pen.PenSample)
	 */
	public void sendSample(PenSample as) throws IOException {
		bos.write((as.toCommaSeparatedString() + SystemUtils.LINE_SEPARATOR).getBytes());
		bos.flush();
	}

	/**
	 * @see papertoolkit.pen.streaming.data.PenServerSender#destroy()
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
