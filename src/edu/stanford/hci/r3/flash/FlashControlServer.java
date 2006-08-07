/**
 * 
 */
package edu.stanford.hci.r3.flash;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class FlashControlServer {

	/**
	 * communicate through this port
	 */
	public static final int DEFAULT_PORT = 6543;

	private static FlashControlServer server;

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

		server = new FlashControlServer(port);
	}

	private Socket incoming;

	private PrintStream printOut;

	private BufferedReader readerIn;

	private ServerSocket socket;

	/**
	 * @param port
	 */
	private FlashControlServer(int port) {
		System.out.println(">> Starting FlashControlServer");
		try {
			socket = new ServerSocket(port);
			incoming = socket.accept();
			readerIn = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			printOut = new PrintStream(incoming.getOutputStream());
//			out("Enter EXIT to exit.\r");
			out("[[Show Map]]");
			boolean done = false;
			while (!done) {
				String str = readerIn.readLine();
				if (str == null) {
					done = true;
				} else {
//					out("Echo: " + str + "\r");
					if (str.trim().equals("EXIT")) {
						done = true;
						incoming.close();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println(">> Closing FlashControlServer");
	}

	/**
	 * @param str
	 */
	private void out(String str) {
		printOut.println(str);
		printOut.flush();
		System.out.println(str);
	}
}
