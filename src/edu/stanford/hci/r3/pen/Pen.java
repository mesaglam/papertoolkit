package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.pen.streaming.PenClient;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenServer;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.communications.COMPort;

/**
 * <p>
 * This class represents a single, physical pen. A pen has an identity, so you should be able to
 * distinguish them. Pens can batch data for later upload. Alternatively, they can stream live data
 * when connected in a streaming mode.
 * </p>
 * <p>
 * The Pen object abstracts the lower level connections with the streaming server/client, and
 * dealing with batched ink input. It also interfaces with event handling in the system.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Pen {

	/**
	 * Do something with this.
	 * 
	 * TODO: Make PenStreamingConnection use this instead of a String.
	 */
	public static final COMPort DEFAULT_COM_PORT = COMPort.COM5;

	/**
	 * The local machine.
	 */
	private static final String LOCALHOST = "localhost";

	/**
	 * Where to connect when we go live.
	 */
	private String defaultPenServer;

	/**
	 * TRUE if the Pen object is currently connected to the physical pen in streaming mode.
	 */
	private boolean liveMode = false;

	/**
	 * A client listens to the Pen Server, which is the physical pen attached to SOME computer
	 * SOMEWHERE in the world. The Pen Server can be in a remote location, as long as it is DNS
	 * addressable.
	 */
	private PenClient livePenClient;

	/**
	 * A simple default name.
	 */
	private String name;

	/**
	 * Can't use this constructor too many times, because you can only have ONE physical pen
	 * connected to the localhost's pen server. However, you can have two pen objects listen to the
	 * same localhost server if you wish. They will just get the same data.
	 */
	public Pen() {
		this("A Pen");
	}

	/**
	 * @param name
	 *            for debugging purposes
	 */
	public Pen(String name) {
		this(name, LOCALHOST);
	}

	/**
	 * @param name
	 *            name the pen so you can identify it later
	 * @param penServerHostName
	 */
	public Pen(String name, String penServerHostName) {
		setName(name);
		defaultPenServer = penServerHostName;
	}

	/**
	 * Adds a low-level pen data listener to the live pen.
	 * 
	 * @param penListener
	 */
	public void addLivePenListener(PenListener penListener) {
		if (livePenClient == null) {
			DebugUtils.println("Cannot add this Listener. The Pen is not in Live Mode.");
			return;
		}
		livePenClient.addPenListener(penListener);
	}

	/**
	 * @return the name of this pen
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return if this pen in live mode.
	 */
	public boolean isLive() {
		return liveMode;
	}

	/**
	 * Removes the pen listener from the live pen.
	 * 
	 * @param penListener
	 */
	public void removeLivePenListener(PenListener penListener) {
		if (livePenClient == null) {
			DebugUtils.println("Cannot Remove the Listener. The Pen is not in Live Mode.");
		}
		livePenClient.removePenListener(penListener);
	}

	/**
	 * @param nomDePlume
	 *            Optional, for differentiating pens during debugging.
	 */
	private void setName(String nomDePlume) {
		name = nomDePlume;
	}

	/**
	 * Connects to the pen connection on the local machine, with the default com port. This will
	 * ensure the PenServer on the local machine is running. This will be called by the PaperToolkit
	 * when you start an application.
	 */
	public void startLiveMode() {
		startLiveMode(defaultPenServer);
	}

	/**
	 * Set up connection to the pen server. The pen server is mapped to a physical pen attached to a
	 * some computer somewhere in the world. Starting livemode on a pen object just "attaches" it to
	 * an external server.
	 * 
	 * @param hostDomainNameOrIPAddr
	 */
	public void startLiveMode(String hostDomainNameOrIPAddr) {
		if (hostDomainNameOrIPAddr.equals(LOCALHOST)) {
			// ensure that a java server has been started on this machine
			if (!PenServer.javaServerStarted()) {
				PenServer.startJavaServer();
			}
		}
		if (livePenClient == null && !isLive()) {
			livePenClient = new PenClient(hostDomainNameOrIPAddr, PenServer.DEFAULT_JAVA_PORT,
					ClientServerType.JAVA);
			livePenClient.connect();
			liveMode = true;
		} else {
			DebugUtils.println("Pen [" + getName() + "] is already live. "
					+ "You are trying to connect again.");
		}
	}

	/**
	 * Exit live mode.
	 */
	public void stopLiveMode() {
		livePenClient.disconnect();
		livePenClient = null;
		liveMode = false;
	}
}
