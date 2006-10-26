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

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Allows us to use the HWRecognition server (written in C#/.NET) from Java. This acts as a client that relays
 * messagse to our Handwriting Recognition Server.
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

	public static void main(String[] args) {
		HandwritingRecognitionService service = getInstance();
		String string = service.recognizeHandwriting("[[helloo]]");
		DebugUtils.println(string);
	}

	private boolean clientInitialized;

	private BufferedReader clientReader;

	private Socket clientSocket = null;

	private PrintWriter clientWriter;

	/**
	 * Has the server been started?
	 */
	private boolean serverStarted;

	/**
	 * This should only ever be called once, so we will start one server and one client.
	 */
	private HandwritingRecognitionService() {
		initializeServer();
		initializeClient();
	}

	/**
	 * 
	 */
	private void initializeClient() {
		if (clientInitialized) {
			return;
		}
		clientInitialized = true;
		try {
			clientSocket = new Socket("localhost", 9898);
			clientWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket
					.getOutputStream())));
			clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This recognize call should return as fast as possible... as an end user will experience this...
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

	private synchronized void notifyService() {
		notifyAll();
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
						"handwritingRec/bin/HandwritingRecognition.exe");
				ProcessBuilder builder = new ProcessBuilder(hwrec.getPath());
				try {
					Process process = builder.start();
					InputStream inputStream = process.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					line = br.readLine();
					DebugUtils.println(line); // Server Started Message...
					notifyService();
					while ((line = br.readLine()) != null) {
						DebugUtils.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();

		
		// but block until the server says it is started
		synchronized(this) {
			try {
				DebugUtils.println("Waiting until the Handwriting Recognition server starts.");
				wait();
				DebugUtils.println("The server has started, so we proceed....");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
