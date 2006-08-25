package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.pen.streaming.*;
import edu.stanford.hci.r3.util.DebugUtils;
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
 * 
 * The Pen object abstracts the lower level connections with the streaming server/client, and
 * dealing with batched ink input. It also interfaces with event handling in the system.
 */
public class Pen {

	/**
	 * Do something with this. TODO: Make PenStreamingConnection use this instead of a String.
	 */
	public static final COMPort DEFAULT_COM_PORT = COMPort.COM5;

	/**
	 * TRUE if the Pen object is currently connected to the physical pen in streaming mode.
	 */
	private boolean liveMode = false;

	private PenClient livePenClient;

	public Pen() {
	}

	/**
	 * Adds a low-level pen data listener to the live pen.
	 * 
	 * @param penListener
	 */
	public void addLivePenListener(PenListener penListener) {
		if (livePenClient == null) {
			DebugUtils.println("Pen is not in Live Mode.");
			return;
		}
		livePenClient.addPenListener(penListener);
	}

	/**
	 * @return
	 */
	public boolean isLive() {
		return liveMode;
	}

	/**
	 * Connects to the pen connection on the local machine, with the default com port. This will
	 * ensure the PenServer on the local machine is running.
	 */
	public void startLiveMode() {
		startLiveMode("localhost");
	}

	/**
	 * Set up connection to the pen server. The pen server is mapped to a physical pen attached to a
	 * some computer somewhere in the world. Starting livemode on a pen object just "attaches" it to
	 * an external server.
	 * 
	 * @param hostDomainNameOrIPAddr
	 */
	public void startLiveMode(String hostDomainNameOrIPAddr) {
		if (hostDomainNameOrIPAddr.equals("localhost")) {
			// ensure that a java server has been started on this machine
			if (!PenServer.javaServerStarted()) {
				PenServer.startJavaServer();
			}
		}

		livePenClient = new PenClient(hostDomainNameOrIPAddr, PenServer.DEFAULT_JAVA_PORT,
				ClientServerType.JAVA);
		livePenClient.connect();
		liveMode = true;
	}
}
