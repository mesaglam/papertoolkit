package papertoolkit.application;

import java.awt.Desktop;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

import papertoolkit.PaperToolkit;
import papertoolkit.devices.Device;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.StrokeHandler;
import papertoolkit.external.flash.FlashWhiteboard;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pattern.coordinates.conversion.PatternCoordinateConverter;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.PenSimulator;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.render.SheetRenderer;
import papertoolkit.tools.debug.DebuggingEnvironment;
import papertoolkit.tools.design.acrobat.AcrobatDesignerLauncher;
import papertoolkit.tools.design.swing.SheetFrame;
import papertoolkit.units.PatternDots;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.components.EndlessProgressDialog;
import papertoolkit.util.files.FileUtils;

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
	private List<InputDevice> penInputDevices = new ArrayList<InputDevice>();

	/**
	 * An application contains multiple bundles, which in turn contain multiple sheets. In the simplest case,
	 * an application might contain one bundle which might be a single sheet (e.g., a GIGAprint).
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
	public void addPenInput(InputDevice penInputDevice) {
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
	public List<InputDevice> getPenInputDevices() {
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
	 * clicks start, and right before the application actually starts. If you override, make sure you call the
	 * parent's method...
	 */
	public void initializeBeforeStarting() {
		// try to load the pattern mappings from last time, if they exist
		loadMostRecentPatternMappings();
	}

	/**
	 * @return true if this application is currently running.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param popupMenu
	 */
	public final void populateTrayMenu(PopupMenu popupMenu) {


		final Menu menu = new Menu(getName());
		popupMenu.add(menu);

		final MenuItem startStopItem = new MenuItem("Stop Application");
		startStopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (isRunning) {
					host.stopApplication(Application.this);
					startStopItem.setLabel("Start Application");
				} else {
					host.startApplication(Application.this);
					startStopItem.setLabel("Stop Application");
				}
			}
		});
		menu.add(startStopItem);

		final MenuItem debugItem = new MenuItem("Debug Application");
		debugItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DebuggingEnvironment debuggingEnvironment = getDebuggingEnvironment();
				debuggingEnvironment.showFlashView();
			}
		});
		menu.add(debugItem);

		final Menu interactionsMenu = new Menu("Interactions");
		menu.add(interactionsMenu);
		populateInteractionsMenu(interactionsMenu);

		final Menu sheetsMenu = new Menu("Paper UI");
		menu.add(sheetsMenu);
		populateSheetsMenu(sheetsMenu);

		// to display incoming ink
		final MenuItem whiteboardItem = new MenuItem("Open Ink Display");
		whiteboardItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// whiteboard should be an internal option...
				FlashWhiteboard whiteboard = new FlashWhiteboard(8989);
				for (InputDevice device : penInputDevices) {
					whiteboard.addPen(device);
				}
				whiteboard.load();
			}
		});
		menu.add(whiteboardItem);

		populateTrayMenuExtensions(menu);
	}

	private void populateSheetsMenu(final Menu sheetsMenu) {

		// load the Acrobat plugin for designing Paper UIs (drawing out regions)...
		final MenuItem designItem = new MenuItem("Design Sheets");
		designItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFrame frame = AcrobatDesignerLauncher.start();
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			}
		});
		sheetsMenu.add(designItem);

		final MenuItem renderItem = new MenuItem("Render PDFs of " + sheets.size() + " sheets");
		renderItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderToPDF();
			}
		});
		sheetsMenu.add(renderItem);

		final MenuItem printSheetsItem = new MenuItem("Render PDFs of " + sheets.size() + " sheets to...");
		printSheetsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderPDFToSpecificFolder();
			}
		});
		sheetsMenu.add(printSheetsItem);

		final MenuItem printSheetInfoItem = new MenuItem("Display UI Information");
		printSheetInfoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final List<Sheet> thisAppsSheets = getSheets();
				for (Sheet s : thisAppsSheets) {
					// use the longer, more descriptive string
					DebugUtils.println("\n" + s.toDetailedString());
				}
			}
		});
		sheetsMenu.add(printSheetInfoItem);
	}

	private void populateInteractionsMenu(final Menu interactionsMenu) {
		MenuItem simulateItem = new MenuItem("Start Pen Simulator");
		simulateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				host.stopApplication(Application.this);
				Application.this.addPenInput(new PenSimulator());
				host.startApplication(Application.this);
			}
		});
		interactionsMenu.add(simulateItem);

		// will populate the system tray with a feature for runtime binding of regions... =)
		addItemsToBindUninitializedRegions(interactionsMenu);
	}

	/**
	 * A unique pattern info file for each sheet... We don't want to clobber the same patternInfo file if we
	 * have multiple sheets.
	 * 
	 * @param map
	 * @return
	 */
	private File getPatternInfoFile(PatternToSheetMapping map) {
		File dir = PaperToolkit.getToolkitFile("/mappings/");
		final File destFile = new File(dir, getName() + "." + map.getSheet().getName() + ".patternInfo.xml");
		return destFile;
	}

	/**
	 * Check for uninitialized regions, and then populate the menu with options to bind these regions to
	 * pattern at runtime!
	 * 
	 * @param mappings
	 */
	private void addItemsToBindUninitializedRegions(Menu popupMenu) {

		for (final PatternToSheetMapping map : getPatternMaps()) {
			final Menu sheetMenu = new Menu(map.getSheet().getName());
			popupMenu.add(sheetMenu);

			MenuItem item = new MenuItem("Simulate Pen for this Sheet");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SheetFrame sheetFrame = new SheetFrame(map.getSheet(), 800, 600);
					sheetFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				}
			});
			sheetMenu.add(item);

			final MenuItem loadMappingItem = new MenuItem("Load Mappings");
			loadMappingItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent nullActionEvent) {
					map.loadConfigurationFromXML(getPatternInfoFile(map));
				}
			});
			sheetMenu.add(loadMappingItem);

			Map<Region, PatternCoordinateConverter> regionToPatternMapping = map.getRegionToPatternMapping();

			for (final Region r : regionToPatternMapping.keySet()) {
				// the menu item for invoking the runtime binding
				// We need to update the text later...
				final MenuItem bindPatternToRegionItem = new MenuItem("Add Binding For [" + r.getName() + "]");

				bindPatternToRegionItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addEventHandlerForUnmappedEvents(map, r, bindPatternToRegionItem,
								getPatternInfoFile(map));
					}
				});
				sheetMenu.add(bindPatternToRegionItem);
			}
		}
	}

	/**
	 * @param map
	 * @param region
	 * @param bindPatternToRegionItem
	 * @param destFile
	 */
	private void addEventHandlerForUnmappedEvents(final PatternToSheetMapping map, final Region region,
			final MenuItem bindPatternToRegionItem, final File destFile) {

		DebugUtils.println("Draw a box (with a single stroke) to bind region: [" + region
				+ "] to an area on patterned paper.");

		// Runtime Pattern to Region Binding adds a listener for unmapped events in the Event Dispatcher
		host.getEventDispatcher().addEventHandlerForUnmappedEvents(new StrokeHandler() {
			public void strokeArrived(PenEvent lastEvent, InkStroke stroke) {
				Rectangle2D bounds = getStroke().getBounds();
				// determine the bounds of the region in
				// pattern space. this information was provided by the user
				final double tlX = bounds.getX();
				final double tlY = bounds.getY();
				final double width = bounds.getWidth();
				final double height = bounds.getHeight();

				// tie the pattern bounds to this region object
				map.setPatternInformationOfRegion(region, //
						new PatternDots(tlX), new PatternDots(tlY), // 
						new PatternDots(width), new PatternDots(height));

				// unregister myself after one stroke
				host.getEventDispatcher().removeEventHandlerForUnmappedEvents(this);

				// DebugUtils.println("Bound the region [" + r.getName() + "] to Pattern " + bounds);
				bindPatternToRegionItem.setLabel("Change Binding for " + region.getName()
						+ ". Currently set to " + bounds);

				// additionally... write this out to a file in the mappings directory
				map.saveConfigurationToXML(destFile);
			}
		});
	}

	/**
	 * This is an extension point. If you want to customize the tray menu, you can subclass this.
	 * 
	 * @param popupMenu
	 */
	public void populateTrayMenuExtensions(Menu popupMenu) {
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
	 * Renders a PDF of the Application's sheets.
	 * 
	 * @param selectedApp
	 */
	private void renderPDFToSpecificFolder() {
		new Thread(new Runnable() {
			private EndlessProgressDialog progress;

			public void run() {
				final File folderToSavePDFs = FileUtils.showDirectoryChooser(null,
						"Choose a Directory for your PDFs");
				if (folderToSavePDFs != null) { // user approved
					// an endless progress bar
					progress = new EndlessProgressDialog(null, "Creating the PDF",
							"Please wait while your PDF is generated.");
					// start rendering
					renderToPDF(folderToSavePDFs, getName());
					try {
						// open the folder in explorer! =)
						Desktop.getDesktop().open(folderToSavePDFs);
					} catch (IOException e) {
						e.printStackTrace();
					}
					progress.setVisible(false);
					progress = null;
				}
			}
		}).start();
	}

	/**
	 * Feel free to OVERRIDE this.
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
	 * Convenience Method.
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
	}

	/**
	 * Used internally by setHostToolkit. When the toolkit is set, the application is running, by implication.
	 * If the host toolkit is null, then the application has stop running.
	 * 
	 * @param flag
	 */
	public void setRunning(boolean flag) {
		isRunning = flag;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + " Application";
	}

	/**
	 * 
	 */
	public void loadMostRecentPatternMappings() {
		List<String> namesOfSheets = new ArrayList<String>();
		for (final PatternToSheetMapping map : getPatternMaps()) {
			boolean successful = map.loadConfigurationFromXML(getPatternInfoFile(map));
			if (successful) {
				namesOfSheets.add(map.getSheet().getName());
			}
		}
		if (namesOfSheets.size() > 0) {
			DebugUtils.println("Loaded the most recent Pattern Mappings for: " + namesOfSheets);
		}
	}
}
