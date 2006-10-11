package edu.stanford.hci.r3.pen.streaming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.networking.ClientServerType;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Connects to the Pen Server and displays some output...
 * </p>
 * <p>
 * A client who connects to a server can also have local PenListeners to process the samples...
 * 
 * TODO: A multithreaded Client for listening to multiple servers... However, does this make sense?
 * Why would we want to listen to multiple pens?
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenClient {

	private Socket clientSocket;

	private ClientServerType clientType;

	private boolean exitFlag = false;

	/**
	 * Multiple listeners can attach themselves to this Pen Client.
	 */
	private List<PenListener> listeners = new ArrayList<PenListener>();

	/**
	 * The name of the machine which is running the pen server. This may be "localhost."
	 */
	private String machineName;

	private int portNumber;

	private Thread socketListenerThread;

	/**
	 * @param serverName
	 * @param port
	 * @param type
	 */
	public PenClient(String serverName, int port, ClientServerType type) {
		machineName = serverName;
		portNumber = port;
		clientType = type;
	}

	/**
	 * @param penListener
	 * @return
	 */
	public boolean addPenListener(PenListener penListener) {
		return listeners.add(penListener);
	}

	/**
	 * 
	 */
	public void connect() {
		socketListenerThread = getSocketListenerThreadBasedOnClientType();
		socketListenerThread.start();
	}

	/**
	 * Disconnect from the server.
	 */
	public synchronized void disconnect() {
		exitFlag = true;
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private Thread getSocketListenerThreadBasedOnClientType() {

		if (clientType == ClientServerType.JAVA) {
			return new Thread(new Runnable() {

				boolean penIsDown = false;

				public void run() {
					try {
						final BufferedReader br = setupSocketAndReader();
						String line = null;

						final XStream xml = new XStream();

						while ((line = br.readLine()) != null) {
							// System.out.println(line);

							// reconstruct the sample from xml
							final PenSample sample = (PenSample) xml.fromXML(line);
							final boolean penIsUp = sample.isPenUp();

							if (!penIsDown && !penIsUp) {
								penIsDown = true;
								for (PenListener pl : listeners) {
									pl.penDown(sample);
								}
							} else if (penIsUp) {
								penIsDown = false;
								for (PenListener pl : listeners) {
									pl.penUp(sample);
								}
							}

							// tell my listeners!
							// System.out.println(sample.toString());
							// modification as of June 12, 2006
							// the behavior here is the same as in pen connection
							// where a .sample event is NOT generated when penUp happens
							// samples are only generated while the pen is down (even if it just
							// came down)
							if (penIsDown) {
								for (PenListener pl : listeners) {
									pl.sample(sample);
								}
							}

							if (exitFlag) {
								break;
							}
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (SocketException se) {
						if (se.getMessage().contains("socket closed")) {
							DebugUtils.println("Pen Client's Socket is now closed...");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			});
		} else { // PLAIN TEXT CLIENT
			return new Thread(new Runnable() {
				public void run() {
					try {
						final BufferedReader br = setupSocketAndReader();
						String line = null;
						while ((line = br.readLine()) != null) {
							// System.out.println(line);

							// reconstruct the sample

							// tell my listeners!
							System.out.println(line);

							if (exitFlag) {
								break;
							}
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * If the pen listener is one of our listeners, detach it. That is, stop sending events to it.
	 * 
	 * @param penListener
	 * @return
	 */
	public boolean removePenListener(PenListener penListener) {
		return listeners.remove(penListener);
	}

	/**
	 * Connects to the socket and gets an inputstream.
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private BufferedReader setupSocketAndReader() throws UnknownHostException, IOException {
		System.out.println("PenClient: Trying to connect to " + machineName + ":" + portNumber);
		final InetAddress addr = InetAddress.getByName(machineName);
		final String hostName = addr.getCanonicalHostName();
		DebugUtils.println("The resolved host name of this pen is: " + hostName);

		clientSocket = new Socket(machineName, portNumber);
		final InputStream inputStream = clientSocket.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		return br;
	}
}
