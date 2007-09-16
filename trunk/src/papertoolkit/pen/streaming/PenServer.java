package papertoolkit.pen.streaming;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.application.config.Constants;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.PenJitterFilter.PenUpCallback;
import papertoolkit.pen.streaming.data.PenServerJavaObjectXMLSender;
import papertoolkit.pen.streaming.data.PenServerPlainTextSender;
import papertoolkit.pen.streaming.data.PenServerSender;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.communications.COMPort;
import papertoolkit.util.networking.ClientServerType;

/**
 * <p>
 * The PenServer also implements a simple filtering to clean up stray penUps that may come from bad
 * pens/pattern.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 * @author Joel Brandt
 */
public class PenServer implements PenListener {
	/**
	 * Hangs out here, accepting multiple client connections...
	 */
	private class ServerThread implements Runnable {
		public void run() {
			while (true) {
				Socket s = null;
				if (exitFlag) {
					log("Closing Pen Server.");
					break;
				}
				try {
					if (serverType == ClientServerType.PLAINTEXT) {
						// log("Waiting for a plain text connection on port " + serverSocket.getLocalPort()
						// + "...");
					} else { // serverType == Java Server
						// log("Waiting for a java connection on port " + serverSocket.getLocalPort() +
						// "...");
					}
					s = serverSocket.accept();
					// log("Got a connection on port " + serverSocket.getLocalPort() + "...");
					// log("Client IP Addr is " + s.getRemoteSocketAddress());
				} catch (IOException ioe) {
					log("Error with server socket: " + ioe.getLocalizedMessage());
				}
				if (s != null) {
					try {
						if (serverType == ClientServerType.PLAINTEXT) {
							outputs.add(new PenServerPlainTextSender(s));
						} else { // serverType == Java Server
							outputs.add(new PenServerJavaObjectXMLSender(s));
						}
					} catch (IOException ioe) {
						try {
							s.close();
						} catch (IOException ioe2) {
							log("Error with server socket: " + ioe2.getLocalizedMessage());
						}
						log("Error creating output: " + ioe.getLocalizedMessage());
					}
				}
			}
		}
	}

	/**
	 * The default port to which pen clients can connect...
	 */
	public static final int DEFAULT_JAVA_PORT = Constants.Ports.PEN_SERVER_JAVA;

	/**
	 * The default debug port.
	 */
	public static final int DEFAULT_PLAINTEXT_PORT = Constants.Ports.PEN_SERVER_PLAINTEXT;

	/**
	 * By default, we connect to the pen on COM5 (works for Nokia pens)...
	 */
	public static final COMPort DEFAULT_SERIAL_PORT = COMPort.COM5;

	/**
	 * The default pen server sends java objects across the wire.
	 */
	private static PenServer javaPenServer;

	/**
	 * A connection to the local COM port.
	 */
	private static PenStreamingConnection penConnection;

	/**
	 * A debug pen server sends text across the wire.
	 */
	private static PenServer textPenServer;

	/**
	 * Set to true if we could not connect to the main java port. This means someone else has already started
	 * a local PenServer.
	 */
	private static boolean javaServerStartedBySomeoneElse = false;

	/**
	 * TODO: We may want to do the server started by someone else trick w/ the debug text server too...
	 * @return whether there is a local Java server running.
	 */
	public static boolean isJavaServerStarted() {
		return javaPenServer != null && javaServerStartedBySomeoneElse;
	}

	/**
	 * @return whether we have started a text server on the localhost (TODO: also, see isJavaServerStarted)
	 */
	public static boolean isTextServerStarted() {
		return textPenServer != null;
	}

	/**
	 * @param msg
	 */
	private static void log(String msg) {
		DebugUtils.printlnWithStackOffset(msg, 1);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// default to COM5, ports 11025 and 11026
		// you can specify these numbers through the arguments
		COMPort serialPort = DEFAULT_SERIAL_PORT;
		int tcpipPortJava = DEFAULT_JAVA_PORT;
		int tcpipPortPlainText = DEFAULT_PLAINTEXT_PORT;

		if (args.length >= 1) {
			if (args[0].equals("?")) {
				DebugUtils.println("Usage: PenServer [Serial Port] "
						+ "[TCP/IP Port for Java] [TCP/IP Port for Plain Text]");
				DebugUtils.println("Example: PenServer COM5 11025 11026");
				System.exit(0);
			} else {
				serialPort = COMPort.valueOf(args[0]);
			}
		}

		if (args.length >= 2) {
			tcpipPortJava = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			tcpipPortPlainText = Integer.parseInt(args[2]);
		}

		startBothServers(serialPort, tcpipPortJava, tcpipPortPlainText);
	}

	/**
	 * Provides default implementation. It's unclear we want two servers going at the same time. Won't
	 * performance be better if we only send one stream of data? Also, what about Multicast? Then, multiple
	 * clients can listen in very easily. However, we'd need a server that will dole out the multicast
	 * address... This is simpler for now.
	 */
	public static void startBothServers(COMPort serialPortName, int tcpipPortJava, int tcpipPortPlainText) {
		startJavaServer(serialPortName, tcpipPortJava);
		startTextServer(serialPortName, tcpipPortPlainText);
	}

	/**
	 * Provides default implementation. Only start the Java Server.
	 */
	public static void startJavaServer() {
		startJavaServer(DEFAULT_SERIAL_PORT, DEFAULT_JAVA_PORT);
	}

	/**
	 * Use the default java port...
	 * 
	 * @param serialPort
	 *            customize the COM port
	 */
	public static void startJavaServer(COMPort serialPort) {
		startJavaServer(serialPort, DEFAULT_JAVA_PORT);
	}

