package papertoolkit.actions.remote;

import java.net.Socket;

import papertoolkit.PaperToolkit;
import papertoolkit.actions.Action;


/**
 * <p>
 * Sends an R3 Action as a Java object serialized to xml over a socket.
 * </p>
 * <p>
 * TODO: Seems like we could integrate this with PenServer's equivalent classes. How do we make it generic so
 * that we can send an object over and either consider it a pen sample, or an action? Might be simpler to keep
 * them separate for now. There are some issues with the way we serialize (removing spaces, et cetera).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionJavaObjectXMLMessenger extends ActionMessenger {

	/**
	 * @param s Communicate over this socket
	 */
	public ActionJavaObjectXMLMessenger(Socket s) {
		super(s);
	}

	/**
	 * Turns an R3Action into an xml string and then into the bytes we need to send. The limitation is that
	 * actions get turned into a single line of XML text. You cannot send anything with a \n, as it gets
	 * replaced with an empty string.
	 * 
	 * @see papertoolkit.actions.remote.ActionMessenger#getMessage(papertoolkit.actions.Action)
	 */
	public byte[] getMessage(Action action) {
		// serialize the object, and put it all on one line
		String xmlString = PaperToolkit.toXML(action).replace("\n", "");

		// do not remove spaces!!!! as actions may have paths, which depend on spaces...
		return (xmlString + LINE_SEPARATOR).getBytes();
	}
}
