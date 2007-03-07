package edu.stanford.hci.r3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import edu.stanford.hci.r3.devices.Device;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.batch.BatchedEventHandler;
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
	 * Batched event handlers allow you to get at data that was synchronized through the USB dock.
	 * In the future, these event handlers will work exactly the same as real-time event handlers.
	 */
	private List<BatchedEventHandler> batchEventHandlers = new ArrayList<BatchedEventHandler>();

	/**
	 * An application can also coordinate multiple devices. A remote collaboration application might
	 * have to ask the user to input the device's hostname, for example.
	 */
	private List<Device> devices = new ArrayList<Device>();

	/**
	 * Which toolkit object is currently running/hosting this application?
	 */
	private PaperToolkit host = null;

	/**
	 * Are we currently at RUNTIME?
	 */
	private boolean isRunning = false;

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
	 * Should the user decide where to render the PDF?
	 */
	private boolean userChoosesPDFDestination = false;

	/**
	 * @param theName
	 */
	public Application(String theName) {
		name = theName;
	}

	/**
	 * @param beh
	 */
	public void addBatchEventHandler(BatchedEventHandler beh) {
		batchEventHandlers.add(beh);
	}

	/**
	 * @param dev
	 */
	public void addDevice(Device dev) {
		devices.add(dev);
	}

	/**
	 * Add a pen for this application. An application may have multiple pens.
	 * 
	 * @param pen
	 */
	public void addPen(Pen pen) {
		pens.add(pen);
	}

	/**
	 * When a sheet is added to an application, we will need to determine how the pattern maps to
	 * the sheet. We will create a PatternLocationToSheetLocationMapping object from this sheet.
	 * 
	 * WARNING: The current design REQUIRES you to add the sheet AFTER you have added regions to the
	 * sheet. This is an unfortunate design (ordering constraints), and should be changed _if
	 * possible_.
	 * 
	 * Alternative, we can warn when there is ambiguity in loading patternInfo files.
	 * 
	 * @param sheet
	 */
	public void addSheet(Sheet sheet) {
		// ensure that a mapping object is created
		final PatternLocationToSheetLocationMapping mapping = sheet
				.getPatternLocationToSheetLocationMapping();
		addSheetObjectToInternalList(sheet);
		registerMappingWithEventEngineDuringRuntime(mapping);
	}

	/**
	 * This method is better than the one argument version, because it makes everything explicit. We
	 * MAY deprecate the other one at some point.
	 * 
	 * @param sheet
	 * @param patternInfoFile
	 */
	public void addSheet(Sheet sheet, File patternInfoFile) {
		// ensure that a mapping object is created from this file
		PatternLocationToSheetLocationMapping mapping = sheet
				.getPatternLocationToSheetLocationMapping(patternInfoFile);
		addSheetObjectToInternalList(sheet);
		registerMappingWithEventEngineDuringRuntime(mapping);
	}

	/**
	 * This method may be the best of the three, because you explicitly construct the
	 * patternToSheetMapping (using any method you prefer). Highest flexibility, but possibly
	 * inconvenient.
	 * 
	 * @param sheet
	 * @param patternInfoFile
	 */
	public void addSheet(Sheet sheet, PatternLocationToSheetLocationMapping patternToSheetMapping) {
		sheet.setPatternLocationToSheetLocationMapping(patternToSheetMapping);
		addSheetObjectToInternalList(sheet);
		registerMappingWithEventEngineDuringRuntime(patternToSheetMapping);
	}

	/**
	 * Keeps track of the sheet, and a single pattern to sheet mapping.
	 * 
	 * @param sheet
	 * @param patternToSheetMapping
	 */
	private void addSheetObjectToInternalList(Sheet sheet) {
		if (sheets.contains(sheet)) {
			DebugUtils.println("Already added this sheet: " + sheet);
		} else {
			DebugUtils.println("Adding Sheet: " + sheet);
			sheets.add(sheet);
		}
	}

	/**
	 * A Paper Application needs BatchEventHandlers to detect when a person synchronizes a pen with
	 * the computer. In the future, this may be integrated with standard event handling on a region.
	 * Right now, this is not possible, as we need the ability to calibrate the batched coordinates
	 * (which are processed by the PAD files) with the streaming coordinates.
	 * 
	 * @return
	 */
	public List<BatchedEventHandler> getBatchEventHandlers() {
		return batchEventHandlers;
	}

	/**
	 * @return the toolkit object that is running this application.
	 */
	public PaperToolkit getHostToolkit() {
		return host;
	}

	/**
	 * @return the application's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * We can calculate this set at paper-application runtime, because each sheet has a reference to
	 * its pattern map.
	 * 
	 * @return the information that maps a pattern location to a location on a sheet.
	 */
	public Collection<PatternLocationToSheetLocationMapping> getPatternMaps() {
		Collection<PatternLocationToSheetLocationMapping> map = new ArrayList<PatternLocationToSheetLocationMapping>();
		for (Sheet s : getSheets()) {
			map.add(s.getPatternLocationToSheetLocationMapping());
		}
		return map;
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
	 * Called right before an applications starts. Override to do anything you like right after a
	 * person clicks start, and right before the application actually starts.
	 */
	protected void initializeBeforeStarting() {
		// do nothing, unless it is overridden.
	}

	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @return whether to let the user choose where to render the Sheets...
	 */
	public boolean isUserChoosingDestinationForPDF() {
		return userChoosesPDFDestination;
	}

	/**
	 * Only if this app is currently running, we will ask the
	 * 
	 * @param mapping
	 */
	private void registerMappingWithEventEngineDuringRuntime(
			PatternLocationToSheetLocationMapping mapping) {
		if (isRunning) {
			// tell the already-running event engine to be aware of this new pattern mapping!
			getHostToolkit().getEventEngine().registerPatternMapForEventHandling(mapping);
		}
	}

	/**
	 * Useful for manipulating the application at RUNTIME.
	 */
	public void removeAllSheets() {
		sheets.clear();
	}

	/**
	 * @param beh
	 */
	public void removeBatchEventHandler(BatchedEventHandler beh) {
		batchEventHandlers.remove(beh);
	}

	/**
	 * We can add and remove sheets before the application starts. However, this is also useful for
	 * manipulating the application at RUNTIME.
	 * 
	 * @param sheet
	 */
	public void removeSheet(Sheet sheet) {
		if (sheets.contains(sheet)) {
			sheets.remove(sheet);
		}

		// unregister this sheet's mapping information
		// so that the EventEngine won't dispatch events to this sheet.
		if (isRunning) {
			getHostToolkit().getEventEngine().unregisterPatternMapForEventHandling(
					sheet.getPatternLocationToSheetLocationMapping());
		}
	}

	/**
	 * Feel free to OVERRIDE this too. It is called if the userChoosesPDFDestination flag is set to
	 * false, and the user presses the Render PDF Button in the App Manager.
	 */
	public void renderToPDF() {
		renderToPDF(FileSystemView.getFileSystemView().getHomeDirectory(), getName());
	}

	/**
	 * <p>
	 * Renders all of the sheets to different PDF files... If there are four Sheets, it will make
	 * files as follows:
	 * </p>
	 * <code>
	 * parentDirectory <br>
	 * |_fileName_1.pdf <br>
	 * |_fileName_2.pdf <br>
	 * |_fileName_3.pdf <br>
	 * |_fileName_4.pdf <br>
	 * </code>
	 * <p>
	 * Feel Free to OVERRIDE this method if you want to attach different behavior to the App
	 * Manager's RenderPDF Button.
	 * </p>
	 */
	public void renderToPDF(File parentDirectory, String fileNameWithoutExtension) {
		if (sheets.size() == 1) {
			DebugUtils.println("Rendering PDF...");
			final Sheet sheet = sheets.get(0);
			final File destPDFFile = new File(parentDirectory, fileNameWithoutExtension + ".pdf");
			System.out.println("Rendering: " + destPDFFile.getAbsolutePath());
			final SheetRenderer renderer = sheet.getRenderer();
			renderer.renderToPDF(destPDFFile);
		} else {
			DebugUtils.println("Rendering PDFs...");
			for (int i = 0; i < sheets.size(); i++) {
				final Sheet sheet = sheets.get(i);
				final File destPDFFile = new File(parentDirectory, fileNameWithoutExtension
						+ "_Sheet_" + i + ".pdf");
				System.out.println("Rendering: " + destPDFFile.getAbsolutePath());
				final SheetRenderer renderer = sheet.getRenderer();
				renderer.renderToPDF(destPDFFile);
			}
		}

	}

	/**
	 * Set it to null when the application is not running. Set it to a valid toolkit object when the
	 * application is running. This allows the application to access the toolkit object that is
	 * hosting it, during RUNTIME.
	 * 
	 * @param toolkit
	 */
	public void setHostToolkit(PaperToolkit toolkit) {
		host = toolkit;
		if (host != null) {
			setRunning(true);
		} else {
			setRunning(false);
		}
	}

	/**
	 * Used internally by setHostToolkit. When the toolkit is set, the application is running, by
	 * implication. If the host toolkit is null, then the application has stop running.
	 * 
	 * @param flag
	 */
	private void setRunning(boolean flag) {
		isRunning = flag;
	}

	/**
	 * @param flag
	 */
	public void setUserChoosesPDFDestinationFlag(boolean flag) {
		userChoosesPDFDestination = flag;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + " Application";
	}
}
