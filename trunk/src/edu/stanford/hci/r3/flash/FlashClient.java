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
	private BufferedReader fromClient;
	private FlashCommunicationServer server;
	private PrintStream toClient;
	private Thread clientThread ;

	public FlashClient(FlashCommunicationServer flashCommServer, int id, Socket clientSock,
			BufferedReader readerIn, PrintStream writerOut) {
		server = flashCommServer;
		clientID = id;
		clientSocket = clientSock;
		toClient = writerOut;
		fromClient = readerIn;

		clientThread = new Thread(new Runnable() {
			@Override
			public void run() {
				DebugUtils.println("New Flash Client: " + clientID);

				boolean done = false;
				while (!done) {
					String command = null;
					String commandLowerCase = null;
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
							commandLowerCase = command.toLowerCase();
							// System.out.println(">> Reading a line from the client: [" + command + "]");
							if (commandLowerCase.equals("exit")) {
								done = true;
								exitClient();
							} else if (commandLowerCase.equals("exitserver")) {
								done = true;
								clientSocket.close();
								server.exitServer();
							} else if (command.contains("policy-file-request")) {
								// TODO: Make this work for Flash GUIs on systems that have NOT installed
								// Flex.

								DebugUtils.println("Got a policy file request.");
								// waiting = true;
								String msg = "<?xml version=\"1.0\"?><!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">"
										+ "<!-- Policy file for xmlsocket://socks.mysite.com --><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>";
								sendMessage(msg);
								clientSocket.close();
								toClient.close();
								fromClient.close();
								done = true;
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
		DebugUtils.println("Sent " + message.length() + " bytes.");
		System.out.flush();
	}

}
