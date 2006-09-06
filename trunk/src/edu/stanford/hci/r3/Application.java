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
 * An application will consist of Bundles and Sheets, and the actions that are bound to individual
 * regions. A PaperToolkit can load/run an Application. When an Application is running, all events
 * will go through the PaperToolkit's EventEngine.
 * </p>
 * <p>
 * The Application will be able to dispatch events to the correct handlers. An application will also
 * be able to handle pens, but these pens must be registered with the PaperToolkit to enable the
 * event engine to do its work.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Application {

	/**
	 * The name of the application. Useful for debugging (e.g., when trying to figure out which
	 * application generated which event).
	 */
	private String name;

	/**
	 * An application will own a number of pens.
	 */
	private List<Pen> pens = new ArrayList<Pen>();

	/**
	 * An application contains multiple bundles, which in turn contain multiple sheets. In the
	 * simplest case, an application might contain one bundle which might be a single sheet (e.g., a
	 * GIGAprint).
	 * 
	 * For simplicity, we expand out Bundles and place the sheets directly in this datastructure.
	 */
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
	 * @return the application's name
	 */
	public String getName() {
		return name;
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

}
