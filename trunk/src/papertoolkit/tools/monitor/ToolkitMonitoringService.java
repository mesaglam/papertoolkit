package papertoolkit.tools.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.application.config.Constants.Ports;
import papertoolkit.util.DebugUtils;

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

	public static final String START_SIDECAR = "PaperToolkit::StartSideCar";
	public static final String START_SIDECAR_GUI = "PaperToolkit::StartSideCarGUI";

	private List<Socket> clients = new ArrayList<Socket>();
	private boolean exitServer = false;

	private boolean firstTimeClientConnected = true;

	private MonitorInputHandling monitor;
	private MonitorPaperUIInfo monitorPaperUIInfo;

	private MonitorSystemOut monitorSystemOut;
	private List<PrintWriter> outputs = new ArrayList<PrintWriter>();
	private ServerSocket serverSocket;

	/**
	 * For communicating with the SideCar server...
	 */
	private PrintWriter printWriterToSideCarServer;

	private Socket sideCarSocket;

	private PaperToolkit toolkit;

	private List<String> cachedMessages = new ArrayList<String>();

	/**
	 * @param paperToolkit
	 */
	public ToolkitMonitoringService(PaperToolkit paperToolkit) {
		toolkit = paperToolkit;

		// wait at this port for a connection
		// the flex gui will connect to this monitor
		createServerToWaitForAConnection();

		// We will assume that SideCar is already running at this point, so we should issue the connect
		// command
		initializeOutputToSideCar();
	}

	/**
	 * SideCarViz connects to the toolkit....
	 */
	private void createServerToWaitForAConnection() {
		try {
			serverSocket = new ServerSocket(Ports.TOOLKIT_MONITORING);
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
								DebugUtils.println("Got a connection. Instrumenting the toolkit now...");
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

	public PaperToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * Connect as a client to the SideCar Server...
	 */
	private void initializeOutputToSideCar() {
		if (sideCarSocket == null) {
			try {
				DebugUtils.println("PaperToolkit Monitor is trying to talk to the SideCar Server...");
				sideCarSocket = new Socket("localhost", Ports.SIDE_CAR_COMMUNICATIONS);
				OutputStream outputStream = sideCarSocket.getOutputStream();
				printWriterToSideCarServer = new PrintWriter(outputStream);

				// ask the SideCar server to connect back to us!
				// why do we even do this?? Can we not just use the sideCar socket???
				// this is a bad design!
				// is it to check that SideCar is actually running?
				// argh.... clean up design at some point... :-(
				DebugUtils.println("PaperToolkit is Asking SideCar to Start...");
				printWriterToSideCarServer.println(ToolkitMonitoringService.START_SIDECAR);
				printWriterToSideCarServer.flush();

				printWriterToSideCarServer.println("[[setName]]@_PaperToolkitMonitor_@");
			} catch (Exception e) {
				DebugUtils.println("SideCar Monitor is not running. " + e.getMessage());
			}
		}
	}

	/**
	 * Add hooks to listen to the toolkit.
	 */
	private void instrumentToolkitForMonitoring() {
		// instrument pen input and event dispatch
		monitor = new MonitorInputHandling(this);

		// instrument System.outs!
		// Everytime someone uses a System.out, we'll know about it, and then forward it to SideCar!
		monitorSystemOut = new MonitorSystemOut(this);

		// send paper ui information to the flash GUI
		monitorPaperUIInfo = new MonitorPaperUIInfo(this, toolkit.getLoadedApps());
	}

	/**
	 * 
	 */
	public void openSideCarGUI() {
		try {
			printWriterToSideCarServer.println(ToolkitMonitoringService.START_SIDECAR_GUI);
			printWriterToSideCarServer.flush();
		} catch (Exception e) {
			DebugUtils.println("Is SideCar Running Yet? If not... start SideCar, and try again!");
			DebugUtils.println("We are expecting SideCar to be listening at Port: "
					+ Ports.SIDE_CAR_COMMUNICATIONS);
		}
	}

	/**
	 * Use this method to broadcast information to listeners... The Flex GUI is one listener...
	 * 
	 * @param msg
	 */
	public void outputToClients(String msg) {
		if (outputs.size() == 0) {
			// cache the string, for later!
			cachedMessages.add(msg);
			return;
		}

		// deal with regular messages
		for (PrintWriter clientPW : outputs) {
			clientPW.println(msg);
			clientPW.flush();
		}
	}

	/**
	 * @param clientSocket
	 */
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

					// send cached messages to this client
					for (String cached : cachedMessages) {
						pw.println(cached);
						pw.flush();
					}

					String line = null;
					while ((line = br.readLine()) != null) {
						outputToClients("Client #" + clientID + " said: " + line + " [" + clients.size()
								+ " total clients]");

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
					DebugUtils.println("SideCar exited.");
					// e.printStackTrace();
				}
				disconnect();
			}

		}.start();
	}

	public void startedApp(Application paperApp) {
		outputToClients("<applicationStarted appName=\"" + paperApp.getName() + "\" />");
	}
}
