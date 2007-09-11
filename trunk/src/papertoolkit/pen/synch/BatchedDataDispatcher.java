package papertoolkit.pen.synch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import papertoolkit.PaperToolkit;
import papertoolkit.application.config.Constants;
import papertoolkit.events.EventDispatcher;
import papertoolkit.events.PenEvent;
import papertoolkit.events.PenEventType;
import papertoolkit.pattern.PatternPackage;
import papertoolkit.pattern.coordinates.PageAddress;
import papertoolkit.pen.PenSample;
import papertoolkit.units.PatternDots;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * Wait at a socket (say: 9999) and receive the location of xml files that contain data from a pen synch
 * action (the user drops the pen into the dock). Then, process them and call any event handlers you have
 * registered at runtime.
 * 
 * Additionally, translate the batched data into streaming coordinates, and pass these events as if they
 * happened in real time over to the event engine.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BatchedDataDispatcher {

	/**
	 * .*? is a reluctant matcher (i.e., not greedy)
	 */
	private static final String BEGIN_PAGE_TAG = "<page address=\"(.*?)\".*?>";

	private static final String BEGIN_SAMPLE_TAG_END_SAMPLE_TAG = "<p x=\"(.*?)\" y=\"(.*?)\" f=\"(.*?)\" t=\"(.*?)\".*?/>";

	private static final String BEGIN_STROKE_TAG = "<stroke begin=\"(.*?)\".*?>";

	/**
	 * Will listen on this port for text commands.
	 */
	public static final int DEFAULT_PLAINTEXT_PORT = Constants.Ports.BATCH_SERVER;

	/**
	 * the end tag
	 */
	private static final String END_PAGE_TAG = "</page>";

	private static final String END_STROKE_TAG = "</stroke>";

	/**
	 * Tells the server that a client wishes to exit. Closes the client's handler.
	 */
	public static final String EXIT_COMMAND = "[[exit]]";

	private static final Pattern PATTERN_BEGIN_PAGE = Pattern.compile(BEGIN_PAGE_TAG);

	private static final Pattern PATTERN_BEGIN_STROKE = Pattern.compile(BEGIN_STROKE_TAG);

	private static final Pattern PATTERN_END_PAGE = Pattern.compile(END_PAGE_TAG);

	private static final Pattern PATTERN_END_STROKE = Pattern.compile(END_STROKE_TAG);

	private static final Pattern PATTERN_SAMPLE = Pattern.compile(BEGIN_SAMPLE_TAG_END_SAMPLE_TAG);

	/**
	 * 
	 */
	private List<Socket> clients = new ArrayList<Socket>();

	/**
	 * We will pass batched events to this event dispatcher, to simulate event dispatch in "real time".
	 */
	private EventDispatcher eventDispatcher;

	/**
	 * Close the batch server if this is ever set to true.
	 */
	private boolean exitFlag = false;

	/**
	 * TODO: Used for converting batched coordinates to streaming coordinates, which can be passed to our
	 * event handlers.
	 */
	private Map<String, PatternPackage> patternPackages = PatternPackage
			.getAvailablePatternPackages(PaperToolkit.getPatternPath());

	private PatternDots referenceUnit = new PatternDots();

	/**
	 * Wait for communication on a socket at this port.
	 */
	private int serverPort;

	/**
	 * The socket over which we receive incoming pen data.
	 */
	private ServerSocket serverSocket;

	/**
	 * @param eventEngine
	 */
	public BatchedDataDispatcher(EventDispatcher theEventEngine) {
		try {
			eventDispatcher = theEventEngine;
			serverSocket = new ServerSocket(DEFAULT_PLAINTEXT_PORT);
			serverPort = DEFAULT_PLAINTEXT_PORT;
			// start thread to accept connections
			getDaemonThread().start();
		} catch (IOException e) {
			System.out.println("Error with server socket: " + e.getLocalizedMessage());
		}
	}

	/**
	 * @param xmlDataFile
	 */
	public void batchedDataArrived(File xmlDataFile) {
		// parse it like we used to do... in BNet
		DebugUtils.println("BatchEventHandler got the file: " + xmlDataFile);

		// read in the whole request file into a String
		// is this an issue if the xml file is large, say 20MB?
		final StringBuilder requestBuffer = FileUtils.readFileIntoStringBuffer(xmlDataFile);

		final Matcher matcherPageBegin = PATTERN_BEGIN_PAGE.matcher(requestBuffer);
		final Matcher matcherPageEnd = PATTERN_END_PAGE.matcher(requestBuffer);

		// for simulating realtime
		long lastPenUpTimestamp = 0L;
		final String penName = "Batched Pen";

		while (matcherPageBegin.find() && matcherPageEnd.find()) {
			// DebugUtils.println("Processing Page: ");

			// location of the opening tag <page ...>
			final int beginTagEndIndex = matcherPageBegin.end();
			final int beginTagStartIndex = matcherPageBegin.start();

			// location of the closing tag </page>
			final int endTagStartIndex = matcherPageEnd.start();
			final int endTagEndIndex = matcherPageEnd.end();

			// DebugUtils.println(BEGIN_PAGE_TAG + " found at " + beginTagStartIndex + " to "
			// + beginTagEndIndex);
			// DebugUtils.println(END_PAGE_TAG + " found at " + endTagStartIndex + " to "
			// + endTagEndIndex);

			// extract page address
			final String pageAddress = matcherPageBegin.group(1);
			// DebugUtils.println("Page Address: " + pageAddress);

			// save where we got this ink, so we will know later on...
			final PageAddress address = new PageAddress(pageAddress);

			// extract front and end matter
			// final String beginText = requestBuffer.substring(beginTagStartIndex, beginTagEndIndex);
			// final String endText = requestBuffer.substring(endTagStartIndex, endTagEndIndex);

			// extract text in between the begin and end tags
			final String insideText = requestBuffer.substring(beginTagEndIndex, endTagStartIndex);
			// System.out.println("Internal Text Length: " + insideText.length());
			// System.out.println(insideText);

			final Matcher matcherStrokeBegin = PATTERN_BEGIN_STROKE.matcher(insideText);
			final Matcher matcherStrokeEnd = PATTERN_END_STROKE.matcher(insideText);
			// look through the strokes for this page
			while (matcherStrokeBegin.find() && matcherStrokeEnd.find()) {
				final String strokeTimeStamp = matcherStrokeBegin.group(1);
				final long ts = Long.parseLong(strokeTimeStamp);
				// date/time of the beginning of the stroke!
				DebugUtils.println("New Batched Stroke at Time: " + new Date(ts));

				// samples between the <stroke...></stroke>
				final String strokeSampleText = insideText.substring(matcherStrokeBegin.end(),
						matcherStrokeEnd.start());

				// store all the pen samples
				final List<PenSample> samples = new ArrayList<PenSample>();

				final Matcher matcherSample = PATTERN_SAMPLE.matcher(strokeSampleText);
				while (matcherSample.find()) {
					final String x = matcherSample.group(1);
					final String y = matcherSample.group(2);
					final String f = matcherSample.group(3);
					final String t = matcherSample.group(4);

					// make samples and stuff.... add it to the ink
					// DebugUtils.println(x + " " + y + " f=" + f + " ts=" + t);

					final PenSample sample = new PenSample(Double.parseDouble(x), Double.parseDouble(y),
							Integer.parseInt(f), Long.parseLong(t));
					samples.add(sample);
				}

				
				// dispatch the whole pen stroke
				// TODO Figure out the handling with multiple pens, etc... at some point
				// Figure out how to handle this in simulated REAL-TIME
				for (int i = 0; i < samples.size(); i++) {
					if (i == 0) {
						long timeDiff = samples.get(i).timestamp - lastPenUpTimestamp;
						if (timeDiff > 1000) { // 1 second, then we just wait one second
							timeDiff = 1000L;
						}
						try {
							// pause a bit, up to 1 second... before triggering a new down sample
							// this avoids the jitter filtering we have in some of the handler classes...
							// TODO: We should remove that filtering, as we now have it in PenClient =\
							Thread.sleep(timeDiff);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// DebugUtils.println("DOWN");
						eventDispatcher.handlePenEvent(new PenEvent(0, penName, System.currentTimeMillis(),
								samples.get(i), PenEventType.DOWN, false));

					} else if (i == samples.size() - 1) {
						// DebugUtils.println("UP");
						eventDispatcher.handlePenEvent(new PenEvent(0, penName, System.currentTimeMillis(),
								samples.get(i), PenEventType.UP, false));
						lastPenUpTimestamp = samples.get(i).timestamp;
					} else {
						// DebugUtils.println("P");
						eventDispatcher.handlePenEvent(new PenEvent(0, penName, System.currentTimeMillis(),
								samples.get(i), PenEventType.SAMPLE, false));
					}
				}
			}
		}
	}

	/**
	 * @param clientSocket
	 * @return
	 */
	private Thread getClientHandlerThread(final Socket clientSocket) {
		return new Thread() {

			/**
			 * Read from the client through this reader.
			 */
			private BufferedReader br;

			/**
			 * Disconnect from the import client (the C# monitor)
			 */
			public synchronized void disconnect() {
				try {
					if (!clientSocket.isClosed()) {
						clientSocket.close();
					}
					if (br != null) {
						br.close();
						br = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void run() {
				try {
					final InputStream inputStream = clientSocket.getInputStream();
					br = new BufferedReader(new InputStreamReader(inputStream));
					String line = null;
					while ((line = br.readLine()) != null) {
						// DebugUtils.println(line);
						if (line.toLowerCase().equals(EXIT_COMMAND)) {
							break;
						}

						// the server's exit flag
						// it can kill all clients at the same time
						if (exitFlag) {
							break;
						}

						// the file name is everything after...
						if (line.toLowerCase().startsWith("xml: ")) {
							// get the file name of the xml file
							final String fileName = line.substring(5).trim();
							// DebugUtils.println(fileName); // everything afterward
							final File xmlFile = new File(fileName);
							DebugUtils.println("Retrieving: " + xmlFile.getAbsolutePath());
							if (xmlFile.exists()) {
								// System.out.println("The file exists!");
								// send the xml file to the batched event handler...
								batchedDataArrived(xmlFile);
							} else {
								DebugUtils.println("The file does not exist. =(");
							}
						}
					}
					// DebugUtils.println("Import Thread Finished...");
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnect();
			}

		};
	}

	/**
	 * @return the server thread.
	 */
	private Thread getDaemonThread() {
		return new Thread() {

			public void run() {
				while (true) {
					Socket client = null;
					try {
						if (exitFlag) {
							DebugUtils.println("Closing BatchedDataDispatcher.");
							break;
						}

						// DebugUtils.println("Waiting for a connection on port [" + serverPort + "]");

						client = serverSocket.accept();

						final InetAddress inetAddress = client.getInetAddress();
						final String ipAddr = inetAddress.toString();
						final String dnsName = inetAddress.getHostName();

						// we got a connection with the client
						// DebugUtils.println("Got a connection on server port " + serverPort);
						// DebugUtils.println(" from client: " + ipAddr + " :: " + dnsName);

						// keep it around
						clients.add(client);
						getClientHandlerThread(client).start();
					} catch (IOException ioe) {
						DebugUtils.println("Error with server socket: " + ioe.getLocalizedMessage());
					}
				}
			}
		};
	}

	/**
	 * Tell the server to stop sending actions.
	 */
	public void stopDaemon() {
		try {
			exitFlag = true;
			for (Socket client : clients) {
				client.close();
			}
			System.out.println("BatchedDataDispatcher at port " + serverSocket.getLocalPort()
					+ " is stopping...");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
