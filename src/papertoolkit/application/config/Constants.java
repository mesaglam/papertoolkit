package papertoolkit.application.config;

/**
 * <p>
 * Put some project-wide, commonly used constants here. For example, all the ports that are used for socket
 * communication.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Constants {

	public static class Ports {
		public static final int ACROBAT_SERVER = 8888;

		public static final int ACTION_RECEIVER_JAVA = 11035;
		public static final int ACTION_RECEIVER_PLAINTEXT = 11036;

		public static final int BATCH_SERVER = 9999;
		
		public static final int FLASH_COMMUNICATION_SERVER = 8545;

		public static final int HANDWRITING_RECOGNITION = 9898;

		public static final int PEN_SERVER_JAVA = 11025;
		public static final int PEN_SERVER_PLAINTEXT = 11026;
		
		public static final int SIDE_CAR_COMMUNICATIONS = 43210;
	}
}
