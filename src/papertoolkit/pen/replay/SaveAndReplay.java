package papertoolkit.pen.replay;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import papertoolkit.PaperToolkit;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * This class interacts with the EventEngine to simulate real-time input events. The events can be loaded from
 * disk (XML files), and can be either batched or realtime events. Alternatively, events generated by an
 * actual pen can be saved out to a file, for future replay.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class SaveAndReplay {

	private static class ReplayEvent {
		public long gapTime = 0L;
		public String penID = "0";
		public PenSample sample;
		public ReplayEventType type;

		public ReplayEvent(ReplayEventType t) {
			type = t;
		}
	}

	private static enum ReplayEventType {
		PEN_CHANGE, SAMPLE, TIME_GAP
	}

	public class SaveAndReplayListener implements PenListener {
		private InputDevice inputDevice;

		public SaveAndReplayListener(InputDevice dev) {
			inputDevice = dev;
		}

		public void penDown(PenSample sample) {
			lazyInitOutputFile();
			checkIfInteractionGap(sample);
			checkIfNewPen(inputDevice);
			saveSample(sample);
		}

		public void penUp(PenSample sample) {
			checkIfNewPen(inputDevice);
			lastPenSampleTracked = sample;
			saveSample(sample);
		}

		public void sample(PenSample sample) {
			checkIfNewPen(inputDevice);
			lastPenSampleTracked = sample;
			saveSample(sample);
		}

		public String toString() {
			return "Save/Replay PenListener";
		}
	}

	/**
	 * Event Data files are of the form *.eventData.
	 */
	public static final String[] FILE_EXTENSION = new String[] { "eventData" };
	private static SaveAndReplay instance;
	private static final Pattern PATTERN_GAP_XML_FORMAT = Pattern.compile("<gap time=\"(.*?)\".*?/>");
	private static final Pattern PATTERN_PEN_XML_FORMAT = Pattern.compile("<pen id=\"(.*?)\".*?/>");

	public static synchronized SaveAndReplay getInstance() {
		if (instance == null) {
			instance = new SaveAndReplay();
		}
		return instance;
	}

	/**
	 * Events that we can replay...
	 */
	private List<ReplayEvent> eventsToReplay = new ArrayList<ReplayEvent>();

	private HashMap<InputDevice, PenListener> inputDeviceToListener = new HashMap<InputDevice, PenListener>();

	private HashMap<String, InputDevice> knownInputDevices = new HashMap<String, InputDevice>();

	private PenSample lastPenSampleTracked;

	/**
	 * 
	 */
	private InputDevice lastPenUsed;
	/**
	 * Allows us to write to our output file for serializing the event stream.
	 */
	private PrintWriter output;

	/**
	 * Should we play back the pen events in real time. That is, if there is a one second pause between two
	 * pen taps, true --> we replicate that one second pause, false --> we replay it as fast as possible.
	 */
	private boolean playEventsInRealTime = true;

	/**
	 * Record input at the InputDevice level, and replay to PenListeners.... This should work for multiple
	 * pens...
	 * 
	 * @param inputDevice
	 */
	private SaveAndReplay() {
		loadMostRecentSession();
	}

	private void checkIfInteractionGap(PenSample sample) {
		if (lastPenSampleTracked != null) {
			long diff = sample.timestamp - lastPenSampleTracked.timestamp;
			if (diff > 9000) { // 9 seconds
				output.println("<gap time=\"" + diff + "\"/>");
			}
		}
		lastPenSampleTracked = sample;
	}

	private void checkIfNewPen(InputDevice inputDevice) {
		// if it's a new pen, inject some xml to close the previous pen, and open a new one...
		if (inputDevice != lastPenUsed) {
			// add XML tag to tell us a pen was changed...
			output.println("<pen id=\"" + inputDevice.getID() + "\"/>");
		}
		lastPenUsed = inputDevice;
	}

	/**
	 * 
	 */
	private void clearLoadedEvents() {
		eventsToReplay = new ArrayList<ReplayEvent>();
	}

	private void countDownToReplay() {
		// count down from 4...3...2...1...replay!
		int count = 3;
		DebugUtils.println("Starting Replay in...");
		while (count > 0) {
			DebugUtils.println(count + "...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			count--;
		}
	}

	public ActionListener getActionListenerForChooseSession() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearLoadedEvents();
				loadEventDataFromFileChooser();
			}
		};
	}

	public ActionListener getActionListenerForLoadLatest() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// load the most recent, and then replay it!
				clearLoadedEvents();
				loadMostRecentSession();
			}
		};
	}

	public ActionListener getActionListenerForReplay() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// the replayed session will not be saved, as we save it at the InputDevice level
				countDownToReplay();
				replayLoadedEvents();
			}
		};
	}

	/**
	 * @return
	 */
	private File getEventStoragePath() {
		return new File(PaperToolkit.getToolkitRootPath(), "eventData/");
	}

	/**
	 * @return
	 */
	private InputDevice getFirstInputDevice() {
		Object[] devices = knownInputDevices.values().toArray();
		if (devices.length > 0) {
			return (InputDevice) devices[0];
		} else {
			return null;
		}
	}

	/**
	 * @param inputDevice
	 * @return
	 */
	public PenListener getPenListener(final InputDevice inputDevice) {
		// register this input device for later
		knownInputDevices.put(inputDevice.getID(), inputDevice);
		PenListener penListener = inputDeviceToListener.get(inputDevice);
		if (penListener == null) {
			penListener = new SaveAndReplayListener(inputDevice);
			inputDeviceToListener.put(inputDevice, penListener);
		}
		return penListener;
	}

	// if we never write to it, the file should never be created...
	private void lazyInitOutputFile() {
		if (output == null) {
			try {
				// Write events to disk (autoflushed), so that we can replay sessions in the
				// future.
				File outputFile = new File(getEventStoragePath(), FileUtils
						.getCurrentTimeForUseInASortableFileName()
						+ ".eventData");
				output = new PrintWriter(new FileOutputStream(outputFile), true /* autoflush */);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private void loadEventDataFromFileChooser() {
		JFileChooser chooser = FileUtils.createNewFileChooser(SaveAndReplay.FILE_EXTENSION);
		chooser.setCurrentDirectory(new File(PaperToolkit.getToolkitRootPath(), "eventData/"));
		chooser.setMultiSelectionEnabled(true);
		int result = chooser.showDialog(null, "Import Event Data");
		if (result == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = chooser.getSelectedFiles();
			for (File f : selectedFiles) {
				// DebugUtils.println("Loading " + f);
				loadSessionDataFrom(f);
			}
		}
	}

	/**
	 * Load the most recent event data file...
	 */
	public void loadMostRecentSession() {
		final List<File> eventFiles = FileUtils.listVisibleFiles(getEventStoragePath(), FILE_EXTENSION);
		if (eventFiles.size() > 0) {
			final File mostRecentFile = eventFiles.get(eventFiles.size() - 1);
			// DebugUtils.println("Loading Most Recent Session: " + mostRecentFile.getName());
			loadSessionDataFrom(mostRecentFile);
		} else {
			// DebugUtils.println("No Event Data Files Found in " + getEventStoragePath());
		}
	}

	/**
	 * @param eventDataFile
	 */
	public void loadSessionDataFrom(File eventDataFile) {
		DebugUtils.println("Loading Session Data from: " + eventDataFile.getName());
		int startingSize = eventsToReplay.size();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(eventDataFile));
			String inputLine = null;
			while ((inputLine = br.readLine()) != null) {
				ReplayEvent e;
				PenSample sample = PenSample.fromXMLString(inputLine);
				if (sample != null) {
					// this was a real sample
					e = new ReplayEvent(ReplayEventType.SAMPLE);
					e.sample = sample;
					eventsToReplay.add(e);
				} else {
					if (inputLine.startsWith("<pen ")) {
						e = new ReplayEvent(ReplayEventType.PEN_CHANGE);
						Matcher matcher = PATTERN_PEN_XML_FORMAT.matcher(inputLine);
						if (matcher.find()) {
							e.penID = matcher.group(1);
							eventsToReplay.add(e);
						}
					} else if (inputLine.startsWith("<gap ")) {
						e = new ReplayEvent(ReplayEventType.TIME_GAP);
						Matcher matcher = PATTERN_GAP_XML_FORMAT.matcher(inputLine);
						if (matcher.find()) {
							e.gapTime = Long.parseLong(matcher.group(1));
							eventsToReplay.add(e);
						}
					}
				}
			}
			DebugUtils.println("Size of the Replay Event Queue changed from " + startingSize + " to "
					+ eventsToReplay.size());
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Replay the events that have been loaded, in the order that they appear in the list...
	 */
	public void replayLoadedEvents() {
		replayToInputDevice(eventsToReplay);
	}

	/**
	 * Replays the list of events... Ideally, this should play it back at real time or some multiple of
	 * realtime...
	 * 
	 * Threaded, because we do not want any GUI to block when calling this. Alternatively, refactor this into
	 * blocking & nonblocking versions.
	 * 
	 * @param events
	 */
	private void replayToInputDevice(final List<ReplayEvent> events) {
		new Thread(new Runnable() {
			public void run() {
				String penID = "0";
				InputDevice currPenInputDevice = getFirstInputDevice();

				long lastTimeStamp = 0;
				HashMap<String, Boolean> penIsUp = new HashMap<String, Boolean>();

				for (ReplayEvent e : events) {
					switch (e.type) {
					case PEN_CHANGE:
						penID = e.penID;
						currPenInputDevice = knownInputDevices.get(penID);
						// DebugUtils.println("Changed to " + currPenInputDevice);

						// keep track of this pen's down or up state
						if (!penIsUp.keySet().contains(penID)) {
							penIsUp.put(penID, true);
						}
						break;
					case SAMPLE:
						// determine if it is a DOWN, REGULAR, or UP event.... and handle accordingly
						PenSample sample = e.sample;

						if (playEventsInRealTime && lastTimeStamp != 0) {
							// pause some amount, to replicate realtime...
							long diff = sample.getTimestamp() - lastTimeStamp;
							try {
								if (diff > 0) {
									// play in "real time"
									Thread.sleep(diff);
								} else if (diff < 0) {
									DebugUtils.println("Timestamps went backwards... "
											+ "Probably loaded new session.");
									Thread.sleep(2000);
								}
							} catch (InterruptedException ex) {
							}
						}

						if (currPenInputDevice == null) {
							// the pen ID doesn't match.. so we grab the first pen
							currPenInputDevice = getFirstInputDevice();
						}

						if (penIsUp.get(penID)) {
							penIsUp.put(penID, false); // pen just came down
							currPenInputDevice.playPenDown(sample);
						} else if (sample.isPenUp()) {
							penIsUp.put(penID, true); // pen just lifted
							currPenInputDevice.playPenUp(sample);
						} else {
							currPenInputDevice.playPenSample(sample);
						}
						lastTimeStamp = sample.getTimestamp();
						break;
					case TIME_GAP: // this was an extended gap (compress to 2 secs)
						long gapTime = e.gapTime;
						try {
							DebugUtils.println("Sleeping two seconds for the gap of: " + gapTime);
							Thread.sleep(2000);
						} catch (InterruptedException ex) {
						}
						lastTimeStamp = 0; // don't sleep the next time...
						break;
					default:
						break;
					}
				}
				DebugUtils.println("Done replaying " + events.size() + " events");
			}

		}).start();
	}

	/**
	 * Save this pen event. This is done automatically for events streamed through any Pen, allowing arbitrary
	 * save and replay of user interactions. One event file is created for each "test session."
	 * 
	 * @param event
	 */
	private void saveSample(PenSample sample) {
		output.println(sample.toXMLString());
	}

	public void populateBookmarks(Menu playBookmarked) {
		File bookmarked = new File(PaperToolkit.getToolkitRootPath(), "eventData/savedEventData");
		final List<File> eventFiles = FileUtils.listVisibleFiles(bookmarked, FILE_EXTENSION);
		for (final File f : eventFiles) {
			MenuItem m = new MenuItem(f.getName().replace(".eventData", ""));
			m.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					clearLoadedEvents();
					loadSessionDataFrom(f);
					countDownToReplay();
					replayLoadedEvents();
				}
			});
			playBookmarked.add(m);
		}
	}
}
