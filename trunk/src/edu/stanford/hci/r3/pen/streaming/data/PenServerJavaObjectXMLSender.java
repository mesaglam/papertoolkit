package edu.stanford.hci.r3.pen.streaming.data;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * Sends Java XML objects representing Pen Samples over the wire.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenServerJavaObjectXMLSender implements PenServerSender {

	private BufferedOutputStream bos;

	private Socket sock;

	/**
	 * @param s
	 * @throws IOException
	 */
	public PenServerJavaObjectXMLSender(Socket s) throws IOException {
		sock = s;
		bos = new BufferedOutputStream(s.getOutputStream());
	}

	/**
	 * @see edu.stanford.hci.r3.pen.streaming.data.PenServerSender#destroy()
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
			System.out.println("Got exception when destroying JavaServerOutput: "
					+ ioe.getLocalizedMessage());
		}
	}

	/**
	 * Removes spaces and \n from the string to send over the wire.
	 * 
	 * @created Mar 31, 2006
	 * @author Ron Yeh
	 * 
	 * @see edu.stanford.hci.r3.pen.streaming.data.PenServerSender#sendSample(edu.stanford.hci.r3.pen.PenSample)
	 */
	public void sendSample(PenSample as) throws IOException {
		String xmlString = PaperToolkit.toXML(as);

		// remove line endings
		if (xmlString.contains("\n")) {
			xmlString = xmlString.replace("\n", "");
		}

		// remove spaces
		if (xmlString.contains(" ")) {
			xmlString = xmlString.replace(" ", "");
		}

		// System.out.println(xmlString);
		bos.write((xmlString + SystemUtils.LINE_SEPARATOR).getBytes());
		bos.flush();
	}

}
