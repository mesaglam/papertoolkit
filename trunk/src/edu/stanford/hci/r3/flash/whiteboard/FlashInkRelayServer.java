package edu.stanford.hci.r3.flash.whiteboard;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * This server will relay Ink objects to the Flash UI, which will listen for them.
 * 
 * This is a skeleton implementation. Later on, we will allow our event handlers and content filters
 * to live in the world of Flash, for faster UI prototyping.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashInkRelayServer {

	/**
	 * communicate through this port
	 */
	public static final int DEFAULT_PORT = 6544;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = DEFAULT_PORT;

		try {
			port = Integer.parseInt(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// Catch exception and keep going.
		}

		new FlashInkRelayServer(port);
	}

	private boolean clientConnected;

	/**
	 * 
	 */
	private Socket incoming;

	/**
	 * 
	 */
	private BufferedReader readerIn;

	/**
	 * 
	 */
	private int serverPort;

	/**
	 * 
	 */
	private ServerSocket socket;

	/**
	 * 
	 */
	private PrintStream writerOut;

	/**
	 * 
	 */
	public FlashInkRelayServer() {
		this(DEFAULT_PORT);
	}

	/**
	 * @param port
	 */
	public FlashInkRelayServer(int port) {
		serverPort = port;
		new Thread(getServer()).start();
	}

	/**
	 * @return
	 */
	private Runnable getServer() {
		return new Runnable() {

			public void run() {
				System.out.println(">> Starting FlashInkRelayServer at port: " + serverPort);
				try {
					socket = new ServerSocket(serverPort);
					System.out.println(">> Waiting for a Client...");
					incoming = socket.accept();
					System.out.println(">> Flash Client connected.");
					clientConnected = true;
					readerIn = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
					writerOut = new PrintStream(incoming.getOutputStream());
					// sendMessage("Say EXIT to exit.\r");
					boolean done = false;
					while (!done) {
						String str = readerIn.readLine();
						System.out.println(">> Reading a line from the client: [" + str + "]");
						if (str == null || str.trim().toLowerCase().equals("exit")) {
							done = true;
							incoming.close();
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}
				System.out.println(">> Closing FlashInkRelayServer");
			}
		};
	}

	/**
	 * Prints to both the console and to the client.
	 * 
	 * @param message
	 *            the string to print.
	 */
	public void sendMessage(String message) {
		if (writerOut != null) {
			writerOut.print(message + "\0");
			writerOut.flush();
		} else {
			System.out.println("There is no client to send the message to.");
		}
		System.out.println(message);
		System.out.flush();
	}

}
