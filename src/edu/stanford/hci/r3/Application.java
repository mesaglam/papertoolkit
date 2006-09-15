package edu.stanford.hci.r3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.render.SheetRenderer;
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
	 * For each sheet, we need to keep the pattern to sheet location map. This lets us know, given
	 * some physical coordinate, where we are on the sheet (i.e., which regions we point to).
	 */
	private Map<Sheet, PatternLocationToSheetLocationMapping> sheetToPatternMap = new HashMap<Sheet, PatternLocationToSheetLocationMapping>();

	/**
	 * @param theName
	 */
	public Application(String theName) {
		name = theName;

		initializePaperUI();
		initializeEventHandlers();
		initializeInputAndOutputDevices();
	}

	/**
	 * @param pen
	 */
	public void addPen(Pen pen) {
		pens.add(pen);
	}

	/**
	 * When a sheet is added to an application, we will need to determine how the pattern maps to
	 * the sheet. We will create a PatternLocationToSheetLocationMapping object from this sheet.
	 * 
	 * @param sheet
	 */
	public void addSheet(Sheet sheet) {
		sheets.add(sheet);
		sheetToPatternMap.put(sheet, new PatternLocationToSheetLocationMapping(sheet));
	}

	/**
	 * @return the application's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the information that maps a pattern location to a location on a sheet.
	 */
	public Collection<PatternLocationToSheetLocationMapping> getPatternMaps() {
		return sheetToPatternMap.values();
	}

	/**
	 * @return
	 */
	public List<Pen> getPens() {
		return pens;
	}

	/**
	 * @return
	 */
	public List<Sheet> getSheets() {
		return sheets;
	}

	/**
	 * Add Event Handlers Here. Do nothing unless it is overridden by a subclass.
	 */
	protected void initializeEventHandlers() {
		// do nothing, unless it is overridden.
	}

	/**
	 * This is an empty initialization method that developers can override if they choose to
	 * subclass an Application instead of creating an empty App and adding sheets to it.
	 * 
	 * It is called by the constructor.
	 */
	protected void initializePaperUI() {
		// do nothing, unless it is overridden.
	}
	
	/**
	 * Pens and Displays, etc.
	 */
	protected void initializeInputAndOutputDevices() {
		// do nothing, unless it is overriden
	}

	/**
	 * <p>
	 * Renders all of the sheets to different PDF files... If there are four Sheets, it will make
	 * files as follows:<p/>
	 * 
	 * <code>
	 * parentDirectory <br>
	 * |_fileName_1.pdf <br>
	 * |_fileName_2.pdf <br>
	 * |_fileName_3.pdf <br>
	 * |_fileName_4.pdf <br>
	 * </code>
	 */
	public void renderToPDF(File parentDirectory, String fileNameWithoutExtension) {
		DebugUtils.println("Rendering PDFs...");
		for (int i = 0; i < sheets.size(); i++) {
			final Sheet sheet = sheets.get(i);
			final File destPDFFile = new File(parentDirectory, fileNameWithoutExtension + "_Sheet_"
					+ i + ".pdf");
			System.out.println("Rendering: " + destPDFFile.getAbsolutePath());
			final SheetRenderer renderer = sheet.getRenderer();
			renderer.renderToPDF(destPDFFile);
			renderer.savePatternInformation(); // do this automatically
		}
	}

	/**
	 * Serializes an application to disk. For now, uses the XML serialization. It might not be the
	 * best...
	 * 
	 * @param appDirectory
	 */
	public void saveToDisk(File appFile) {
		PaperToolkit.toXML(this, appFile);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + " Application";
	}
}
