package edu.stanford.hci.r3.flash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import edu.stanford.hci.r3.util.DebugUtils;

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

				boolean done = false;
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
							command = command.trim();
							// System.out.println(">> Reading a line from the client: [" + command + "]");
							if (command.equals("exit")) {
								done = true;
								exitClient();
							} else if (command.equals("exitServer")) {
								done = true;
								clientSocket.close();
								server.exitServer();
							} else if (command.equals("exitApplication")) {
								done = true;
								clientSocket.close();
								server.exitServer();
								System.exit(0);
							} else {
								// DebugUtils.println("Client " + clientID + " Unhandled command: "
								// + command);
								server.handleCommand(clientID, command);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		});
		clientThread.start();
	}

	/**
	 * 
	 */
	public void exitClient() {
		DebugUtils.println("Exiting client " + clientID);
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
