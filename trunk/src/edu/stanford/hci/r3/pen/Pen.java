package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.util.communications.COMPort;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * This class represents a single, physical pen. A pen has an identity, so you should be able to
 * distinguish them. Pens can batch data for later upload. Alternatively, they can stream live data
 * when connected in a streaming mode.
 */
public class Pen {

	public static final COMPort DEFAULT_COM_PORT = COMPort.COM5;

	/**
	 * TRUE if the Pen object is currently connected to the physical pen in streaming mode.
	 */
	private boolean liveMode = false;

	public Pen() {
	}

	/**
	 * Connects to the pen connection on the local machine, with the default com port.
	 */
	public void startLiveMode() {
		startLiveMode("localhost", DEFAULT_COM_PORT);
	}

	/**
	 * @param hostDomainNameOrIPAddr
	 * @param port
	 */
	public void startLiveMode(String hostDomainNameOrIPAddr, COMPort port) {

		// Set Up Connection Here

		// fail --> liveMode == false

		// success --> liveMode == true
		liveMode = true;
	}
}
