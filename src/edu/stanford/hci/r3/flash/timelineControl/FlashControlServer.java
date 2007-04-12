package edu.stanford.hci.r3.flash.timelineControl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import edu.stanford.hci.r3.config.Constants;

/**
 * <p>
 * This server will relay events to the Flash UI, which will listen for commands. It can ask the UI
 * to do different things, such as going to a named frame.
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
public class FlashControlServer {

	/**
	 * communicate through this port
	 */
	public static final int DEFAULT_PORT = Constants.Ports.FLASH_CONTROL_SERVER;

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

		new FlashControlServer(port);
	}

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
	public FlashControlServer() {
		this(DEFAULT_PORT);
	}

	/**
	 * @param port
	 */
	public FlashControlServer(int port) {
		serverPort = port;
		new Thread(getServer()).start();
	}

	/**
	 * @return
	 */
	private Runnable getServer() {
		return new Runnable() {

			public void run() {
				System.out.println(">> Starting FlashControlServer");
				try {
					socket = new ServerSocket(serverPort);
					System.out.println(">> Waiting for a Client...");
					incoming = socket.accept();
					System.out.println(">> Flash UI Client connected.");
					readerIn = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
					writerOut = new PrintStream(incoming.getOutputStream());
					sendMessage("Say EXIT to exit.\r");
					boolean done = false;
					while (!done) {
						System.out.println(">> Reading a line from the client.");
						String str = readerIn.readLine();
						if (str == null || str.trim().equals("EXIT")) {
							done = true;
							incoming.close();
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}
				System.out.println(">> Closing FlashControlServer");
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
			writerOut.println(message);
			writerOut.flush();
		} else {
			System.out.println("There is no client to send the message to.");
		}
		System.out.println(message);
		System.out.flush();
	}
}
