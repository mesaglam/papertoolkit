package papertoolkit.tools.monitor;

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
import papertoolkit.application.Application;
import papertoolkit.application.config.Constants.Ports;
import papertoolkit.events.EventDispatcher;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
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

	public static final String START_SIDECAR = "StartSideCar";

	private static int clientIDs = 0;
	private List<Socket> clients = new ArrayList<Socket>();
	private boolean exitServer = false;
	private boolean firstTimeClientConnected = true;
	private ToolkitMonitor monitor;
	private List<PrintWriter> outputs = new ArrayList<PrintWriter>();
	private ServerSocket serverSocket;
	private PaperToolkit toolkit;
	private MonitorSystemOut monitorSystemOut;

	/**
	 * @param paperToolkit
	 */
	public ToolkitMonitoringService(PaperToolkit paperToolkit) {
		toolkit = paperToolkit;

		// wait at this port for a connection
		// the flex gui will connect to this monitor
		createServerToWaitForAConnection();

		// once the first one happens, access paper toolkit and instrument the event dispatcher...
		// send out information to the socket!
		
		// if a flash gui already exists, ask it to connect to us! =\
		
	}

	/**
	 * Darn... so the flex GUI is supposed to connect to the toolkit? Why don't we do it the other way???
	 * Perhaps we can easily ask the Flash GUI to connect to the toolkit?
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

	/**
	 * Add hooks to listen to the toolkit.
	 */
	private void instrumentToolkitForMonitoring() {
		monitor = new ToolkitMonitor(this);

		// instrument the event dispatcher
		EventDispatcher eventDispatcher = toolkit.getEventDispatcher();
		eventDispatcher.setMonitor(monitor);

		List<Application> loadedApps = toolkit.getLoadedApps();
		for (Application app : loadedApps) {
			// instrument all pens
			List<InputDevice> penInputDevices = app.getPenInputDevices();
			for (final InputDevice dev : penInputDevices) {
				dev.addLivePenListener(new PenListener() {
					public void penDown(PenSample sample) {
						monitor.penDown(dev, sample);
					}

					public void penUp(PenSample sample) {
						monitor.penUp(dev, sample);
					}

					public void sample(PenSample sample) {
						// don't do anything here (for now), because it's too much info

					}
				});
			}

			// instrument all event handlers!
		}

		// instrument System.outs!
		instrumentSystemOuts();
	}

	/**
	 * Everytime someone uses a System.out, we'll know about it, and then forward it to SideCar!
	 */
	private void instrumentSystemOuts() {
		monitorSystemOut = new MonitorSystemOut(this);
	}

	/**
	 * Use this method to broadcast information to listeners...
	 * 
	 * @param msg
	 */
	public void outputToClients(String msg) {
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
}
