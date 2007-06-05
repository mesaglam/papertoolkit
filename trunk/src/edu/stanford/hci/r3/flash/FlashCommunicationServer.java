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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.config.Constants;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

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
	 * Send messages to these Java listeners.
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
	 * Useful for debugging Flash communcations...
	 */
	private boolean verbose = false;

	/**
	 * Replaces OTHER_PARAMS in the HTML/Flash template with other query parameters.
	 */
	private String queryParameters = "";

	/**
	 * Allows us to send messages to the Flash GUI.
	 */
	public FlashCommunicationServer() {
		this(Constants.Ports.FLASH_COMMUNICATION_SERVER);
	}

	/**
	 * Customize the port for the Flash Communication Server.
	 * 
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
				DebugUtils.println("Starting Flash Communications Server at port: " + serverPort);
				try {
					socket = new ServerSocket(serverPort);
					while (true) {
						// DebugUtils.println("Waiting for a Client...");
						final Socket incoming = socket.accept();
						// DebugUtils.println("Flash Client connected.");
						final BufferedReader readerIn = new BufferedReader(new InputStreamReader(incoming
								.getInputStream()));
						final PrintStream writerOut = new PrintStream(incoming.getOutputStream());

						// pass this to a handler thread that will service this client!
						flashClients.add(new FlashClient(FlashCommunicationServer.this, clientID++, incoming,
								readerIn, writerOut));
					}
				} catch (SocketException e) {
					DebugUtils.println("Server Socket was Closed");
					// e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				DebugUtils.println("Closing Flash Communications Server");
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
				boolean consumed = listener.messageReceived(command);
				if (consumed) {
					break;
				}
			}
		}
	}

	/**
	 * Point it to the Apollo exe that will serve as your GUI.
	 * 
	 * @param apolloGUIFile
	 */
	public void openFlashApolloGUI(File apolloGUIFile, String... otherArguments) {
		try {
			List<String> app = new ArrayList<String>();
			app.add(apolloGUIFile.getAbsolutePath());
			app.add("port:" + serverPort);
			for (String arg : otherArguments) {
				app.add(arg);
			}
			ProcessBuilder processBuilder = new ProcessBuilder(app);
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in a Template HTML file, and generates the final HTML file on the fly, to contain our SWF,
	 * passing in the port as a parameter.
	 * 
	 * @param flashGUIFile
	 *            Or perhaps this should be a URL in the future, as the GUI can live online? Launches the
	 *            flash GUI in a browser.
	 */
	public void openFlashHTMLGUI(File flashGUIFile) {
		try {
			// replace the template with a port and other query parameters, such as the tool's name (which
			// tells the ToolWrapper which tool to load)
			String fileStr = FileUtils.readFileIntoStringBuffer(flashGUIFile, true).toString();
			fileStr = fileStr.replace("PORT_NUM", Integer.toString(serverPort));
			fileStr = fileStr.replace("OTHER_PARAMS", queryParameters);

			// create a new/temporary file in the same location as the template
			final File outputTempHTML = new File(flashGUIFile.getParentFile(), flashGUIFile.getName() + "_"
					+ serverPort + ".html");
			FileUtils.writeStringToFile(fileStr, outputTempHTML);
			final URI uri = outputTempHTML.toURI();
			
			// browse to this new file
			DebugUtils.println("Loading the Flash GUI...");
			Desktop.getDesktop().browse(uri);
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
		if (verbose) {
			if (msg.length() < 100) {
				DebugUtils.println("Sending message: " + msg.replace("\n", "") + " to all "
						+ flashClients.size() + " clients");
			} else {
				DebugUtils.println("Sending message: " + msg.substring(0, 100).replace("\n", "")
						+ "... to all " + flashClients.size() + " clients");
			}
		}

		for (FlashClient client : flashClients) {
			client.sendMessage(msg);
		}
	}

	/**
	 * @param v
	 */
	public void setVerbose(boolean v) {
		verbose = v;
	}

	/**
	 * @param params
	 */
	public void addQueryParameter(String params) {
		if (queryParameters.equals("")) {
			queryParameters = params;
		} else {
			queryParameters = queryParameters + "&" + params;
		}
	}
}
