package papertoolkit.pen;

import papertoolkit.PaperToolkit;
import papertoolkit.pen.streaming.PenClient;
import papertoolkit.pen.streaming.PenServer;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.communications.COMPort;
import papertoolkit.util.networking.ClientServerType;

/**
 * <p>
 * This class represents a single, physical digital pen. A pen has an identity, so you should be able to
 * distinguish them. Pens can batch data for later upload. Alternatively, they can stream live data when
 * connected in a streaming mode.
 * </p>
 * <p>
 * The Pen object abstracts the lower level connections with the streaming server/client, and dealing with
 * batched ink input. It also interfaces with event handling in the system.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Pen extends InputDevice {

	/**
	 * PenStreamingConnection uses this to determine which COM port to connect to when looking for pen data.
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
	 * A client listens to the Pen Server, which is the physical pen attached to SOME computer SOMEWHERE in
	 * the world. The Pen Server can be in a remote location, as long as it is DNS addressable.
	 */
	private PenClient livePenClient;

	/**
	 * Defaults to COM5. This can be customized, as long as you do it before the pen server is started.
	 */
	private COMPort localPenComPort = DEFAULT_COM_PORT;

	/**
	 * The port that the local or remote pen server is listening on...
	 */
	private int penServerTcpIpPort = PenServer.DEFAULT_JAVA_PORT;

	/**
	 * Can't use this constructor more than once, because you can only have ONE physical pen connected to the
	 * localhost's pen server. However, you can have two pen objects listen to the same localhost server if
	 * you wish. They will just get the same data.
	 */
	public Pen() {
		this("A Pen");
	}

	/**
	 * @param name
	 *            for debugging purposes, so you can identify pens by name
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
		super(name);
		defaultPenServer = penServerHostName;
	}

	/**
	 * @param name
	 * @param penServerHostName
	 * @param penServerPort
	 */
	public Pen(String name, String penServerHostName, int penServerPort) {
		this(name, penServerHostName);
		setPenServerPort(penServerPort);
	}

	/**
	 * This is the quick-and-dirty approach to working with streaming pens. If you add a listener, you will be
	 * able to test the pen and get pattern coordinates. However, you will soon notice that the pattern
	 * coordinates are in a really large coordinate space, which may be difficult to keep track of. How will
	 * you map these coordinates into coordinates that make sense for your application? That's what the
	 * PaperToolkit's Application infrastructure will help you to accomplish.
	 * 
	 * @see papertoolkit.pen.PenInput#addLivePenListener(papertoolkit.pen.streaming.listeners.PenListener)
	 */
	public void addLivePenListener(PenListener penListener) {
		// keep one around in the cache if the pen is not currently live
		super.addLivePenListener(penListener);
		
		if (isLive()) {
			// if we're live, then listen to the PenClient
			livePenClient.addPenListener(penListener);
		}
	}

	/**
	 * @return
	 */
	public String getPenServerName() {
		return defaultPenServer;
	}

	/**
	 * @return
	 */
	public int getPenServerPort() {
		return penServerTcpIpPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pen.PenInput#removeLivePenListener(edu.stanford.hci.r3.pen.streaming.listeners.PenListener)
	 */
	public void removeLivePenListener(PenListener penListener) {
		super.removeLivePenListener(penListener);
		if (isLive()) {
			livePenClient.removePenListener(penListener);
		}
	}

	/**
	 * Customize the COM port, before going live... This will only have an effect on the local pen, as you
	 * can't really tell the remote pen which port to connect on. For that, you should customize the PenServer
	 * or PenServerTrayApp directly.
	 * 
	 * @param port
	 */
	public void setLocalComPort(COMPort port) {
		localPenComPort = port;
	}

	/**
	 * Customize the port with which you will connect to the PenServer.
	 * 
	 * @param tcpipPort
	 */
	public void setPenServerPort(int tcpipPort) {
		penServerTcpIpPort = tcpipPort;
	}

	/**
	 * Connects to the pen connection on the local machine, with the default com port. This will ensure the
	 * PenServer on the local machine is running. This will be called by the PaperToolkit when you start an
	 * application.
	 */
	public void startLiveMode() {
		PaperToolkit.init(); // touch the PaperToolkit class, to initialize the system tray, etc..
		startLiveMode(defaultPenServer);
	}

	/**
	 * Set up connection to the pen server. The pen server is mapped to a physical pen attached to a some
	 * computer somewhere in the world. Starting livemode on a pen object just "attaches" it to an external
	 * server.
	 * 
	 * @param hostDomainNameOrIPAddr
	 */
	public void startLiveMode(String hostDomainNameOrIPAddr) {
		if (liveMode) {
			// already started
			return;
		}

		// if the pen is on the local host...
		// ensure that a java server has been started on this machine
		if (hostDomainNameOrIPAddr.equals(LOCALHOST)) {
			if (!PenServer.javaServerStarted()) {
				PenServer.startJavaServer(localPenComPort);
			}
		}

		// start a client to listen to the pen...
		if (livePenClient == null && !isLive()) {
			livePenClient = new PenClient(hostDomainNameOrIPAddr, penServerTcpIpPort, ClientServerType.JAVA);
			livePenClient.connect();
			liveMode = true;

			// add all the cached listeners now
			for (PenListener pl : penListenersToAdd) {
				// DebugUtils.println("Adding cached pen listeners...");
				livePenClient.addPenListener(pl);
			}
		} else {
			DebugUtils.println("Pen [" + getName() + "] is already live. " + "We cannot connect again.");
		}
	}

	/**
	 * Exit live mode.
	 */
	public void stopLiveMode() {
		if (!liveMode) {
			// already stopped
			return;
		}

		livePenClient.disconnect();
		livePenClient = null;
		liveMode = false;

		// if the server was started on the localhost, kill it too!
		if (PenServer.javaServerStarted()) {
			PenServer.stopServers();
		}
	}
}
