package papertoolkit.pen.synch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import papertoolkit.PaperToolkit;
import papertoolkit.config.Constants;
import papertoolkit.events.EventDispatcher;
import papertoolkit.pattern.PatternPackage;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * Wait at a socket (say: 9999) and receive the location of xml files that contain data from a pen synch
 * action (the user drops the pen into the dock). Then, process them and call any event handlers you have
 * registered at runtime.
 * 
 * Additionally, translate the batched data into streaming coordinates, and pass these events as if they
 * happened in real time over to the event engine.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchedDataServer {

	/**
	 * Will listen on this port for text commands.
	 */
	public static final int DEFAULT_PLAINTEXT_PORT = Constants.Ports.BATCH_SERVER;

	/**
	 * Tells the server that a client wishes to exit. Closes the client's handler.
	 */
	public static final String EXIT_COMMAND = "[[exit]]";

	/**
	 * 
	 */
	private List<Socket> clients = new ArrayList<Socket>();

	/**
	 * TODO: We will pass batched events to this event engine, to simulate event dispatch in "real time".
	 */
	private EventDispatcher eventEngine;

	/**
	 * 
	 */
	private List<BatchedEventHandler> eventHandlers = new ArrayList<BatchedEventHandler>();

	/**
	 * Close the batch server if this is ever set to true.
	 */
	private boolean exitFlag = false;

	/**
	 * TODO: Used for converting batched coordinates to streaming coordinates, which can be passed to our
	 * event handlers.
	 */
	private Map<String, PatternPackage> patternPackages = PatternPackage
			.getAvailablePatternPackages(PaperToolkit.getPatternPath());

	/**
	 * Wait for communication on a socket at this port.
	 */
	private int serverPort;

	/**
	 * The socket over which we receive incoming pen data.
	 */
	private ServerSocket serverSocket;

	
	
	/**
	 * @param eventEngine
	 */
	public BatchedDataServer(EventDispatcher theEventEngine) {
		try {
			eventEngine = theEventEngine;
			serverSocket = new ServerSocket(DEFAULT_PLAINTEXT_PORT);
			serverPort = DEFAULT_PLAINTEXT_PORT;
			// start thread to accept connections
			getDaemonThread().start();
		} catch (IOException e) {
			System.out.println("Error with server socket: " + e.getLocalizedMessage());
		}
	}

	/**
	 * @param clientSocket
	 * @return
	 */
	private Thread getClientHandlerThread(final Socket clientSocket) {
		return new Thread() {

			/**
			 * Read from the client through this reader.
			 */
			private BufferedReader br;

			/**
			 * Disconnect from the import client (the C# monitor)
			 */
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
						// DebugUtils.println(line);
						if (line.toLowerCase().equals(EXIT_COMMAND)) {
							break;
						}

						// the server's exit flag
						// it can kill all clients at the same time
						if (exitFlag) {
							break;
						}

						// the file name is everything after...
						if (line.toLowerCase().startsWith("xml: ")) {
							// get the file name of the xml file
							final String fileName = line.substring(5).trim();
							// DebugUtils.println(fileName); // everything afterward
							final File xmlFile = new File(fileName);
							DebugUtils.println("Retrieving: " + xmlFile.getAbsolutePath());
							if (xmlFile.exists()) {
								// System.out.println("The file exists!");
								for (BatchedEventHandler beh : eventHandlers) {
									// send the xml file to the batched event handler...
									beh.batchedDataArrived(xmlFile);
								}
							} else {
								DebugUtils.println("The file does not exist. =(");
							}
						}
					}
					DebugUtils.println("Import Thread Finished...");
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnect();
			}

		};
	}

	/**
	 * @return the server thread.
	 */
	private Thread getDaemonThread() {
		return new Thread() {

			public void run() {
				while (true) {
					Socket client = null;
					try {
						if (exitFlag) {
							log("Closing BatchServer.");
							break;
						}

						log("Waiting for a connection on port [" + serverPort + "]");

						client = serverSocket.accept();

						final InetAddress inetAddress = client.getInetAddress();
						final String ipAddr = inetAddress.toString();
						final String dnsName = inetAddress.getHostName();

						// we got a connection with the client
						log("Got a connection on server port " + serverPort);
						log("               from client: " + ipAddr + " :: " + dnsName);

						// keep it around
						clients.add(client);
						getClientHandlerThread(client).start();
					} catch (IOException ioe) {
						log("Error with server socket: " + ioe.getLocalizedMessage());
					}
				}
			}
		};
	}

	/**
	 * To make the code in this class look a little cleaner.
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("Batch Server: " + msg);
	}

	/**
	 * @param batchEventHandlers
	 */
	public void registerBatchEventHandlers(List<BatchedEventHandler> batchEventHandlers) {
		for (BatchedEventHandler beh : batchEventHandlers) {
			eventHandlers.add(beh);
		}
	}

	/**
	 * Tell the server to stop sending actions.
	 */
	public void stopDaemon() {
		try {
			exitFlag = true;
			for (Socket client : clients) {
				client.close();
			}
			System.out.println("BatchServer on port " + serverSocket.getLocalPort() + " is stopping...");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param batchEventHandlers
	 */
	public void unregisterBatchEventHandlers(List<BatchedEventHandler> batchEventHandlers) {
		for (BatchedEventHandler beh : batchEventHandlers) {
			eventHandlers.remove(beh);
			DebugUtils.println("Unregistering BatchEventHandler [" + beh + "]");
		}
	}

}
