package papertoolkit.external;

/**
 * <p>
 * Listens for commands coming from flash.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public interface ExternalListener {
	
	public static final boolean CONSUMED = true;
	public static final boolean NOT_CONSUMED = false;
	
	/**
	 * @param command
	 * @return if the event was "consumed" If so, do not process it anymore...
	 */
	public boolean messageReceived(String command, String...args);
}
