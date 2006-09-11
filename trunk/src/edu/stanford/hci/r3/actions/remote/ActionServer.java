package edu.stanford.hci.r3.actions.remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.actions.remote.transport.ActionJavaObjectXMLMessenger;
import edu.stanford.hci.r3.actions.remote.transport.ActionMessenger;
import edu.stanford.hci.r3.actions.remote.transport.ActionPlainTextMessenger;
import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * More abstract than the PenServer/Client infrastructure, this sends Action objects over the
 * wire...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionServer {

	/**
	 * 
	 */
	public static final int DEFAULT_JAVA_PORT = 11035;

	/**
	 * 
	 */
	public static final int DEFAULT_PLAINTEXT_PORT = 11036;

	/**
	 * 
	 */
	private static ActionServer javaServer;

	/**
	 * 
	 */
	private static ActionServer textServer;

	/**
	 * Use this method to notify remote listeners... Remote clients will get this action, and invoke
	 * it as soon as possible.
	 * 
	 * @param action
	 */
	public static void relayRemoteActionToServers(R3Action action) {
		if (javaServer != null) {
			javaServer.invokeRemoteAction(action);
		} else {
			// System.out.println("Not sending to the java server, as it's null.");
		}
		if (textServer != null) {
			textServer.invokeRemoteAction(action);
		} else {
			// System.out.println("Not sending to the plain text server, as it's null.");
		}
	}

	/**
	 * Unlike the pen servers, it's probably OK to start a java and text server
	 */
	public static void startServers() {
		startServers(DEFAULT_JAVA_PORT, DEFAULT_PLAINTEXT_PORT);
	}

	/**
	 * @param tcpipPortJava
	 * @param tcpipPortPlainText
	 */
	public static void startServers(int tcpipPortJava, int tcpipPortPlainText) {
		if (javaServer == null) {
			try {
				final ServerSocket ss = new ServerSocket(tcpipPortJava);
				javaServer = new ActionServer(ss, ClientServerType.JAVA);
			} catch (IOException ioe) {
				System.out.println("Error with server socket: " + ioe.getLocalizedMessage());
				System.exit(-1);
			}
		}

		if (textServer == null) {
			try {
				final ServerSocket ss = new ServerSocket(tcpipPortPlainText);
				textServer = new ActionServer(ss, ClientServerType.PLAINTEXT);
			} catch (IOException ioe) {
				System.out.println("Error with server socket: " + ioe.getLocalizedMessage());
				System.exit(-1);
			}
		}
	}

	/**
	 * Kill one or both servers.
	 */
	public static void stopServers() {
		DebugUtils.println("Stopping Action Servers.");
		if (javaServer != null) {
			javaServer.stopServer();
			javaServer = null;
		}

		if (textServer != null) {
			textServer.stopServer();
			textServer = null;
		}
	}

	/**
	 * Close the action server if this is ever set to true.
	 */
	private boolean exitFlag = false;

	/**
	 * The queue of output messages that we need to send.
	 */
	private List<ActionMessenger> outputs;

	/**
	 * The port we are listening to for client connections.
	 */
	private int serverPort;

	/**
	 * 
	 */
	private ServerSocket serverSocket;

	/**
	 * Is this a java or plain text server?
	 */
	private ClientServerType serverType;

	/**
	 * @param ss
	 * @param type
	 */
	public ActionServer(ServerSocket ss, ClientServerType type) {
		serverSocket = ss;
		serverType = type;
		outputs = new ArrayList<ActionMessenger>();

		// the server port
		serverPort = serverSocket.getLocalPort();

		// start thread to accept connections
		getServerThread().start();
	}

	/**
	 * @return
	 */
	private Thread getServerThread() {
		return new Thread() {
			public void run() {
				while (true) {
					Socket client = null;
					try {
						if (exitFlag) {
							System.out.println("Closing Action Server.");
							break;
						}

						log("ActionServer:: Waiting for a " + serverType + " connection on port ["
								+ serverPort + "]");

						client = serverSocket.accept();

						// we got a connection with the client
						log("ActionServer:: Got a connection on server port " + serverPort);
						log("               from client: " + client.getInetAddress());
					} catch (IOException ioe) {
						log("ActionServer:: Error with server socket: " + ioe.getLocalizedMessage());
					}

					if (client != null) {
						try {
							if (serverType == ClientServerType.PLAINTEXT) {
								outputs.add(new ActionPlainTextMessenger(client));
							} else { // serverType == Java Server
								outputs.add(new ActionJavaObjectXMLMessenger(client));
							}
						} catch (IOException ioe) {
							try {
								client.close();
							} catch (IOException ioe2) {
								log("ActionServer::Error with socket connection: "
										+ ioe2.getLocalizedMessage());
							}
							log("Error creating output: " + ioe.getLocalizedMessage());
						}
					}
				}
			}
		};
	}

	/**
	 * 
	 * @param action
	 */
	public void invokeRemoteAction(R3Action action) {
		final List<ActionMessenger> toRemove = new ArrayList<ActionMessenger>();

		for (ActionMessenger aso : outputs) {
			try {
				aso.sendAction(action);
			} catch (IOException ioe) {
				System.out.println("Error sending action, removing output "
						+ ioe.getLocalizedMessage());
				toRemove.add(aso);
			}
		}

		for (ActionMessenger aso : toRemove) {
			aso.destroy();
			outputs.remove(aso);
		}

	}

	/**
	 * To make the code in this class look a little cleaner.
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println(msg);
	}

	/**
	 * Tell the server to stop sending actions.
	 */
	private void stopServer() {
		try {
			System.out.println("ActionServer::" + serverType + " on port "
					+ serverSocket.getLocalPort() + " is stopping...");
			exitFlag = true;
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
