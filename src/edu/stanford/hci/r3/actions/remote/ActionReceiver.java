package edu.stanford.hci.r3.actions.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A daemon that sits and waits for actions to come in over the wire. It then invokes those actions.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionReceiver {

	/**
	 * Will listen on this port for Java XML objects.
	 */
	public static final int DEFAULT_JAVA_PORT = 11035;

	/**
	 * Will listen on this port for text commands.
	 */
	public static final int DEFAULT_PLAINTEXT_PORT = 11036;

	/**
	 * 
	 */
	private static ActionReceiver javaDaemon;

	/**
	 * 
	 */
	private static ActionReceiver textDaemon;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		startDaemons();
	}

	/**
	 * Unlike the pen servers, it's probably OK to start both a java and text server...
	 */
	public static void startDaemons() {
		startDaemons(DEFAULT_JAVA_PORT, DEFAULT_PLAINTEXT_PORT);
	}

	/**
	 * @param tcpipPortJava
	 * @param tcpipPortPlainText
	 */
	public static void startDaemons(int tcpipPortJava, int tcpipPortPlainText) {
		if (javaDaemon == null) {
			javaDaemon = new ActionReceiver(tcpipPortJava, ClientServerType.JAVA);
		}
		if (textDaemon == null) {
			textDaemon = new ActionReceiver(tcpipPortPlainText, ClientServerType.PLAINTEXT);
		}
	}

	/**
	 * Kill one or both servers.
	 */
	public static void stopDaemons() {
		DebugUtils.println("Stopping Action Receiver Daemons.");
		if (javaDaemon != null) {
			javaDaemon.stopServer();
			javaDaemon = null;
		}

		if (textDaemon != null) {
			textDaemon.stopServer();
			textDaemon = null;
		}
	}

	/**
	 * 
	 */
	private List<ActionHandler> actionHandlers = new ArrayList<ActionHandler>();

	/**
	 * 
	 */
	private List<Socket> clients = new ArrayList<Socket>();

	/**
	 * Close the action server if this is ever set to true.
	 */
	private boolean exitFlag = false;

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
	 * Will only accept actions from this list of remote senders.
	 */
	private ArrayList<String> trustedSenders = new ArrayList<String>();

	/**
	 * Will only accept actions from the localhost.
	 * 
	 * @param type
	 * @param tcpipPortPlainText
	 */
	public ActionReceiver(int tcpipPort, ClientServerType type) {
		this(tcpipPort, type, "localhost");
	}

	/**
	 * @param trusted
	 *            a list of IPs, DNSs, or (todo) subnets that we trust...
	 */
	public ActionReceiver(int tcpipPort, ClientServerType type, String... trusted) {
		trustedSenders.addAll(Arrays.asList(trusted));
		try {
			serverSocket = new ServerSocket(tcpipPort);
		} catch (IOException e) {
			System.out.println("Error with server socket: " + e.getLocalizedMessage());
		}
		serverType = type;

		// the server port
		serverPort = serverSocket.getLocalPort();

		// start thread to accept connections
		getServerThread().start();
	}

	/**
	 * @param ah
	 */
	public void addActionHandler(ActionHandler ah) {
		actionHandlers.add(ah);
	}

	/**
	 * @param clientSocket
	 * @return
	 */
	private Thread getJavaClientHandlerThread(final Socket clientSocket) {
		return new Thread() {
			private BufferedReader br;

			public void run() {
				try {
					final InputStream inputStream = clientSocket.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					String line = null;
					final XStream xml = new XStream();
					while ((line = br.readLine()) != null) {
						// System.out.println(line);

						if (exitFlag) {
							break;
						}

						// reconstruct the action
						final R3Action action = (R3Action) xml.fromXML(line);

						// tell my listeners!
						for (ActionHandler ah : actionHandlers) {
							ah.receivedAction(action);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * Waits for connections, and passes of connections to a client handler thread.
	 * 
	 * @return
	 */
	private Thread getServerThread() {
		return new Thread() {

			public void run() {
				while (true) {
					Socket client = null;
					try {
						if (exitFlag) {
							System.out.println("Closing Action Receiver Daemon.");
							break;
						}

						log("ActionServer:: Waiting for a " + serverType + " connection on port ["
								+ serverPort + "]");

						client = serverSocket.accept();

						final InetAddress inetAddress = client.getInetAddress();
						final String ipAddr = inetAddress.toString();
						final String dnsName = inetAddress.getHostName();

						// we got a connection with the client
						log("ActionServer:: Got a connection on server port " + serverPort);
						log("               from client: " + ipAddr + " :: " + dnsName);

						// check whether it's ok to get messages from this remote machine...
						boolean clientIsOK = false;
						for (String nameOrAddress : trustedSenders) {
							if (dnsName.toLowerCase().endsWith(nameOrAddress)
									|| ipAddr.endsWith(nameOrAddress)) {
								// good enough for us!
								DebugUtils.println("This is a trusted client.");
								clientIsOK = true;
							} else {
								DebugUtils.println("Untrusted client... next!");
							}
						}
						if (!clientIsOK) {
							client.close();
							continue;
						}

						// keep it around
						clients.add(client);
					} catch (IOException ioe) {
						log("ActionServer:: Error with server socket: " + ioe.getLocalizedMessage());
					}

					if (client != null) {
						if (serverType == ClientServerType.PLAINTEXT) {
							getTextClientHandlerThread(client).start();
						} else { // serverType == Java Server
							getJavaClientHandlerThread(client).start();
						}
					}
				}
			}
		};
	}

	/**
	 * @param clientSocket
	 * @return
	 */
	private Thread getTextClientHandlerThread(final Socket clientSocket) {
		return new Thread() {
			private BufferedReader br;

			public synchronized void disconnect() {
				try {
					if (!clientSocket.isClosed()) {
						clientSocket.close();
					}
					if (br != null) {
						br.close();
						br = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void run() {
				try {
					final InputStream inputStream = clientSocket.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					String line = null;
					while ((line = br.readLine()) != null) {
						System.out.println(line);

						if (line.toLowerCase().equals("[[exit]]")) {
							break;
						}

						// the server's exit flag
						// it can kill all clients at the same time
						if (exitFlag) {
							break;
						}

						// tell my listeners!
						for (ActionHandler ah : actionHandlers) {
							ah.receivedActionText(line);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnect();
			}

		};
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
			exitFlag = true;
			for (Socket client : clients) {
				client.close();
			}
			System.out.println("ActionReceiver :: " + serverType + " on port "
					+ serverSocket.getLocalPort() + " is stopping...");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
