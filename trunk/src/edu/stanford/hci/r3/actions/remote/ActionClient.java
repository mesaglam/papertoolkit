package edu.stanford.hci.r3.actions.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.actions.R3Action;
import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ActionClient {

	/**
	 * 
	 */
	private List<ActionHandler> actionHandlers = new ArrayList<ActionHandler>();

	/**
	 * 
	 */
	private Socket clientSocket;

	/**
	 * 
	 */
	private ClientServerType clientType;

	/**
	 * 
	 */
	private boolean exitFlag = false;

	/**
	 * 
	 */
	private String machineName;

	/**
	 * 
	 */
	private int portNumber;

	/**
	 * 
	 */
	private BufferedReader socketBufferedReader;

	/**
	 * 
	 */
	private Thread socketListenerThread;

	/**
	 * @param serverNameOrIPAddr
	 * @param port
	 * @param type
	 */
	public ActionClient(String serverNameOrIPAddr, int port, ClientServerType type) {
		machineName = serverNameOrIPAddr;
		portNumber = port;
		clientType = type;
	}

	/**
	 * @param handler
	 */
	public void addActionHandler(ActionHandler handler) {
		actionHandlers.add(handler);
	}

	/**
	 * Connect to the server.
	 */
	public void connect() {
		socketListenerThread = getSocketListenerThreadBasedOnClientType();
		socketListenerThread.start();
	}

	/**
	 * 
	 */
	public synchronized void disconnect() {
		try {
			exitFlag = true;
			if (clientSocket != null) {
				clientSocket.close();
				clientSocket = null;
			}
			if (socketBufferedReader != null) {
				socketBufferedReader.close();
				socketBufferedReader = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return whether this action client is still active.
	 */
	public boolean isRunning() {
		return !exitFlag && clientSocket != null;
	}

	/**
	 * @return
	 */
	private Runnable getJavaClientRunnable() {
		return new Runnable() {
			public void run() {
				try {
					final BufferedReader br = setupSocketAndReader();
					String line = null;

					final XStream xml = new XStream();

					while ((line = br.readLine()) != null) {
						// System.out.println(line);

						// reconstruct the action
						final R3Action gpAction = (R3Action) xml.fromXML(line);

						// tell my listeners!
						for (ActionHandler ah : actionHandlers) {
							ah.receivedAction(gpAction);
						}

						if (exitFlag) {
							break;
						}
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnect();
			}
		};
	}

	/**
	 * @return
	 */
	private Thread getSocketListenerThreadBasedOnClientType() {
		if (clientType == ClientServerType.JAVA) {
			return new Thread(getJavaClientRunnable());
		} else { // PLAIN TEXT CLIENT
			return new Thread(getTextClientRunnable());
		}
	}

	/**
	 * @return
	 */
	private Runnable getTextClientRunnable() {
		return new Runnable() {
			public void run() {
				try {
					final BufferedReader br = setupSocketAndReader();
					String line = null;
					while ((line = br.readLine()) != null) {
						// tell my listeners!
						// System.out.println(line);
						for (ActionHandler ah : actionHandlers) {
							ah.receivedActionText(line);
						}
						if (exitFlag) {
							break;
						}
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnect();
			}
		};
	}

	/**
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private BufferedReader setupSocketAndReader() throws UnknownHostException, IOException {
		DebugUtils.println("Trying to connect to " + machineName + "::" + portNumber);
		clientSocket = new Socket(machineName, portNumber);
		final InputStream inputStream = clientSocket.getInputStream();
		socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		return socketBufferedReader;
	}

}