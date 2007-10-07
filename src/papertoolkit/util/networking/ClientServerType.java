package papertoolkit.util.networking;

/**
 * <p>
 * Used by the various R3 servers to determine whether it will get data as serialized java objects,
 * or plain text. For example, when we connect to a pen server, we can either stream Java objects
 * (serialized to XML) or just show plain text. Same with the ActionServer.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public enum ClientServerType {
	JAVA, 
	FLASH,
	PLAINTEXT
}
