package edu.stanford.hci.r3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.events.EventEngine;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * An application will consist of Bundles and Sheets, and the actions that are bound to individual
 * regions. A PaperToolkit can load/run an Application. When an Application is running, all events
 * will go through the PaperToolkit's EventEngine.
 */
public class Application {

	/**
	 * All of an application's events are sent to the eventListener.
	 */
	private EventEngine eventListener;

	private String name;

	private List<Pen> pens = new ArrayList<Pen>();

	private List<Sheet> sheets = new ArrayList<Sheet>();

	/**
	 * @param theName
	 */
	public Application(String theName) {
		name = theName;
	}

	/**
	 * @param pen
	 */
	public void addPen(Pen pen) {
		pens.add(pen);
	}

	/**
	 * @param sheet
	 */
	public void addSheet(Sheet sheet) {
		sheets.add(sheet);
	}

	/**
	 * @return
	 */
	public List<Pen> getPens() {
		return pens;
	}

	/**
	 * Serializes an application to disk.
	 * 
	 * @param appDirectory
	 */
	public void saveToDisk(File appDirectory) {
		DebugUtils.println("Unimplemented Method");
	}

	/**
	 * @param eventEngine
	 */
	public void setApplicationEventListener(EventEngine eventEngine) {
		eventListener = eventEngine;
		// add all the live pens to the eventEngine
		for (Pen pen : pens) {
			if (pen.isLive()) {
				eventEngine.register(pen);
			}
		}
	}
}