	/**
	 * Start a Java server on this machine at the corresponding TCP/IP port. Add the java server as a listener
	 * to the local pen connection (at the specified COM port).
	 * 
	 * @param tcpipPort
	 */
	public static void startJavaServer(COMPort serialPort, int tcpipPort) {
		try {
			final ServerSocket javaServer = new ServerSocket(tcpipPort);

			// provide access to this variable, so we can close a pen connection if necessary
			penConnection = PenStreamingConnection.getInstance(serialPort);
			if (penConnection == null) {
				DebugUtils.println("The PenServer could not connect to the local serial port.");
				return;
			}
			javaPenServer = new PenServer(javaServer, ClientServerType.JAVA);
			penConnection.addPenListener(javaPenServer);
		} catch (IOException ioe) {
			log("A Pen Server (or some other server) already exists at " + tcpipPort);
			log("We will try to connect to it....");
			javaServerStartedBySomeoneElse = true;
		}
	}

	/**
	 * Start at default ports...
	 */
	public static void startTextServer() {
		startTextServer(DEFAULT_SERIAL_PORT, DEFAULT_PLAINTEXT_PORT);
	}

	/**
	 * NOTE, you can only start one PenStreamingConnection at a time, on the local machine. Thus, if you have
	 * created on one COM5, you cannot create another one at COM6, until you kill the connection.
	 * 
	 * @param tcpipPort
	 */
	public static void startTextServer(COMPort serialPort, int tcpipPort) {
		try {
			penConnection = PenStreamingConnection.getInstance(serialPort);
			final ServerSocket textServer = new ServerSocket(tcpipPort);
			textPenServer = new PenServer(textServer, ClientServerType.PLAINTEXT);
			penConnection.addPenListener(textPenServer);
		} catch (IOException ioe) {
			log("Error with server socket: " + ioe.getLocalizedMessage());
		}
	}

	/**
	 * TODO: also reset the flag, so we can check if the javaServer port is owned again...
	 */
	public static void stopServers() {
		if (javaPenServer != null) {
			javaPenServer.stopServer();
			javaPenServer = null;
		}
		if (textPenServer != null) {
			textPenServer.stopServer();
			textPenServer = null;
		}
	}

	/**
	 * Helps us break from the while loop (above).
	 */
	private boolean exitFlag = false;

	/**
	 * Weed out the spurious PENUP events that some NOKIA SU-1B pens throw...
	 */
	private PenJitterFilter jitterFilter;

	/**
	 * Serializes pen samples and sends it across the wire.
	 */
	private List<PenServerSender> outputs;

	/**
	 * So we know when pen ups are valid...
	 */
	private boolean penDownHasHappened = false;

	/**
	 * Is the pen currently UP (not touching a patterned page)
	 */
	private boolean penUp = true;

	private ServerSocket serverSocket;

	/**
	 * TEXT or JAVA (default)
	 */
	private ClientServerType serverType;

	/**
	 * @param ss
	 * @param type
	 */
	public PenServer(ServerSocket ss, ClientServerType type) {
		serverSocket = ss;
		serverType = type;
		outputs = new ArrayList<PenServerSender>();
		jitterFilter = new PenJitterFilter(new PenUpCallback() {
			public void penUp(PenSample s) {
				penUp = true;
				sample(s);
			}
		});

		// start thread to accept connections
		new Thread(new ServerThread()).start();
	}

	/**
	 * @return is the pen currently up?
	 */
	public boolean isPenUp() {
		return penUp;
	}

	/**
	 * Since with a PenListener... a penDown event NEVER overlaps with a penSample event, we now must send a
	 * sample over the wire for penDown events too!
	 * 
	 * @created Jun 12, 2006
	 * @author Ron Yeh
	 */
	public void penDown(PenSample s) {
		penDownHasHappened = true;
		if (jitterFilter.happenedTooCloseToLastPenUp()) {
			jitterFilter.cancelLastPenUp();
		} else {
			penUp = false;
			sample(s);
		}
	}

	/**
	 * We should not fire a pen up sample if no pen down has happned... This implies (with the Nokia SU-1B, at
	 * least) that we are actually getting NO data at all...
	 * 
	 * @created Jun 12, 2006
	 * @author Ron Yeh
	 */
	public void penUp(PenSample s) {
		if (!penDownHasHappened) {
			// ignore the spurious pen up event...
			return;
		}
		penDownHasHappened = false;

		// let the filter to figure this out
		jitterFilter.triggerPenUpAfterADelay(s);
	}

	/**
	 * @see papertoolkit.pen.streaming.listeners.PenListener#sample(papertoolkit.pen.PenSample)
	 */
	public void sample(PenSample sample) {
		final List<PenServerSender> toRemove = new ArrayList<PenServerSender>();

		for (PenServerSender out : outputs) {
			try {
				out.sendSample(sample);
			} catch (IOException ioe) {
				log("Error sending sample, removing output " + ioe.getLocalizedMessage());
				toRemove.add(out);
			}
		}

		for (PenServerSender penServerOutput : toRemove) {
			penServerOutput.destroy();
			outputs.remove(penServerOutput);
		}
	}

	/**
	 * Kills the local Java or Text Pen server... It also asks the PenConnection class to stop listening to
	 * the COM port.
	 */
	private void stopServer() {
		try {
			log("PenServer::" + serverType + " on port " + serverSocket.getLocalPort() + " is stopping...");
			exitFlag = true;
			serverSocket.close();

			penConnection.exit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
