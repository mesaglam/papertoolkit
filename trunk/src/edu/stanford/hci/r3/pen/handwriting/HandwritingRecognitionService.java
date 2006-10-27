package edu.stanford.hci.r3.pen.handwriting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Allows us to use the HWRecognition server (written in C#/.NET) from Java. This acts as a client
 * that relays messagse to our Handwriting Recognition Server.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class HandwritingRecognitionService {

	private static HandwritingRecognitionService instance;

	public synchronized static HandwritingRecognitionService getInstance() {
		if (instance == null) {
			instance = new HandwritingRecognitionService();
		}
		return instance;
	}

	private boolean clientInitialized;

	private BufferedReader clientReader;

	private Socket clientSocket = null;

	private PrintWriter clientWriter;

	private int numConnectAttempts = 0;

	/**
	 * Has the server been started?
	 */
	private boolean serverStarted;

	/**
	 * This should only ever be called once, so we will start one server and one client.
	 */
	private HandwritingRecognitionService() {
		initializeServer();
	}

	/**
	 * Connect to the HWRecognition Server...
	 */
	public void connect() {
		if (clientInitialized) {
			return;
		}
		if (numConnectAttempts > 2) {
			// if it doesn't work after 3 attempts, give up
			DebugUtils
					.println("We tried to connect to the Handwriting Server three times... and failed.");
			return;
		}

		clientInitialized = true;
		try {
			numConnectAttempts++;
			clientSocket = new Socket("localhost", 9898);
			clientWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket
					.getOutputStream())));
			clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			DebugUtils.println("Unknown Host: " + e.getLocalizedMessage());
			clientInitialized = false;
		} catch (IOException e) {
			DebugUtils.println("IOException: " + e.getLocalizedMessage());
			clientInitialized = false;
		}

		if (!clientInitialized) {
			// sleep for a bit... then try again!
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			connect();
		} else {
			// done!
		}
	}

	private void disconnectClient() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitServer() {
		clientWriter.println("[[quitserver]]");
		clientWriter.flush();
		disconnectClient();
	}

	/**
	 * @return
	 */
	public List<String> getAlternatives() {
		clientWriter.println("[[topten]]");
		clientWriter.flush();
		String line = null;
		List<String> alternatives = new ArrayList<String>();
		try {
			while ((line = clientReader.readLine()) != null) {
				if (line.equals("[[endofalternatives]]")) {
					break;
				}
				alternatives.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return alternatives;
	}

	/**
	 * Runs the .exe file.
	 */
	private void initializeServer() {
		if (serverStarted) {
			return;
		}

		serverStarted = true;

		// start a thread to start the server
		new Thread(new Runnable() {
			public void run() {
				// off of the PaperToolkit directory...
				// handwritingRec\bin\HandwritingRecognition.exe
				File hwrec = new File(PaperToolkit.getToolkitRootPath(),
						"handwritingRec/bin/Release/HandwritingRecognition.exe");
				ProcessBuilder builder = new ProcessBuilder(hwrec.getPath());
				try {
					Process process = builder.start();
					InputStream inputStream = process.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while ((line = br.readLine()) != null) {
						DebugUtils.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	public String recognizeHandwriting(File xmlFile) {
		return recognizeHandwriting(FileUtils.readFileIntoStringBuffer(xmlFile, false).toString());
	}

	/**
	 * This recognize call should return as fast as possible... as an end user will experience
	 * this...
	 * 
	 * @param text
	 * @return
	 */
	public String recognizeHandwriting(String text) {
		clientWriter.println(text);
		clientWriter.flush();
		try {
			String returnVal = clientReader.readLine();
			return returnVal;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
