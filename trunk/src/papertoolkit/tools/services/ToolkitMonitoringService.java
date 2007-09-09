package papertoolkit.tools.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.PaperToolkit;
import papertoolkit.events.EventDispatcher;

/**
 * <p>
 * You can monitor and control PaperToolkit's runtime by talking to this server. By default, PaperToolkit will
 * broadcast information to this server. If anyone is listening, then they can communicate with PaperToolkit.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ToolkitMonitoringService {

	private static int clientIDs = 0;
	
	/**
	 * Communicate over this port.
	 */
	public static int SERVICES_PORT = 9797;

	private List<Socket> clients = new ArrayList<Socket>();
	private boolean exitServer = false;
	private boolean firstTimeClientConnected = true;
	private List<PrintWriter> outputs = new ArrayList<PrintWriter>();
	private ServerSocket serverSocket;
	private PaperToolkit toolkit;

	private ToolkitMonitor monitor;
	
	public ToolkitMonitoringService(PaperToolkit paperToolkit) {
		toolkit = paperToolkit;
		
		// wait at this port for a connection
		createServerToWaitForAConnection();

		// once the first one happens, access paper toolkit and instrument the event dispatcher...

		// send out information to the socket!

	}

	private void createServerToWaitForAConnection() {
		try {
			serverSocket = new ServerSocket(SERVICES_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread() {

			public void run() {
				while (!exitServer) {
					Socket client;
					try {
						client = serverSocket.accept();
						if (client != null) {
							clients.add(client);
							startClientHandlerThread(client);
							if (firstTimeClientConnected) {
								instrumentToolkitForMonitoring();
								firstTimeClientConnected = false;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
	}

	private void instrumentToolkitForMonitoring() {
		EventDispatcher eventDispatcher = toolkit.getEventDispatcher();
		monitor = new ToolkitMonitor(toolkit, eventDispatcher, this);
		eventDispatcher.setMonitor(monitor);
	}

	/**
	 * Use this method to broadcast information to listeners...
	 * @param msg
	 */
	public void outputToClients(String msg) {
		for (PrintWriter clientPW : outputs) {
			clientPW.println(msg);
			clientPW.flush();
		}
	}

	private void startClientHandlerThread(final Socket clientSocket) {
		new Thread() {
			private BufferedReader br;
			private int clientID = ++clientIDs; // count from 1
			private PrintWriter pw;

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
					final OutputStream outputStream = clientSocket.getOutputStream();
					final InputStream inputStream = clientSocket.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					pw = new PrintWriter(outputStream);
					outputs.add(pw);

					String line = null;
					while ((line = br.readLine()) != null) {
						outputToClients("Client " + clientID  + " of " + clients.size() + " said: " + line);

						if (line.equals("[[exit]]")) {
							clients.remove(clientSocket);
							outputs.remove(pw);
							break;
						}

						// the server's exit flag
						// it can kill all clients at the same time
						if (exitServer) {
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnect();
			}

		}.start();
	}
}
