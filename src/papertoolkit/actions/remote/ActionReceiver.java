package papertoolkit.actions.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import papertoolkit.actions.Action;
import papertoolkit.application.config.Configuration;
import papertoolkit.application.config.Constants;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.networking.ClientServerType;

import com.thoughtworks.xstream.XStream;

/**
 * <p>
 * A daemon that sits and waits for actions to come in over the wire. It then invokes those actions. You can
 * run an ActionReceiver on any external devices to receive program output.
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
	 * Read our trusted senders from this config file (see Configuration.java).
	 */
	public static final String CONFIG_FILE_KEY = "actionreceiver.trustedsenders";

	/**
	 * This file is found in PaperToolkit/data/config/. You can edit it to restrict the devices that can send
	 * you actions. By default, we will allow all computers to send you R3Actions to the port you are
	 * listening on. We use a wildcard * to designate this.
	 */
	public static final String CONFIG_FILE_VALUE = "data/config/ActionReceiver.xml";

	/**
	 * Will listen on this port for Java XML objects.
	 */
	public static final int DEFAULT_JAVA_PORT = Constants.Ports.ACTION_RECEIVER_JAVA;

	/**
	 * Will listen on this port for text commands.
	 */
	public static final int DEFAULT_PLAINTEXT_PORT = Constants.Ports.ACTION_RECEIVER_PLAINTEXT;

	/**
	 * 
	 */
	private static ActionReceiver javaDaemon;

	/**
	 * 
	 */
	private static final String PROPERTY_NAME = "trustedClients";

	/**
	 * 
	 */
	private static ActionReceiver textDaemon;

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
			javaDaemon.disconnect();
			javaDaemon = null;
		}

		if (textDaemon != null) {
			textDaemon.disconnect();
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
	 * 
	 */
	private ActionReceiverConnectionListener connectionListener;

	/**
	 * Close the action server if this is ever set to true.
	 */
	private boolean exitFlag = false;

	/**
	 * Helps with debugging or setting up connections between devices.
	 */
	private String hostAddress;

	/**
	 * The local hostname of the computer running this ActionReceiver.
	 */
	private String hostName;

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
	private Set<String> trustedSenders = new HashSet<String>();

	/**
	 * A Default ActionReceiver, with a default connection listener and handler (which invokes all incoming
	 * actions immediately)...
	 */
	public ActionReceiver() {
		this(ActionReceiver.DEFAULT_JAVA_PORT, ClientServerType.JAVA, new String[] { "*" });
		setConnectionListener(new ActionReceiverConnectionListener() {
			public void newConnectionFrom(String hostName, String ipAddr) {
				// DebugUtils.println("New Connection From: " + hostName + " " + ipAddr);
			}
		});
		addActionHandler(new ActionHandler());
	}

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
		readTrustedSendersFromConfigFile();
		// DebugUtils.println("Trusted Client Set: " + trustedSenders);

		try {
			serverSocket = new ServerSocket(tcpipPort);
		} catch (IOException e) {
			System.out.println("Error with server socket: " + e.getLocalizedMessage());
		}
		serverType = type;

		// the server port
		serverPort = serverSocket.getLocalPort();

		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// start thread to accept connections
		getDaemonThread().start();
	}

	/**
	 * Add another listener that handles received actions.
	 * 
	 * @param handler
	 */
	public void addActionHandler(ActionHandler handler) {
		actionHandlers.add(handler);
	}

	/**
	 * Waits for connections, and passes of connections to a client handler thread.
	 * 
	 * @return
	 */
	private Thread getDaemonThread() {
		return new Thread() {
			public void run() {
				while (true) {
					Socket client = null;
					try {
						if (exitFlag) {
							// DebugUtils.println("Closing Action Receiver Daemon.");
							break;
						}

						// DebugUtils.println("Waiting for a " + serverType + " connection on port ["
						// + serverPort + "]");

						client = serverSocket.accept();

						final InetAddress inetAddress = client.getInetAddress();
						final String ipAddr = inetAddress.toString();
						final String dnsName = inetAddress.getHostName();

						// we got a connection with the client
						// DebugUtils.println("Got a connection on server port " + serverPort);
						// DebugUtils.println(" from client: " + ipAddr + " :: " + dnsName);
						if (connectionListener != null) {
							connectionListener.newConnectionFrom(dnsName, ipAddr);
						}

						// check whether it's ok to get messages from this remote machine...
						boolean clientIsOK = false;
						for (String nameOrAddress : trustedSenders) {
							if (nameOrAddress.contains("*")) {
								// 128.15.*.* --> 128.15.
								nameOrAddress = nameOrAddress.substring(0, nameOrAddress.indexOf("*"));
							}

							if (dnsName.toLowerCase().endsWith(nameOrAddress)
									|| ipAddr.startsWith(nameOrAddress)) {
								// .stanford.edu
								// 128.15.
								// good enough for us!
								// DebugUtils.println("This is a trusted client. Matched: " + nameOrAddress);
								clientIsOK = true;
							} else {
								// DebugUtils.println("Did not match our client. Filter [" + nameOrAddress
								// + "] does not match " + dnsName + " next!");
							}
						}
						if (!clientIsOK) {
							client.close();
							continue;
						}

						// keep it around
						clients.add(client);
					} catch (IOException ioe) {
						// DebugUtils.println("Error with server socket: " + ioe.getLocalizedMessage());
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
	 * @return the local IP address
	 */
	public String getHostAddress() {
		return hostAddress;
	}

	/**
	 * @return the local computer's name
	 */
	public String getHostName() {
		return hostName;
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
						final Action action = (Action) xml.fromXML(line);

						// tell my listeners!
						for (ActionHandler ah : actionHandlers) {
							ah.receivedAction(action);
						}
					}
				} catch (IOException e) {
					DebugUtils.println(e);
					DebugUtils.println("The client " + clientSocket.getInetAddress().getHostName()
							+ " has probably disconnected...");
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
	 * Reads the list of trusted senders, that is stored in the XML configuration file.
	 */
	private void readTrustedSendersFromConfigFile() {
		final String trustedClients = Configuration.getPropertyFromConfigFile(PROPERTY_NAME, CONFIG_FILE_KEY);
		final String[] clients = trustedClients.split("[,]");
		for (String client : clients) {
			trustedSenders.add(client.trim());
		}
	}

	/**
	 * @param listener
	 */
	public void setConnectionListener(ActionReceiverConnectionListener listener) {
		connectionListener = listener;
	}

	/**
	 * Tell the local receiver to stop processing actions from the remote senders.
	 */
	public void disconnect() {
		try {
			exitFlag = true;
			for (Socket client : clients) {
				client.close();
			}
			// DebugUtils.println("ActionReceiver :: " + serverType + " on port " +
			// serverSocket.getLocalPort() + " is stopping...");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
