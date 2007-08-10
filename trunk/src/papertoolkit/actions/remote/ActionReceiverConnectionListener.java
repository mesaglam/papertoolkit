package papertoolkit.actions.remote;

/**
 * <p>
 * ActionReceiver uses this to listen for new client connections.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface ActionReceiverConnectionListener {

	/**
	 * Notifies the receiver of new connections.
	 * 
	 * @param hostName
	 *            client's host name (DNS)
	 * @param ipAddr
	 *            the client's ip address
	 */
	public void newConnectionFrom(String hostName, String ipAddr);
}
