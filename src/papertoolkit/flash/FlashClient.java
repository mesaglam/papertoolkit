package papertoolkit.flash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Handles one Flash GUI client, that will communicate with our local Java server.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class FlashClient {

	private int clientID;
	private Socket clientSocket;
	private Thread clientThread;
	private BufferedReader fromClient;
	private FlashCommunicationServer server;
	private PrintStream toClient;
	private boolean done = false;

	public FlashClient(FlashCommunicationServer flashCommServer, int id, Socket clientSock,
			BufferedReader readerIn, PrintStream writerOut) {
		server = flashCommServer;
		clientID = id;
		clientSocket = clientSock;
		toClient = writerOut;
		fromClient = readerIn;

		clientThread = new Thread(new Runnable() {
			public void run() {
				DebugUtils.println("New Flash Client: ID == " + clientID);
				while (!done) {
					String command = null;
					try {
						command = fromClient.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (command == null) {
							done = true;
							clientSocket.close();
						} else {
							// drop the surrounding whitespace
							command = command.trim(); 
							// always pass commands up to the server
							server.handleCommand(FlashClient.this, command);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		});
		clientThread.start();
	}
	
	public int getID() {
		return clientID;
	}

	/**
	 * Set the done flag and close the socket. We don't need this client anymore.
	 */
	public void exitClient() {
		DebugUtils.println("Exiting client " + clientID);
		done = true;
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints to both the console and to the client.
	 * 
	 * @param message
	 *            the string to print.
	 */
	public void sendMessage(String message) {
		if (toClient != null) {
			toClient.print(message + "\0");
			toClient.flush();
		} else {
			DebugUtils.println("There is no client to send the message to.");
		}
		// DebugUtils.println("Sent " + message.length() + " bytes.");
		System.out.flush();
	}

}
