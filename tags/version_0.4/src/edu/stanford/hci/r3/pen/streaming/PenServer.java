package edu.stanford.hci.r3.pen.streaming;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.data.PenServerJavaObjectXMLSender;
import edu.stanford.hci.r3.pen.streaming.data.PenServerPlainTextSender;
import edu.stanford.hci.r3.pen.streaming.data.PenServerSender;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.networking.ClientServerType;

/**
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
	 * 
	 */
	private class ServerThread implements Runnable {

		private void log(String msg) {
			DebugUtils.printlnWithStackOffset(msg, 1);
		}

		public void run() {
			while (true) {
				Socket s = null;

				if (exitFlag) {
					log("Closing Pen Server.");
					break;
				}

				try {
					if (serverType == ClientServerType.PLAINTEXT) {
						log("Waiting for a plain text connection on port "
								+ serverSocket.getLocalPort() + "...");
					} else { // serverType == Java Server
						log("Waiting for a java connection on port " + serverSocket.getLocalPort()
								+ "...");
					}
					s = serverSocket.accept();
					log("Got a connection on port " + serverSocket.getLocalPort() + "...");
					log("Client IP Addr is " + s.getRemoteSocketAddress());
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
	public static final int DEFAULT_JAVA_PORT = 11025;

	public static final int DEFAULT_PLAINTEXT_PORT = 11026;

	public static final String DEFAULT_SERIAL_PORT = "COM5";

	private static PenServer javaPenServer;

	private static PenServer textPenServer;

	/**
	 * @return
	 */
	public static boolean javaServerStarted() {
		return javaPenServer != null;
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
		String serialPortName = DEFAULT_SERIAL_PORT;
		int tcpipPortJava = DEFAULT_JAVA_PORT;
		int tcpipPortPlainText = DEFAULT_PLAINTEXT_PORT;

		if (args.length >= 1) {
			if (args[0].equals("?")) {
				System.out
						.println("Usage: PenServer [Serial Port] [TCP/IP Port for Java] [TCP/IP Port for Plain Text]");
				System.exit(0);
			} else {
				serialPortName = args[0];
			}
		}

		if (args.length >= 2) {
			tcpipPortJava = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			tcpipPortPlainText = Integer.parseInt(args[2]);
		}

		startBothServers(serialPortName, tcpipPortJava, tcpipPortPlainText);
	}

	/**
	 * Provides default implementation. It's unclear we want two servers going at the same time.
	 * Won't performance be better if we only send one stream of data? Also, what about Multicast?
	 * Then, multiple clients can listen in very easily. However, we'd need a server that will dole
	 * out the multicast address... This is simpler for now.
	 */
	public static void startBothServers(String serialPortName, int tcpipPortJava,
			int tcpipPortPlainText) {
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
	 * Start a Java server on this machine at the corresponding TCP/IP port. Add the java server as
	 * a listener to the local pen connection (at the specified COM port).
	 * 
	 * @param tcpipPort
	 */
	public static void startJavaServer(String serialPortName, int tcpipPort) {
		try {
			// TODO: At some point, provide access to this variable, so we can close a pen
			// connection if necessary
			final PenStreamingConnection penConnection = PenStreamingConnection
					.getInstance(serialPortName);
			if (penConnection == null) {
				System.err
						.println("The PenServer could not connect to the local serial port. Is your Bluetooth Dongle unplugged?");
				return;
			}

			final ServerSocket javaServer = new ServerSocket(tcpipPort);
			javaPenServer = new PenServer(javaServer, ClientServerType.JAVA);
			penConnection.addPenListener(javaPenServer);
		} catch (IOException ioe) {
			log("Error with server socket: " + ioe.getLocalizedMessage());
		}
	}

	/**
	 * 
	 */
	public static void startTextServer() {
		startTextServer(DEFAULT_SERIAL_PORT, DEFAULT_PLAINTEXT_PORT);
	}

	/**
	 * @param tcpipPort
	 */
	public static void startTextServer(String serialPortName, int tcpipPort) {
		try {
			final PenStreamingConnection penConnection = PenStreamingConnection
					.getInstance(serialPortName);
			final ServerSocket textServer = new ServerSocket(tcpipPort);
			textPenServer = new PenServer(textServer, ClientServerType.PLAINTEXT);
			penConnection.addPenListener(textPenServer);
		} catch (IOException ioe) {
			log("Error with server socket: " + ioe.getLocalizedMessage());
		}
	}

	/**
	 * 
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
	 * @return
	 */
	public static boolean textServerStarted() {
		return textPenServer != null;
	}

	private boolean exitFlag = false;

	private List<PenServerSender> outputs;

	/**
	 * Is the pen currently UP (not touching a patterned page)
	 */
	private boolean penUp = true;

	private ServerSocket serverSocket;

	private ClientServerType serverType;

	/**
	 * @param ss
	 * @param type
	 */
	public PenServer(ServerSocket ss, ClientServerType type) {
		serverSocket = ss;
		serverType = type;
		outputs = new ArrayList<PenServerSender>();

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
	 * Since with a PenListener... a penDown event NEVER overlaps with a penSample event, we now
	 * must send a sample over the wire for penDown events too!
	 * 
	 * @created Jun 12, 2006
	 * @author Ron Yeh
	 */
	public void penDown(PenSample s) {
		penUp = false;
		sample(s);
	}

	/**
	 * @created Jun 12, 2006
	 * @author Ron Yeh
	 */
	public void penUp(PenSample s) {
		penUp = true;
		// log("Pen UP Detected by Server " + serverType);
		sample(s);
	}

	/**
	 * @see edu.stanford.hci.r3.pen.streaming.listeners.PenListener#sample(edu.stanford.hci.r3.pen.PenSample)
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
	 * 
	 */
	private void stopServer() {
		try {
			log("PenServer::" + serverType + " on port " + serverSocket.getLocalPort()
					+ " is stopping...");
			exitFlag = true;
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
