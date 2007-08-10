package papertoolkit.pen.handwriting;

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

import papertoolkit.PaperToolkit;
import papertoolkit.config.Constants;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;


/**
 * <p>
 * Allows us to use the HWRecognition server (written in C#/.NET) from Java.
 * This acts as a client that relays messagse to our Handwriting Recognition
 * Server.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a>
 *         (ronyeh(AT)cs.stanford.edu)
 */
public class HandwritingRecognitionService {

	private static final int HWREC_PORT = Constants.Ports.HANDWRITING_RECOGNITION;

	private static final String HWREC_SERVER = "localhost";

	private static HandwritingRecognitionService instance;

	private static final String HW_REC_SERVER_EXE = "HandwritingRecognition.exe";

	private static final String REL_PATH_TO_HWREC_SERVER = "handwritingRec/HWRecServer/bin/Release/";

	/**
	 * @return access to the HWRec Server.
	 */
	public synchronized static HandwritingRecognitionService getInstance() {
		if (instance == null) {
			instance = new HandwritingRecognitionService();
		}
		return instance;
	}

	private boolean clientInitialized;

	private BufferedReader clientReader;

	private Socket clientSocket;

	/**
	 * Write to this to send information to the server.
	 */
	private PrintWriter clientWriter;

	/**
	 * Has the server been started?
	 */
	private boolean serverStarted;

	/**
	 * This should only ever be called once, so we will start one server and one
	 * client.
	 */
	private HandwritingRecognitionService() {
		initializeServer();
		connectToServer();

		// Exit the Server upon shutdown...
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				HandwritingRecognitionService.getInstance().exitServer();
			}
		}));
	}

	/**
	 * Connect to the HWRecognition Server...
	 */
	private synchronized void connectToServer() {
		if (clientInitialized) {
			return;
		}

		while (!serverStarted) {
			try {
				DebugUtils.println("Waiting for the server to start up...");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			clientSocket = new Socket(HWREC_SERVER, HWREC_PORT);
			clientWriter = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream())));
			clientReader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			clientInitialized = true;
		} catch (UnknownHostException e) {
			DebugUtils.println("Unknown Host: " + e.getLocalizedMessage());
			clientInitialized = false;
		} catch (IOException e) {
			DebugUtils.println("IOException: " + e.getLocalizedMessage());
			clientInitialized = false;
		}
	}

	/**
	 * 
	 */
	private void disconnectClient() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ask the server to exit.... You can only call this once!
	 */
	public void exitServer() {
		clientWriter.println("[[quitserver]]");
		clientWriter.flush();
		disconnectClient();
	}

	/**
	 * @return ask the server to provide the top ten alternatives... The server
	 *         will respond with a list, and terminate that list with a token
	 *         [[endofalternatives]]
	 */
	public List<String> getAlternatives() {
		clientWriter.println("[[topten]]");
		clientWriter.flush();
		String line = null;
		final List<String> alternatives = new ArrayList<String>();
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
	 * Call this only once! Runs the .exe file.
	 */
	private void initializeServer() {

		final Object mutex = this;

		// start a thread to start the server
		new Thread(new Runnable() {
			public void run() {
				// off of the PaperToolkit directory...
				// handwritingRec\bin\HandwritingRecognition.exe
				final File hwrecPath = new File(PaperToolkit
						.getToolkitRootPath(), REL_PATH_TO_HWREC_SERVER);
				final File hwrecExe = new File(hwrecPath, HW_REC_SERVER_EXE);
				final ProcessBuilder builder = new ProcessBuilder(hwrecExe
						.getPath());
				builder.directory(hwrecPath);
				try {
					final Process process = builder.start();

					DebugUtils.println(builder.directory());

					final InputStream inputStream = process.getInputStream();
					final BufferedReader br = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					// trap the console output
					while ((line = br.readLine()) != null) {
						System.out.println("Handwriting Server: " + line);
						synchronized (mutex) {
							if (line.contains("[[serverstarted]]")) {
								serverStarted = true;
								mutex.notifyAll();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	/**
	 * @param xmlFile
	 * @return
	 */
	public String recognizeHandwriting(File xmlFile) {
		return recognizeHandwriting(FileUtils.readFileIntoStringBuffer(xmlFile,
				false).toString());
	}

	/**
	 * This recognize call should return as fast as possible... as an end user
	 * will experience this...
	 * 
	 * @param xml
	 * @return
	 */
	public String recognizeHandwriting(String xml) {
		clientWriter.println(xml);
		clientWriter.flush();
		try {
			final String returnVal = clientReader.readLine();
			return returnVal;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
