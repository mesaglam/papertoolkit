package edu.stanford.hci.r3.flash;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * A messaging server that will relay information objects to one or more Flash GUIs, which will
 * listen for them. It's a two way pipe, so the Flash GUIs can also send messages back!
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
public class FlashCommunicationServer {

	/**
	 * communicate through this port
	 */
	public static final int DEFAULT_PORT = 8545;

	/**
	 * 
	 */
	private int serverPort;

	/**
	 * 
	 */
	private ServerSocket socket;

	/**
	 * @param port
	 */
	public FlashCommunicationServer(int port) {
		serverPort = port;
		new Thread(getServer()).start();
	}

	/**
	 * @param flashGUIFile
	 *            Or perhaps this should be a URL in the future, as the GUI can live online?
	 */
	public void addFlashGUIClient(File flashGUIFile) {
		// browse to the flash GUI file, and pass over our port as a query parameter
		// TODO
	}

	private List<FlashClient> flashClients;

	/**
	 * @return
	 */
	private Runnable getServer() {
		return new Runnable() {


			public void run() {
				System.out
						.println(">> Starting Flash Communications Server at port: " + serverPort);
				try {
					socket = new ServerSocket(serverPort);

					while (true) {

						System.out.println(">> Waiting for a Client...");
						Socket incoming = socket.accept();
						System.out.println(">> Flash Client connected.");
						BufferedReader readerIn = new BufferedReader(new InputStreamReader(incoming
								.getInputStream()));
						PrintStream writerOut = new PrintStream(incoming.getOutputStream());
						
						// pass this to a handler thread that will service this client!
						flashClients.add(new FlashClient());
						
						boolean done = false;
						while (!done) {
							String command = readerIn.readLine();
							if (command == null) {
								done = true;
								incoming.close();
							} else {
								command = command.trim().toLowerCase();
								System.out.println(">> Reading a line from the client: [" + command
										+ "]");
								if (command.equals("exit")) {
									done = true;
									incoming.close();
								} else if (command.contains("next")) {
									handleNext();
								} else if (command.contains("prev")) {
									handlePrev();
								} else if (command.contains("policy-file-request")) {
									// waiting = true;
									String msg = "<?xml version=\"1.0\"?><!DOCTYPE cross-domain-policy SYSTEM \"http://www.macromedia.com/xml/dtds/cross-domain-policy.dtd\">"
											+ "<!-- Policy file for xmlsocket://socks.mysite.com --><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>";
									sendMessage(msg);
									incoming.close();
									writerOut.close();
									readerIn.close();
									done = true;
								} else {
									DebugUtils.println("Unhandled command: " + command);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(">> Closing PageNavServer");
			}
		};
	}

	private void handleNext() {
		File nextPageDir = notesDB.getNextPageDir();
		// figure out which files are there... and send them in XML back to the Flash GUI...
		List<File> pageFiles = FileUtils.listVisibleFiles(nextPageDir);
		DebugUtils.println("Sending these page files back: " + pageFiles);
		sendMessage(makeInkXMLMessageOfPageFiles(pageFiles));
	}

	private void handlePrev() {
		File prevPageDir = notesDB.getPrevPageDir();
		// figure out which files are there... and send them in XML back to the Flash GUI...
		List<File> pageFiles = FileUtils.listVisibleFiles(prevPageDir);
		DebugUtils.println("Sending these page files back: " + pageFiles);
		sendMessage(makeInkXMLMessageOfPageFiles(pageFiles));
	}

	/**
	 * Turn the files into ink that I will send to the flash client! Crazyyy....
	 * 
	 * @param pageFiles
	 * @return
	 */
	private String makeInkXMLMessageOfPageFiles(List<File> pageFiles) {
		StringBuilder xml = new StringBuilder();
		xml.append("<ink>");
		for (File f : pageFiles) {
			xml.append(new Ink(f).getInnerXML());
		}
		xml.append("</ink>");
		return xml.toString();
	}

	private String makeXMLMessageOfPageFiles(List<File> pageFiles) {
		StringBuilder xml = new StringBuilder();
		xml.append("<pageFiles>");
		{
			for (File f : pageFiles) {
				xml.append("<file path=\"" + f.getAbsolutePath() + "\"/>");
			}
		}
		xml.append("</pageFiles>");
		return xml.toString();
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
		System.out.println("Sent " + message.length() + " bytes.");
		System.out.flush();
	}

}
