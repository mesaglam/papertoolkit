package edu.stanford.hci.r3.pen.streaming;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.pen.streaming.data.PenServerOutput;
import edu.stanford.hci.r3.pen.streaming.data.ServerOutputJavaObjectXML;
import edu.stanford.hci.r3.pen.streaming.data.ServerOutputPlainText;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 * @author Joel Brandt
 */
public class PenServer implements PenListener {

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
	 * @param tcpipPort
	 */
	public static void startJavaServer(String serialPortName, int tcpipPort) {
		try {
			final PenStreamingConnection penConnection = PenStreamingConnection
					.getInstance(serialPortName);
			final ServerSocket javaServer = new ServerSocket(tcpipPort);
			javaPenServer = new PenServer(javaServer, ClientServerType.JAVA);
			penConnection.addPenListener(javaPenServer);
		} catch (IOException ioe) {
			System.out.println("Error with server socket: " + ioe.getLocalizedMessage());
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
			System.out.println("Error with server socket: " + ioe.getLocalizedMessage());
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

	private List<PenServerOutput> outputs;

	/**
	 * Is the pen currently UP (not touching a patterned page)
	 */
	private boolean penUp = true;

	private ClientServerType serverType;

	private ServerSocket ss;

	public PenServer(ServerSocket ss, ClientServerType type) {
		this.ss = ss;
		this.serverType = type;
		outputs = new ArrayList<PenServerOutput>();

		// start thread to accept connections
		new Thread() {
			public void run() {
				while (true) {
					Socket s = null;

					if (exitFlag) {
						System.out.println("Closing Pen Server.");
						break;
					}

					try {
						if (PenServer.this.serverType == ClientServerType.PLAINTEXT) {
							System.out.println("Waiting for a plain text connection on port "
									+ PenServer.this.ss.getLocalPort() + "...");
						} else { // serverType == Java Server
							System.out.println("Waiting for a java connection on port "
									+ PenServer.this.ss.getLocalPort() + "...");
						}
						s = PenServer.this.ss.accept();
						System.out.println("Got a connection on port " + PenServer.this.ss.getLocalPort()
								+ "...");
					} catch (IOException ioe) {
						System.out.println("Error with server socket: " + ioe.getLocalizedMessage());
					}
					if (s != null) {
						try {
							if (PenServer.this.serverType == ClientServerType.PLAINTEXT) {
								outputs.add(new ServerOutputPlainText(s));
							} else { // serverType == Java Server
								outputs.add(new ServerOutputJavaObjectXML(s));
							}
						} catch (IOException ioe) {
							try {
								s.close();
							} catch (IOException ioe2) {
								System.out.println("Error with server socket: "
										+ ioe2.getLocalizedMessage());
							}
							System.out.println("Error creating output: " + ioe.getLocalizedMessage());
						}
					}

				}
			}
		}.start();

	}

	/**
	 * @return is the pen currently up?
	 */
	public boolean isPenUp() {
		return penUp;
	}

	/**
	 * @created Jun 12, 2006
	 * @author Ron Yeh
	 */
	public void penDown(PenSample s) {
		penUp = false;
	}

	/**
	 * @created Jun 12, 2006
	 * @author Ron Yeh
	 * 
	 * PenServer/Clients DO send samples on penUp. This is in keeping with PenEventGenerator. We
	 * might fix this later on.
	 */
	public void penUp(PenSample s) {
		penUp = true;
		// System.out.println("Pen UP Detected by Server " + serverType);
		sample(s);
	}

	/**
	 * @see edu.stanford.hci.r3.pen.streaming.PenListener#sample(edu.stanford.hci.r3.pen.streaming.PenSample)
	 */
	public void sample(PenSample sample) {
		final List<PenServerOutput> toRemove = new ArrayList<PenServerOutput>();

		for (PenServerOutput out : outputs) {
			try {
				out.sendSample(sample);
			} catch (IOException ioe) {
				System.out
						.println("Error sending sample, removing output " + ioe.getLocalizedMessage());
				toRemove.add(out);
			}
		}

		for (PenServerOutput penServerOutput : toRemove) {
			penServerOutput.destroy();
			outputs.remove(penServerOutput);
		}

	}

	/**
	 * 
	 */
	private void stopServer() {
		try {
			System.out.println("PenServer::" + serverType + " on port " + ss.getLocalPort()
					+ " is stopping...");
			exitFlag = true;
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
