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
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.networking.ClientServerType;

/**
 * <p>
 * Connects to the Pen Server and displays some output...
 * </p>
 * <p>
 * A client who connects to a server can also have local PenListeners to process the samples...
 * 
 * TODO: A multithreaded Client for listening to multiple servers... However, does this make sense? Why would
 * we want to listen to multiple pens?
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenClient {

	/**
	 * 
	 */
	private Socket clientSocket;

	/**
	 * This either listens for Java objects (Default) or Plain Text.
	 */
	private ClientServerType clientType;

	/**
	 * Set this to true (call disconnect()) to tell the PenClient to stop listening for incoming pen data.
	 */
	private boolean exitFlag = false;

	/**
	 * Multiple listeners can attach themselves to this Pen Client.
	 */
	private List<PenListener> listeners = Collections.synchronizedList(new ArrayList<PenListener>());

	/**
	 * The name of the machine which is running the pen server. This may be "localhost."
	 */
	private String machineName;

	/**
	 * The PenClient and PenServer will communicate through a socket connection, over this port.
	 */
	private int portNumber;

	/**
	 * 
	 */
	private Thread socketListenerThread;

	/**
	 * @param serverName
	 * @param port
	 *            any
	 * @param type
	 *            in general, you want to use JAVA
	 */
	public PenClient(String serverName, int port, ClientServerType type) {
		machineName = serverName;
		portNumber = port;
		clientType = type;
	}

	/**
	 * Listens to the PenClient (always local), which is talking to the PenServer (either remote or local).
	 * 
	 * @param penListener
	 * @return
	 */
	public synchronized boolean addPenListener(PenListener penListener) {
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
			if (clientSocket != null) {
				clientSocket.close();
			}
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

							// TODO: Should we replace the time field in the sample with the time we received
							// this sample?
							// The old way is that the sample's time field is set to whatever the pen server's
							// time is set to
							// this might result in some clock skew between different pens...
							// should there be an option to do this?

							// basically implements a state machine... =)
							if (!penIsDown && !penIsUp) {
								penIsDown = true;
								notifyListenersOfPenDown(sample);
							} else if (penIsUp) {
								penIsDown = false;
								notifyListenersOfPenUp(sample);
							} else {
								// tell my listeners!
								// June 12, 2006 & Nov 2, 2006
								// the behavior here is the same as in pen connection
								// where a .sample event is NOT generated when penUp or penDown happen
								// samples are only generated while the pen is down
								// (but not if it just came down)
								if (penIsDown) {
									notifyListenersOfPenSample(sample);
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
							// print the text of the pen sample to the console
							DebugUtils.println(line);
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
	 * Pass this sample on to the listeners...
	 * 
	 * @param sample
	 */
	private synchronized void notifyListenersOfPenDown(final PenSample sample) {
		for (PenListener pl : listeners) {
			pl.penDown(sample);
		}
	}

	/**
	 * Pass this sample on to the listeners...
	 * 
	 * @param sample
	 */
	private synchronized void notifyListenersOfPenSample(final PenSample sample) {
		for (PenListener pl : listeners) {
			pl.sample(sample);
		}
	}

	/**
	 * Pass this sample on to the listeners...
	 * 
	 * @param sample
	 */
	private synchronized void notifyListenersOfPenUp(final PenSample sample) {
		for (PenListener pl : listeners) {
			pl.penUp(sample);
		}
	}

	/**
	 * If the pen listener is one of our listeners, detach it. That is, stop sending events to it.
	 * 
	 * @param penListener
	 * @return
	 */
	public synchronized boolean removePenListener(PenListener penListener) {
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
		DebugUtils.println("Trying to connect to " + machineName + ":" + portNumber);
		final InetAddress addr = InetAddress.getByName(machineName);
		final String hostName = addr.getCanonicalHostName();
		DebugUtils.println("The resolved host name of this pen is: " + hostName);

		clientSocket = new Socket(machineName, portNumber);
		final InputStream inputStream = clientSocket.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		return br;
	}
}
