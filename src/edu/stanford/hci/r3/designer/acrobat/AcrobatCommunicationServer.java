package edu.stanford.hci.r3.designer.acrobat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * In Acrobat, run this javascript to send the annotations over to this server.
 * </p>
 * <code>
 * this.submitForm({
 * 	cURL: "http://localhost/",
 * 	cSubmitAs: "XFDF",
 * 	bAnnotations: true
 * });
 * </code>
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class AcrobatCommunicationServer {
	/**
	 * Sep 6, 2006
	 */
	public static void main(String[] args) {
		try {
			AcrobatCommunicationServer server = new AcrobatCommunicationServer(8888,
					new FileOutputStream(new File("AcrobatCommunicationServer.log")));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private boolean alreadyClosed = false;

	/**
	 * Handle multiple connections...
	 */
	private List<Socket> clientConnections = new ArrayList<Socket>();

	/**
	 * for writing log messages to
	 */
	private PrintWriter logOutput;

	private int serverPort;

	/**
	 * The socket that we open.
	 */
	private ServerSocket serverSocket;

	/**
	 * @param port
	 * @param log
	 * @param controller
	 */
	public AcrobatCommunicationServer(int port, FileOutputStream log) {
		serverPort = port;
		logOutput = new PrintWriter(log, true /* autoflush */);
		getCommandRelayThread().start();
	}

	/**
	 * 
	 */
	public void closeSockets() {
		if (!alreadyClosed) {
			alreadyClosed = true;
			try {
				System.out.println("Cleaning up AcrobatCommServer on port "
						+ serverSocket.getLocalPort());
				serverSocket.close();
				for (Socket clientConnection : clientConnections) {
					if (clientConnection != null) {
						clientConnection.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	private Thread getCommandRelayThread() {
		return new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(serverPort);
				} catch (IOException e1) {
					e1.printStackTrace();
					logOutput.println("Could not connect to server port " + serverPort);
				}

				int clientIDs = 0;

				while (true) {
					if (alreadyClosed) {
						break;
					}

					try {
						final String connectMsg = "AcrobatCommServer::Waiting for a plain text connection on port "
								+ serverSocket.getLocalPort();

						logToConsoleAndFile(connectMsg);

						Socket clientConnection = serverSocket.accept();
						clientConnections.add(clientConnection);

						// we got a connection with the client
						String connectedMsg = "AcrobatCommServer::Got a connection on port "
								+ serverSocket.getLocalPort();
						String newClientMsg = "AcrobatCommServer::New Client: "
								+ clientConnection.getInetAddress();

						logToConsoleAndFile(connectedMsg);
						logToConsoleAndFile(newClientMsg);

						getCommunicationsThread(clientConnection, clientIDs++).start();

					} catch (SocketException e) {
					} catch (IOException e) {
					}
				}

			}
		});
	}

	/**
	 * @param clientConn
	 * @return
	 */
	private Thread getCommunicationsThread(final Socket clientConn, final int id) {
		return new Thread(new Runnable() {
			public void run() {
				BufferedReader in = null;
				PrintWriter out = null;
				try {
					in = new BufferedReader(new InputStreamReader(clientConn.getInputStream()));
					out = new PrintWriter(clientConn.getOutputStream(), true);
				} catch (IOException e) {
					logOutput.println("Failed creating an in/out connection with the client.");
				}

				StringBuilder sb = new StringBuilder();

				String inputLine = null;

				boolean trimmed = false;

				try {
					while (((inputLine = in.readLine()) != null)) {
						sb.append(inputLine);

						// System.out.println(inputLine);
						if (!trimmed) { // remove the cruft up front
							final int xmlStart = sb.indexOf("<?xml");
							if (xmlStart != -1) {
								sb.delete(0, xmlStart);
								trimmed = true;
							}
						}

						// seems like adobe acrobat has a bug here!
						// where is the final closing character??? =D
						if (sb.indexOf("</xfdf") != -1) {
							// we're done!
							break;
						}
					}

					logToConsoleAndFile("[Client " + id + "]");
					// include the final character that adobe didn't send us
					logToConsoleAndFile(sb.toString() + ">");

					// tell the client to go away now
					out.println("HTTP/1.1 200 OK");
					out.println("Content-Length: 69");
					out.println("Connection: close");
					out.println("Content-Type: text/html; charset=UTF-8");
					out.println();
					out.println("<html><body>Your Paper UI regions have been sent to R3.</body></html>");
					clientConn.close();
				} catch (IOException e) {
					logOutput.println("Failed reading a line from the client.");
					logOutput.println("Perhaps client " + id + " is closed?");
				}
			}

		});
	}

	/**
	 * @param msg
	 */
	private void logToConsoleAndFile(String msg) {
		System.out.println(msg);
		logOutput.println(msg);
	}

	/**
	 * @param inputLine
	 */
	private void processInput(String inputLine) {
		String inputLowerCase = inputLine.toLowerCase();

		// exit check
		if (inputLowerCase.equals("[[exit]]")) {
			closeSockets();
			System.out.println("Processing exit command");
			System.exit(0);
		}
	}

}
