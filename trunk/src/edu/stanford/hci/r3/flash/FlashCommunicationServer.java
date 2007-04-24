package edu.stanford.hci.r3.flash;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.config.Constants;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * A messaging server that will relay information objects to one or more Flash GUIs, which will listen for
 * them. It's a two way pipe, so the Flash GUIs can also send messages back!
 * 
 * This is an early implementation. Later on, we may allow our event handlers to live in the world of Flash,
 * for faster UI prototyping.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashCommunicationServer {

	/**
	 * communicate through this port
	 */
	public static final int DEFAULT_PORT = Constants.Ports.FLASH_COMMUNICATION_SERVER;

	/**
	 * Only counts up.
	 */
	private int clientID = 0;

	/**
	 * 
	 */
	private Map<String, FlashCommand> commands = new HashMap<String, FlashCommand>();

	/**
	 * All the clients that have connected to us! You can test this by telnetting in to this server and port.
	 */
	private List<FlashClient> flashClients = new ArrayList<FlashClient>();

	/**
	 * 
	 */
	private List<FlashListener> listeners = new ArrayList<FlashListener>();

	/**
	 * 
	 */
	private int serverPort;

	/**
	 * 
	 */
	private Thread serverThread;

	/**
	 * 
	 */
	private ServerSocket socket;

	/**
	 * Allows us to send messages to the Flash GUI.
	 */
	public FlashCommunicationServer() {
		this(DEFAULT_PORT);
	}

	/**
	 * Customize the port for the Flash Communication Server.
	 * @param port
	 */
	public FlashCommunicationServer(int port) {
		serverPort = port;
		serverThread = new Thread(getServer());
		serverThread.start();
	}

	/**
	 * @param cmdName
	 * @param flashCommand
	 */
	public void addCommand(String cmdName, FlashCommand flashCommand) {
		commands.put(cmdName, flashCommand);
	}

	/**
	 * @param flashListener
	 */
	public void addFlashClientListener(FlashListener flashListener) {
		listeners.add(flashListener);
	}

	/**
	 * Exits the Flash communication server.
	 */
	public void exitServer() {
		for (FlashClient client : flashClients) {
			client.exitClient();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugUtils.println("Exiting Flash Communications Server.... "
				+ "If this is the last thread, the program should exit.");
		for (FlashListener listener : listeners) {
			listener.messageReceived("exitServer");
		}
	}

	/**
	 * @return
	 */
	private Runnable getServer() {
		return new Runnable() {
			public void run() {
				log("Starting Flash Communications Server at port: " + serverPort);
				try {
					socket = new ServerSocket(serverPort);
					while (true) {
						log("Waiting for a Client...");
						final Socket incoming = socket.accept();
						log("Flash Client connected.");
						final BufferedReader readerIn = new BufferedReader(new InputStreamReader(incoming
								.getInputStream()));
						final PrintStream writerOut = new PrintStream(incoming.getOutputStream());
						
						// pass this to a handler thread that will service this client!
						flashClients.add(new FlashClient(FlashCommunicationServer.this, clientID++, incoming,
								readerIn, writerOut));
					}
				} catch (SocketException e) {
					log("Server Socket was Closed");
					// e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				log("Closing Flash Communications Server");
			}

		};
	}

	/**
	 * @param clientID
	 * @param command
	 */
	public void handleCommand(int clientID, String command) {
		if (commands.containsKey(command)) {
			// invoking a stored command
			commands.get(command).invoke();
		} else {
			// sending the command on to listeners
			DebugUtils.println("Server got command [" + command + "] from client " + clientID);
			for (FlashListener listener : listeners) {
				listener.messageReceived(command);
			}
		}
	}

	/**
	 * @param string
	 */
	private void log(String string) {
		DebugUtils.println(string);
	}

	/**
	 * Point it to the Apollo exe that will serve as your GUI.
	 * 
	 * @param apolloGUIFile
	 */
	public void openFlashApolloGUI(File apolloGUIFile) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(apolloGUIFile.getAbsolutePath(), "port:"
					+ serverPort);
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Currently, we assume the next client connection is for THIS flash GUI. =) We'll hopefully be able to
	 * use this information later.
	 * 
	 * @param flashGUIFile
	 *            Or perhaps this should be a URL in the future, as the GUI can live online? Launches the
	 *            flash GUI in a browser.
	 */
	public void openFlashGUI(File flashGUIFile) {
		// browse to the flash GUI file, and pass over our port as a query parameter
		// TODO: pass the port
		// Idea... auto-generate an HTML file that includes the PORT as a query parameter?
		// We did this for the Whiteboard
		try {
			Desktop.getDesktop().browse(flashGUIFile.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void removeAllFlashClientListeners() {
		listeners.clear();
	}

	/**
	 * @param msg
	 */
	public void sendMessage(String msg) {
		if (msg.length() < 100) {
			DebugUtils.println("Sending message: " + msg.replace("\n", "") + " to all " + flashClients.size()
					+ " clients");
		} else {
			DebugUtils.println("Sending message: " + msg.substring(0, 100).replace("\n", "") + "... to all "
					+ flashClients.size() + " clients");
		}
		for (FlashClient client : flashClients) {
			client.sendMessage(msg);
		}
	}
}
