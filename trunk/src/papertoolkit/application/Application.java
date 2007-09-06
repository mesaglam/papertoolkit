package papertoolkit.application;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import papertoolkit.PaperToolkit;
import papertoolkit.devices.Device;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pen.PenInput;
import papertoolkit.render.SheetRenderer;
import papertoolkit.tools.debug.DebuggingEnvironment;
import papertoolkit.tools.design.swing.SheetFrame;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * The PaperToolkit approach suggests that you create Application objects to wrap your entire paper + digital
 * application. This is only ONE approach to solving the paper + digital integration, as you can actually use
 * PaperToolkit's components separately...
 * </p>
 * <p>
 * An application will consist of Bundles and Sheets, and the actions that are bound to individual regions. A
 * PaperToolkit can load/run an Application. When an Application is running, all events will go through the
 * PaperToolkit's EventEngine.
 * </p>
 * <p>
 * The Application will be able to dispatch events to the correct handlers. An application will also be able
 * to handle pens, but these pens must be registered with the PaperToolkit to enable the event engine to do
 * its work.
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
	 * For inspecting the application at runtime.
	 */
	private DebuggingEnvironment debuggingEnvironment;

	/**
	 * An application can also coordinate multiple devices. A remote collaboration application might have to
	 * ask the user to input the device's hostname, for example.
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
	 * The name of the application. Useful for debugging (e.g., when trying to figure out which application
	 * generated which event).
	 */
	private String name;

	/**
	 * An application will own a number of pen input devices. Most of the time, these are implemented by
	 * actual digital pens (Pen.java). Sometimes, they can be simulated... by anything that implements the
	 * PenIput interface..
	 */
	private List<PenInput> penInputDevices = new ArrayList<PenInput>();

	/**
	 * An application contains multiple bundles, which in turn contain multiple sheets. In the simplest case,
	 * an application might contain one bundle which might be a single sheet (e.g., a GIGAprint).
	 * 
	 * For simplicity, we expand out Bundles and place the sheets directly in this datastructure.
	 */
	private List<Sheet> sheets = new ArrayList<Sheet>();

	/**
	 * Should the user decide where to render the PDF?
	 */
	private boolean userChoosesPDFDestination = false;

	private static boolean isfirstAppPopulatingSystemTray = true;

	/**
	 * @param theName
	 */
	public Application(String theName) {
		name = theName;
	}

	/**
	 * @param dev
	 */
	public void addDevice(Device dev) {
		devices.add(dev);
	}

	/**
	 * Add a digital pen (or pen simulator) for this application. An application may have multiple pens.
	 * 
	 * @param pen
	 */
	public void addPenInput(PenInput penInputDevice) {
		penInputDevices.add(penInputDevice);
	}

	/**
	 * When a sheet is added to an application, we will need to determine how pattern coordinates map to the
	 * sheet's coordinates (i.e., we need a PatternToSheetMapping for this sheet).
	 * 
	 * @param sheet
	 */
	public void addSheet(Sheet sheet) {
		addSheet(sheet, sheet.getPatternToSheetMapping());
	}

	/**
	 * This method may be better, because you explicitly construct the patternToSheetMapping (using any method
	 * you prefer). More flexible, but less convenient.
	 * 
	 * @param sheet
	 * @param patternInfoFile
	 */
	public void addSheet(Sheet sheet, PatternToSheetMapping patternToSheetMapping) {
		sheet.setPatternToSheetMapping(patternToSheetMapping);
		addSheetObjectToInternalList(sheet);
		registerMappingWithEventEngineDuringRuntime(patternToSheetMapping);
		sheet.setParentApplication(this);
	}

	/**
	 * Keeps track of the sheet, and a single pattern to sheet mapping.
	 * 
	 * @param sheet
	 * @param patternToSheetMapping
	 */
	private void addSheetObjectToInternalList(Sheet sheet) {
		if (sheets.contains(sheet)) {
			// DebugUtils.println("Already added this sheet: " + sheet);
		} else {
			// DebugUtils.println("Adding Sheet: " + sheet);
			sheets.add(sheet);
		}
	}

	/**
	 * Create, add, and return a new sheet...
	 * 
	 * @return a new Sheet object with dimensions 8.5 x 11 inches
	 */
	public Sheet createSheet() {
		return createSheet(8.5, 11);
	}

	/**
	 * Create, add, and return a new sheet...
	 * 
	 * @return a new Sheet object.
	 */
	public Sheet createSheet(double widthInches, double heightInches) {
		final Sheet sheetToAdd = new Sheet(widthInches, heightInches);
		addSheet(sheetToAdd); // how do we map this sheet to some pattern location?
		return sheetToAdd;
	}

	/**
	 * @return
	 */
	public DebuggingEnvironment getDebuggingEnvironment() {
		if (debuggingEnvironment == null) {
			debuggingEnvironment = new DebuggingEnvironment(this);
		}

		return debuggingEnvironment;
	}

	/**
	 * Attaches to the popup menu. Allows us to drop into the Debug mode of a paper application, with event
	 * visualizations and stuff. =)
	 * 
	 * @param app
	 * @return
	 */
	private ActionListener getDebugListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DebuggingEnvironment debuggingEnvironment = getDebuggingEnvironment();
				debuggingEnvironment.showFlashView();
			}
		};
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
	 * We can calculate this set at paper-application runtime, because each sheet has a reference to its
	 * pattern map.
	 * 
	 * @return the information that maps a pattern location to a location on a sheet.
	 */
	public Collection<PatternToSheetMapping> getPatternMaps() {
		final Collection<PatternToSheetMapping> map = new ArrayList<PatternToSheetMapping>();
		for (Sheet s : getSheets()) {
			map.add(s.getPatternToSheetMapping());
		}
		return map;
	}

	/**
	 * @return the list of pens. The EventEngine will have to get the listeners to these pens...
	 */
	public List<PenInput> getPenInputDevices() {
		return penInputDevices;
	}

	/**
	 * @return
	 */
	public List<Sheet> getSheets() {
		return sheets;
	}

	/**
	 * Called right before an applications starts. Override to do anything you like right after a person
	 * clicks start, and right before the application actually starts.
	 */
	public void initializeBeforeStarting() {
		// do nothing, unless it is overridden.
	}

	/**
	 * @return true if this application is currently running.
	 */
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
	 * @param popupMenu
	 */
	public final void populateTrayMenu(PopupMenu popupMenu) {
		if (isfirstAppPopulatingSystemTray) {
			popupMenu.add(new MenuItem("-")); // separator
			isfirstAppPopulatingSystemTray = false;
		}
		
		final PopupMenu menu = new PopupMenu(getName());
		popupMenu.add(menu);

		final MenuItem debugItem = new MenuItem("Debug");
		debugItem.addActionListener(getDebugListener());

		final MenuItem renderItem = new MenuItem("Render " + sheets.size() + " Sheets to PDF");
		renderItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderToPDF();
			}
		});

		menu.add(debugItem);
		menu.add(renderItem);

		for (final Sheet s : getSheets()) {
			MenuItem item = new MenuItem("GUI Simulator for Sheet [" + s.getName() + "]");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SheetFrame(s, 640, 480);
				}
			});
			menu.add(item);
		}
		
		populateTrayMenuForSideCar(menu);
		populateTrayMenuExtensions(menu);
	}


	private final void populateTrayMenuForSideCar(PopupMenu popupMenu) {
		popupMenu.add("-");
		
		final MenuItem openSideCarItem = new MenuItem("Open SideCar Display");
		openSideCarItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DebugUtils.println("Opening Sidecar...");
				
				// opens the flex application
				
				// our eclipse is already instrumented with the SideCar plugin, so it's already listening on 
				// all the right ports
				
				// TODO:
			}
		});

		popupMenu.add(openSideCarItem);
	}
	
	/**
	 * This is an extension point. If you want to customize the tray menu, you can subclass this.
	 * 
	 * @param popupMenu
	 */
	public void populateTrayMenuExtensions(PopupMenu popupMenu) {
		// nothing; subclasses can use this
	}

	/**
	 * Only if this app is currently running, we will ask the event engine to register the pattern mapping so
	 * that we can handle real-time input events.
	 * 
	 * @param mapping
	 */
	private void registerMappingWithEventEngineDuringRuntime(PatternToSheetMapping mapping) {
		if (isRunning) {
			// tell the already-running event engine to be aware of this new pattern mapping!
			getHostToolkit().getEventDispatcher().registerPatternMapForEventHandling(mapping);
		}
	}

	/**
	 * Useful for manipulating the application at RUNTIME.
	 */
	public void removeAllSheets() {
		sheets.clear();
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
			getHostToolkit().getEventDispatcher().unregisterPatternMapForEventHandling(
					sheet.getPatternToSheetMapping());
		}
	}

	/**
	 * Feel free to OVERRIDE this too. It is called if the userChoosesPDFDestination flag is set to false, and
	 * the user presses the Render PDF Button in the App Manager.
	 */
	public void renderToPDF() {
		renderToPDF(FileSystemView.getFileSystemView().getHomeDirectory(), getName());
	}

	/**
	 * <p>
	 * Renders all of the sheets to different PDF files... If there are four Sheets, it will make files as
	 * follows:
	 * </p>
	 * <code>
	 * parentDirectory <br>
	 * |_fileName_1.pdf <br>
	 * |_fileName_2.pdf <br>
	 * |_fileName_3.pdf <br>
	 * |_fileName_4.pdf <br>
	 * </code>
	 * <p>
	 * Feel Free to OVERRIDE this method if you want to attach different behavior to the App Manager's
	 * RenderPDF Button.
	 * </p>
	 */
	public void renderToPDF(File parentDirectory, String fileNameWithoutExtension) {
		if (sheets.size() == 1) {
			// DebugUtils.println("Rendering PDF...");
			final Sheet sheet = sheets.get(0);
			final File destPDFFile = new File(parentDirectory, fileNameWithoutExtension + ".pdf");
			System.out.println("Rendering: " + destPDFFile.getAbsolutePath());
			final SheetRenderer renderer = sheet.getRenderer();
			renderer.renderToPDF(destPDFFile);
		} else {
			// DebugUtils.println("Rendering PDFs...");
			for (int i = 0; i < sheets.size(); i++) {
				final Sheet sheet = sheets.get(i);
				final File destPDFFile = new File(parentDirectory, fileNameWithoutExtension + "_Sheet_" + i
						+ ".pdf");
				System.out.println("Rendering: " + destPDFFile.getAbsolutePath());
				final SheetRenderer renderer = sheet.getRenderer();
				renderer.renderToPDF(destPDFFile);
			}
		}

	}

	/**
	 * 
	 */
	public void run() {
		PaperToolkit.getInstance().startApplication(this);
	}

	/**
	 * @param debugEnvironment
	 */
	public void setDebuggingEnvironment(DebuggingEnvironment debugEnvironment) {
		debuggingEnvironment = debugEnvironment;
	}

	/**
	 * Set it to null when the application is not running. Set it to a valid toolkit object when the
	 * application is running. This allows the application to access the toolkit object that is hosting it,
	 * during RUNTIME.
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
	 * Used internally by setHostToolkit. When the toolkit is set, the application is running, by implication.
	 * If the host toolkit is null, then the application has stop running.
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
