package edu.stanford.hci.r3.actions.remote;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.util.SystemUtils;

/**
 * <p>
 * TODO: Seems like we could integrate this with PenServer's equivalent classes. How do we make it
 * generic so that we can send an object over and either consider it a pen sample, or an action?
 * Might be simpler to keep them separate for now.
 * </p>
 * <p>
 * We can definitely at least integrate the messengers and the pen server output objects and move
 * them into the networking package. This is a TODO, but the interfaces should remain the same.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionJavaObjectXMLMessenger implements ActionMessenger {

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
	private Socket sock;

	/**
	 * 
	 */
	private XStream xml;

	/**
	 * @param s
	 * @throws IOException
	 */
	public ActionJavaObjectXMLMessenger(Socket s) throws IOException {
		sock = s;
		bos = new BufferedOutputStream(s.getOutputStream());
		xml = new XStream();
	}

	/**
	 * @see edu.stanford.hci.r3.actions.remote.ActionMessenger#destroy()
	 */
	public void destroy() {
		try {
			xml = null;

			if (bos != null) {
				bos.close();
				bos = null;
			}
			if (sock != null) {
				sock.close();
				sock = null;
			}
		} catch (IOException ioe) {
			System.out
					.println("ActionServerOutputJOXML::Got exception when destroying JavaServerOutput: "
							+ ioe.getLocalizedMessage());
		}
	}

	/**
	 * Removes \n from the string to send over the wire.
	 * 
	 * @see edu.stanford.hci.r3.actions.remote.ActionMessenger#sendAction(edu.stanford.hci.r3.actions.R3Action)
	 */
	public void sendAction(R3Action action) throws IOException {
		String xmlString = xml.toXML(action);

		if (xmlString.contains("\n")) {
			xmlString = xmlString.replace("\n", "");
		}

		// do not remove spaces!!!!
		// as that is not appropriate for actions, which may have paths, which depend on spaces...
		// if (xmlString.contains(" ")) {
		// xmlString = xmlString.replace(" ", "");
		// }

		// System.out.println(xmlString);

		bos.write((xmlString + LINE_SEPARATOR).getBytes());
		bos.flush();
	}

}
