package edu.stanford.hci.r3.pen.streaming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> [ronyeh(AT)cs.stanford.edu]
 * @created Mar 31, 2006
 * 
 * Connects to the Pen Server and displays some output...
 * 
 * A client who connects to a server can also have local PenListeners to process the samples...
 * 
 * TODO: A multithreaded Client for listening to multiple servers...
 */
public class PenClient {

	private Socket clientSocket;

	private ClientServerType clientType;

	private boolean exitFlag = false;

	private List<PenListener> listeners = new ArrayList<PenListener>();

	private String machineName;

	private int portNumber;

	private Thread socketListenerThread;

	public PenClient(String name, int port, ClientServerType type) {
		machineName = name;
		portNumber = port;
		clientType = type;
	}

	public void addPenListener(PenListener l) {
		listeners.add(l);
	}

	public void connect() {
		socketListenerThread = getSocketListenerThreadBasedOnClientType();
		socketListenerThread.start();
	}

	/**
	 * 
	 */
	public synchronized void exit() {
		exitFlag = true;
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
							// samples are only generated while the pen is down (even if it just came down)
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
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private BufferedReader setupSocketAndReader() throws UnknownHostException, IOException {
		System.out.println("Trying to connect to " + machineName + "::" + portNumber);
		clientSocket = new Socket(machineName, portNumber);
		final InputStream inputStream = clientSocket.getInputStream();
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		return br;
	}
}
