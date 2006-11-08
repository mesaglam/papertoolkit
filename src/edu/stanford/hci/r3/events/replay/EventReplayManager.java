package edu.stanford.hci.r3.events.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.EventEngine;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * This class interacts with the EventEngine to simulate real-time input events. The events can be
 * loaded from disk (XML files), and can be either batched or realtime events.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventReplayManager {

	/**
	 * We will dispatch events to this event engine, simulating input from one or more pens...
	 */
	private EventEngine eventEngine;

	/**
	 * Allows us to write to our output file for serializing the event stream.
	 */
	private PrintWriter output;

	/**
	 * Write events to disk (autoflushed), so that we can replay sessions in the future.
	 */
	private File outputFile;

	/**
	 * @param engine
	 */
	public EventReplayManager(EventEngine engine) {
		eventEngine = engine;
		outputFile = new File(PaperToolkit.getToolkitRootPath(), "eventData/"
				+ FileUtils.getCurrentTimeForUseInASortableFileName() + ".eventData");
		try {
			output = new PrintWriter(new FileOutputStream(outputFile), true /* autoflush */);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param events
	 */
	public void replay(List<PenEvent> events) {
		for (PenEvent event : events) {
			// assume here that all PenEvent objects have their flags set correctly
			eventEngine.handlePenEvent(event);
		}
	}

	/**
	 * @param event
	 */
	public void saveEvent(PenEvent event) {
		output.println(PaperToolkit.toXMLNoLineBreaks(event));
	}
}
