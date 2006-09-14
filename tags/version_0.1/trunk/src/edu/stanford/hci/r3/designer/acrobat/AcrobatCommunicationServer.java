package edu.stanford.hci.r3.designer.acrobat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * In Acrobat, run this javascript to send the annotations over to this server.
 * </p>
 * <code>
 * this.submitForm({
 * 		cURL: "http://localhost/",
 * 		cSubmitAs: "XFDF",
 * 		bAnnotations: true
 * });
 * </code>
 * <p>
 * When XML comes in, transform it such that we flip the heights (Adobe considers 0,0 the bottom
 * left of a page; We consider it the top left, like in a GUI toolkit. Then, write the XML file to
 * disk as a region configuration file.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class AcrobatCommunicationServer {

	/**
	 * The RESPONSE to send to Acrobat Pro.
	 */
	private static final StringBuilder CONFIRMATION = FileUtils.readFileIntoStringBuffer(
			PaperToolkit.getResourceFile("/designer/Confirmation.html"), false /* no new lines */);

	private static AcrobatCommunicationServer server;

	/**
	 * Sep 6, 2006
	 */
	public static void main(String[] args) {
		startServer();
	}

	/**
	 * 
	 */
	protected static void startServer() {
		if (server != null) {
			DebugUtils.println("Acrobat Communication Server has already started...");
			return;
		}
		try {
			server = new AcrobatCommunicationServer(8888, new FileOutputStream(new File(
					"AcrobatCommunicationServer.log")));

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

	private File outputFile;

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

						final Socket clientConnection = serverSocket.accept();
						clientConnections.add(clientConnection);

						// we got a connection with the client
						final String connectedMsg = "AcrobatCommServer::Got a connection on port "
								+ serverSocket.getLocalPort();
						final String newClientMsg = "AcrobatCommServer::New Client: "
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

				final StringBuilder sb = new StringBuilder();

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

					processXML(sb.toString() + ">");

					final File parentDir = outputFile.getParentFile();
					// fill in the correct URI of the parent directory
					String confirmation = CONFIRMATION.toString().replace("__PARENTURI__",
							parentDir.toURI().toString());
					confirmation = confirmation.replace("__FOLDERNAME__", parentDir.getName());
					confirmation = confirmation.replace("__FILEURI__", outputFile.toURI().toString());
					confirmation = confirmation.replace("__FILENAME__", outputFile.getName());

					// tell the client to go away now
					// provide a valid web server response so that the Acrobat web browser
					// will not hang
					out.println("HTTP/1.1 200 OK");
					out.println("Content-Length: " + confirmation.length());
					out.println("Connection: close");
					out.println("Content-Type: text/html; charset=UTF-8");
					out.println();
					out.println(confirmation);
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
	 * We communicate the region information through a temporary XML file.
	 * 
	 * @param xml
	 */
	private void processXML(String xml) {
		logToConsoleAndFile(xml);
		final File xmlFile = PaperToolkit.getResourceFile("/designer/TemporaryXML.xml");
		FileWriter fw;
		try {
			fw = new FileWriter(xmlFile);
			fw.append(xml);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final RegionConfigurationWriter writer = new RegionConfigurationWriter(xmlFile);
		writer.processXML();
		outputFile = writer.getOutputFile();
	}

}
