package papertoolkit;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import papertoolkit.actions.remote.ActionReceiverTrayApp;
import papertoolkit.application.Application;
import papertoolkit.application.ApplicationManager;
import papertoolkit.config.Configuration;
import papertoolkit.events.EventDispatcher;
import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.StrokeHandler;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pattern.coordinates.RegionID;
import papertoolkit.pattern.coordinates.conversion.PatternCoordinateConverter;
import papertoolkit.pattern.coordinates.conversion.TiledPatternCoordinateConverter;
import papertoolkit.pen.Pen;
import papertoolkit.pen.PenInput;
import papertoolkit.pen.handwriting.HandwritingRecognitionService;
import papertoolkit.pen.streaming.PenServerTrayApp;
import papertoolkit.pen.synch.BatchedDataDispatcher;
import papertoolkit.tools.ToolExplorer;
import papertoolkit.tools.debug.DebuggingEnvironment;
import papertoolkit.tools.design.acrobat.AcrobatDesignerLauncher;
import papertoolkit.tools.design.acrobat.RegionConfiguration;
import papertoolkit.tools.design.swing.SheetFrame;
import papertoolkit.units.Centimeters;
import papertoolkit.units.Inches;
import papertoolkit.units.PatternDots;
import papertoolkit.units.Pixels;
import papertoolkit.units.Points;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.StringUtils;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.graphics.ImageCache;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.DarkStar;
import com.thoughtworks.xstream.XStream;

/**
 * <p>
 * Every PaperToolit has one EventEngine that handles input from users, and schedules output for the system. A
 * PaperToolkit can run one or more Applications at the same time. You can also deactivate applications (to
 * pause them). Or, you can remove them altogether. (These features are not yet fully implemented.)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * <p>
 * TODOS:
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PaperToolkit {

	/**
	 * 
	 */
	public static final String CONFIG_FILE_KEY = "papertoolkit.startupinformation";

	/**
	 * 
	 */
	public static final String CONFIG_FILE_VALUE = "data/config/PaperToolkit.xml";

	/**
	 * 
	 */
	public static final String CONFIG_PATTERN_PATH_KEY = "tiledpatterngenerator.patternpath";

	/**
	 * Where our pattern files are located...
	 */
	public static final String CONFIG_PATTERN_PATH_VALUE = "data/pattern/";

	/**
	 * Property Keys.
	 */
	private static final String HW_REC_KEY = "handwritingRecognition";

	/**
	 * Whether we have called initializeLookAndFeel() yet...
	 */
	private static boolean lookAndFeelInitialized = false;

	/**
	 * Where PaperToolkit is installed.
	 */
	private static File paperToolkitRootPath;

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = getPatternPath();

	/**
	 * A key for the configuration file.
	 */
	private static final String REMOTE_PENS_KEY = "remotePens";

	/**
	 * The instance that is created when you use the convenience functions.
	 */
	private static PaperToolkit toolkitInstance;

	/**
	 * The version of PaperToolkit.
	 */
	private static String versionString = "0.6";

	/**
	 * Serializes/Unserializes toolkit objects to/from XML strings.
	 */
	private static XStream xmlEngine;

	/**
	 * Print an Intro Message.
	 */
	static {
		printInitializationMessages();
	}

	/**
	 * @return a new application with a file name that is based on the calling class... That is, if you use
	 *         this convenience function, we will assume that the class you called it from is the main app.
	 */
	public static Application createApplication() {
		return new Application("PaperApp_"
				+ DebugUtils.getClassNameFromStackTraceElement(Thread.currentThread().getStackTrace()[2]));
	}

	/**
	 * Loads an object from an XML File.
	 * 
	 * @param xmlFile
	 * @return
	 */
	public static Object fromXML(File xmlFile) {
		Object o = null;
		try {
			o = getXMLEngine().fromXML(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * @return the location of pattern data, from the configuration files.
	 */
	public static File getPatternPath() {
		return Configuration.getConfigFile(CONFIG_PATTERN_PATH_KEY);
	}

	/**
	 * @return the place where new XML files show up, when we synch our pen.
	 */
	public static File getPenSynchDataPath() {
		return new File(getToolkitRootPath(), "penSynch/data/XML/");
	}

	/**
	 * Can only point to files...
	 * 
	 * @param resourcePath
	 * @return
	 */
	public static File getResourceFile(String resourcePath) {
		try {
			File f = new File(PaperToolkit.class.getResource(resourcePath).toURI());
			return f;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File getToolkitDataFile(String relativePath) {
		return new File(new File(getToolkitRootPath(), "/data/"), relativePath);
	}

	/**
	 * There are no plans for PaperToolkit to be deployed as a single JAR file. Thus, this assumes you have it
	 * installed on the file system.
	 * 
	 * @return the root path to the toolkit (e.g., C:\Documents and Settings\User Name\Projects\PaperToolkit\)
	 */
	public static File getToolkitRootPath() {
		if (paperToolkitRootPath == null) {
			// get the runtime directory for the papertoolkit package (e.g.,
			// PaperToolkit/bin/papertoolkit) the parent will be the root directory!
			URL resource = PaperToolkit.class.getResource("/papertoolkitroot");
			try {
				paperToolkitRootPath = new File(resource.toURI()).getParentFile().getParentFile();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return paperToolkitRootPath;
	}

	/**
	 * @return the XStream processor that parses and creates XML.
	 */
	private static synchronized XStream getXMLEngine() {
		if (xmlEngine == null) {
			xmlEngine = new XStream();

			// Add Aliases Here (for more concise XML)
			xmlEngine.alias("Sheet", Sheet.class);
			xmlEngine.alias("Inches", Inches.class);
			xmlEngine.alias("Centimeters", Centimeters.class);
			xmlEngine.alias("Pixels", Pixels.class);
			xmlEngine.alias("Points", Points.class);
			xmlEngine.alias("RegionConfiguration", RegionConfiguration.class);
			xmlEngine.alias("Region", Region.class);
			xmlEngine.alias("Rectangle2DDouble", Rectangle2D.Double.class);
			xmlEngine.alias("TiledPatternCoordinateConverter", TiledPatternCoordinateConverter.class);
			xmlEngine.alias("RegionID", RegionID.class);
			xmlEngine.alias("PenEvent", PenEvent.class);

		}
		return xmlEngine;
	}

	/**
	 * Sets up parameters for any Java Swing UI we need. Feel free to call this from an external class. If you
	 * use the default PaperToolkit() constructor, it will also use the custom look and feel. All PaperToolkit
	 * utility classes will also use this look and feel.
	 */
	public static void initializeLookAndFeel() {
		if (!lookAndFeelInitialized) {
			// JGoodies Look and Feel
			try {
				final DarkStar theme = new DarkStar();
				PlasticLookAndFeel.setPlasticTheme(theme);
				UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
			} catch (Exception e) {
			}
			lookAndFeelInitialized = true;
		}
	}

	/**
	 * 
	 */
	public static void initializeNativeLookAndFeel() {
		if (!lookAndFeelInitialized) {
			WindowUtils.setNativeLookAndFeel();
		}
		lookAndFeelInitialized = true;
	}

	/**
	 * Alternatively, try using the *.bat files instead.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			// the 0 args branch will run the Paper Toolkit GUI, which helps designers learn what you can do
			// with this toolkit. It integrates with the documentation and stuff too!
			printUsage();
			// new PaperToolkit().startToolExplorer();
		} else if (args[0].startsWith("-actions")) {
			ActionReceiverTrayApp.main(new String[] {});
		} else if (args[0].startsWith("-pen")) {
			PenServerTrayApp.main(new String[] {});
		}
	}

	/**
	 * A Welcome message.
	 */
	private static void printInitializationMessages() {
		final String dashes = StringUtils.repeat("-", versionString.length());
		System.out.println("-------------------------------------" + dashes);
		System.out.println(" PaperToolkit version " + versionString);
		System.out.println(" Copyright (c) 2007 Stanford University ");
		System.out.println(" Ron B. Yeh [ronyeh@cs.stanford.edu] ");
		System.out.println("-------------------------------------" + dashes);
	}

	/**
	 * 
	 */
	private static void printUsage() {
		System.out
				.println("Without any arguments, we run the PaperToolkit Explorer. You can also run Papertoolkit with one argument: ");
		System.out.println("	-actions	// runs the action receiver");
		System.out.println("	-pen		// runs the pen server");
		System.out
				.println("Thank you for using PaperToolkit! Feel free to send feedback (good & bad) to ronyeh@cs.stanford.edu.");
	}

	/**
	 * Convenience function that uses an internal PaperToolkit object.
	 * 
	 * @param paperApp
	 */
	public static synchronized void runApplication(Application paperApp) {
		if (toolkitInstance == null) {
			toolkitInstance = new PaperToolkit();
		}
		toolkitInstance.startApplication(paperApp);
	}

	/**
	 * 
	 */
	public static void startAcrobatDesigner() {
		AcrobatDesignerLauncher.start();
	}

	/**
	 * @param obj
	 * @return a string representing the object translated into XML
	 */
	public static String toXML(Object obj) {
		return getXMLEngine().toXML(obj);
	}

	/**
	 * @param object
	 * @param outputFile
	 */
	public static void toXML(Object object, File outputFile) {
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			toXML(object, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param object
	 * @param stream
	 *            write the xml to disk or another output stream.
	 */
	public static void toXML(Object object, OutputStream stream) {
		getXMLEngine().toXML(object, stream);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param o
	 * @return an XML string representation of the object, without line breaks.
	 */
	public static String toXMLNoLineBreaks(Object o) {
		return toXML(o).replace("\n", "");
	}

	/**
	 * Allows us to manage multiple running applications. Showing this GUI is optional. It is useful during
	 * the design/debugging stages.
	 */
	private ApplicationManager appManager;

	/**
	 * Processes batched ink.
	 */
	private BatchedDataDispatcher batchedDataDispatcher;

	/**
	 * The engine that processes all pen events, producing the correct outputs and calling the right event
	 * handlers.
	 */
	private EventDispatcher eventDispatcher;

	/**
	 * Store frequently used pens here... so that you can ask the toolkit for them instead of creating them
	 * from scratch. This list is loaded up from the PaperToolkit.xml config file.
	 */
	private List<Pen> frequentlyUsedPens = new ArrayList<Pen>();

	/**
	 * 
	 */
	private List<ActionListener> listenersToLoadRecentPatternMappings = new ArrayList<ActionListener>();

	/**
	 * A list of all applications loaded (but not necessarily running) in this system.
	 */
	private List<Application> loadedApplications = new ArrayList<Application>();

	/**
	 * Feel free to edit the PaperToolkit.xml in your local directory, to add configuration properties for
	 * your own program. Then, you can get the local properties from this properties object.
	 */
	private final Properties localProperties = new Properties();

	private PopupMenu popupMenu;

	/**
	 * The list of running applications.
	 */
	private List<Application> runningApplications = new ArrayList<Application>();

	private TrayIcon trayIcon;

	/**
	 * Whether to show the application manager whenever an app is loaded/started. Defaults to false. True is
	 * useful for debugging and stopping apps that don't have a GUI.
	 */
	private boolean useAppManager = false;

	/**
	 * Whether or not to use handwriting recognition. It will start the HWRec Server...
	 */
	private boolean useHandwriting;

	/**
	 * Start up a paper toolkit. A toolkit can load multiple applications, and dispatch events accordingly
	 * (and between applications, ideally). There will be one event engine in the paper toolkit, and all
	 * events that applications generate will be fed through this single event engine.
	 */
	public PaperToolkit() {
		this(false);
	}

	/**
	 * @param useAppManager
	 */
	public PaperToolkit(boolean useAppManager) {
		this(true, useAppManager, false /* no handwriting */);
	}

	/**
	 * TODO: Make the look and feel default to OFF, or somehow research a mixed look and feel solution...
	 * 
	 * @param useAppManager
	 */
	public PaperToolkit(boolean useLookAndFeel, boolean useAppManager, boolean useHandwritingRecognitionServer) {
		loadStartupConfiguration();

		if (useLookAndFeel) {
			initializeLookAndFeel();
		}

		eventDispatcher = new EventDispatcher();
		batchedDataDispatcher = new BatchedDataDispatcher(eventDispatcher);

		// Start the local server up whenever the paper toolkit is initialized.
		// the either flag can override the other. They will both need to be
		// TRUE to actually load
		// it.
		if (useHandwriting && useHandwritingRecognitionServer) {
			HandwritingRecognitionService.getInstance();
		}

		// whether or not to show the app manager GUI when an application is
		// loaded
		// the idea is that one can load multiple applications (TODO)!
		useApplicationManager(useAppManager);
	}

	/**
	 * Check for uninitialized regions, and then populate the menu with options to bind these regions to
	 * pattern at runtime!
	 * 
	 * @param mappings
	 */
	private void checkPatternMapsForUninitializedRegions(Collection<PatternToSheetMapping> mappings) {

		if (trayIcon == null) {
			DebugUtils
					.println("No need to check for uninitialized pattern maps, as we're not using the system tray.");
			return;
		}

		for (final PatternToSheetMapping map : mappings) {

			final MenuItem loadMappingItem = new MenuItem("Load most recent Pattern Mappings");
			loadMappingItem.addActionListener(getLoadRecentPatternMappingsActionListener(map));
			getTrayPopupMenu().add(loadMappingItem);

			Map<Region, PatternCoordinateConverter> regionToPatternMapping = map.getRegionToPatternMapping();

			for (final Region r : regionToPatternMapping.keySet()) {
				PatternCoordinateConverter patternCoordinateConverter = regionToPatternMapping.get(r);
				double area = patternCoordinateConverter.getArea();
				// DebugUtils.println("Area: " + area);
				if (area > 0) {
					// this region has a real mapping! NEXT!
					continue;
				}

				// the menu item for invoking the runtime binding
				// We need to update the text later...
				final MenuItem bindPatternToRegionItem = new MenuItem("Add Pattern Binding For ["
						+ r.getName() + "]");

				bindPatternToRegionItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// DebugUtils.println("Binding " + r);

						// Runtime Pattern to Region Binding
						// adds a listener for trashed events in the Event Dispatcher
						eventDispatcher.addEventHandlerForUnmappedEvents(new StrokeHandler() {
							public void strokeArrived(PenEvent e) {
								Rectangle2D bounds = getStroke().getBounds();
								// determine the bounds of the region in
								// pattern space
								// this information was provided by the
								// user
								final double tlX = bounds.getX();
								final double tlY = bounds.getY();
								final double width = bounds.getWidth();
								final double height = bounds.getHeight();

								// tie the pattern bounds to this region
								// object
								map.setPatternInformationOfRegion(r, //
										new PatternDots(tlX), new PatternDots(tlY), // 
										new PatternDots(width), new PatternDots(height));

								// unregister myself...
								eventDispatcher.removeEventHandlerForUnmappedEvents(this);

								// DebugUtils.println("Bound the region [" + r.getName() + "] to Pattern "
								// + bounds);
								bindPatternToRegionItem.setLabel("Change Binding for " + r.getName()
										+ ". Currently set to " + bounds);

								// additionally... write this out to a
								// file on the desktop
								File destFile = getLastRunPatternInfoFile();
								map.saveConfigurationToXML(destFile);
							}

						});
					}
				});
				getTrayPopupMenu().add(bindPatternToRegionItem);
			}
		}
	}

	/**
	 * Attaches to the popup menu. Allows us to drop into the Debug mode of a paper application, with event
	 * visualizations and stuff. =)
	 * 
	 * @param app
	 * @return
	 */
	private ActionListener getDebugListener(final Application app) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DebuggingEnvironment debuggingEnvironment = app.getDebuggingEnvironment();
				if (debuggingEnvironment == null) {
					debuggingEnvironment = new DebuggingEnvironment();
				}
				debuggingEnvironment.setApp(app);
				debuggingEnvironment.showFlashView();
			}
		};
	}

	/**
	 * EXPERTS ONLY: Interact with the EventEngine at runtime!
	 * 
	 * @return
	 */
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	/**
	 * Remove any tray icons, and exit!
	 * 
	 * @return
	 */
	private ActionListener getExitListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Exiting the Paper Toolkit...");
				if (trayIcon != null) {
					TrayIcon iconToRemove = trayIcon;
					trayIcon = null;
					SystemTray.getSystemTray().remove(iconToRemove);
				}
				System.exit(0);
			}
		};
	}

	/**
	 * @return
	 */
	public List<Pen> getFrequentlyUsedPens() {
		return frequentlyUsedPens;
	}

	/**
	 * @return
	 */
	private File getLastRunPatternInfoFile() {
		File homeDir = FileSystemView.getFileSystemView().getHomeDirectory();
		File destFile = new File(homeDir, "PaperToolkitLastRun.patternInfo.xml");
		return destFile;
	}

	/**
	 * @return
	 */
	public List<Application> getLoadedApplications() {
		return loadedApplications;
	}

	/**
	 * @param map
	 * @return
	 */
	private ActionListener getLoadRecentPatternMappingsActionListener(final PatternToSheetMapping map) {
		// add it to the list, so we can invoke them later!
		final ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent nullActionEvent) {
				map.loadConfigurationFromXML(getLastRunPatternInfoFile());
			}
		};
		listenersToLoadRecentPatternMappings.add(actionListener);

		return actionListener;
	}

	public String getProperty(String propertyKey) {
		return localProperties.getProperty(propertyKey);
	}

	/**
	 * @param app
	 * @return
	 */
	private ActionListener getRenderListener(final Application app) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.renderToPDF();
			}
		};
	}

	/**
	 * @return
	 */
	public List<Application> getRunningApplications() {
		return runningApplications;
	}

	/**
	 * For debugging running applications. =)
	 */
	private void getSystemTrayIcon() {
		if (trayIcon == null) {
			// this is the icon that sits in our tray...
			trayIcon = new TrayIcon(ImageCache.loadBufferedImage(PaperToolkit
					.getToolkitDataFile("/icons/paper.png")), "Paper Toolkit", getTrayPopupMenu());
			trayIcon.setImageAutoSize(true);
			try {
				if (SystemTray.isSupported()) {
					SystemTray.getSystemTray().add(trayIcon);

					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
						public void run() {
							// DebugUtils.println("Running Shutdown Services...");
							// Buggy for some reason... Can stop the shutdown =\
							// if (trayIcon != null) {
							// SystemTray.getSystemTray().remove(trayIcon);
							// }
							// DebugUtils.println("Done with Shutdown!");
						}
					}));
				}
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return a menu for the System Tray Icon.
	 */
	private PopupMenu getTrayPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new PopupMenu("Paper Toolkit Options");

			// exit the application
			final MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(getExitListener());

			popupMenu.add(exitItem);
		}

		return popupMenu;
	}

	/**
	 * Adds an application to the loaded list, and displays the application manager if the useAppManager flag
	 * is set to true (default == false).
	 * 
	 * If you would like to use the GUI launcher/App Manager then call PaperToolkit.useAppManager(true);
	 * 
	 * @param app
	 */
	public void loadApplication(Application app) {
		// DebugUtils.println("Loading " + app.getName());
		loadedApplications.add(app);

		// show the app manager if the developer wants to see it.
		if (useAppManager) {
			// DebugUtils.println("Loading/Updating the Application Manager.");
			appManager = new ApplicationManager(this);
			appManager.updateListOfApps();
		}

		// load the system tray icon...
		getSystemTrayIcon();

		popupMenu.add(new MenuItem("-")); // separator
		final MenuItem debugItem = new MenuItem("Debug [" + app.getName() + "]");
		debugItem.addActionListener(getDebugListener(app));

		final MenuItem renderItem = new MenuItem("Render Sheets for [" + app.getName() + "]");
		renderItem.addActionListener(getRenderListener(app));

		getTrayPopupMenu().add(debugItem);
		getTrayPopupMenu().add(renderItem);

		for (final Sheet s : app.getSheets()) {
			MenuItem item = new MenuItem("Open JFrame for sheet [" + s.getName() + "]");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SheetFrame sf = new SheetFrame(s, 640, 480);
					sf.setVisible(true);
				}
			});
			getTrayPopupMenu().add(item);
		}
	}

	/**
	 * 
	 */
	public void loadMostRecentPatternMappings() {
		DebugUtils.println("Loading most recent Pattern Mappings...");
		for (ActionListener l : listenersToLoadRecentPatternMappings) {
			l.actionPerformed(null);
		}
	}

	/**
	 * Load the configuration information on startup...
	 */
	private void loadStartupConfiguration() {
		final Properties props = Configuration.getPropertiesFromConfigFile(CONFIG_FILE_KEY);
		setStartupProperties(props);

		// also check for a custom PaperToolkit.xml in the run directory of the
		// application
		// properties in that file will override the ones we just loaded from
		// the default location
		// alternatively, you can just edit the default PaperToolkit.xml,
		// located in
		// data/config/PaperToolkit.xml
		File localPropsFile = new File("PaperToolkit.xml");
		if (localPropsFile.exists()) {
			DebugUtils.println("Local Properties File Exists. Overriding Properties: ");
			try {
				localProperties.loadFromXML(new FileInputStream(localPropsFile));
				setStartupProperties(localProperties);
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			DebugUtils.println("Local Properties File Does Not Exist");
		}
	}

	/**
	 * TODO: Figure out the easiest way to send a PDF (with or without regions) to the default printer.
	 * 
	 * @param sheet
	 */
	public void print(Sheet sheet) {
		// Implement this...
		DebugUtils.println("Unimplemented Method");
	}

	/**
	 * @param props
	 */
	private void setStartupProperties(Properties props) {
		if (props.containsKey(HW_REC_KEY)) {
			String theProp = props.getProperty(HW_REC_KEY);
			// DebugUtils.println(HW_REC_KEY + " was: [" + useHandwriting + "] and is now [" + theProp + "]");
			useHandwriting = Boolean.parseBoolean(theProp);
		}

		// add the Pens that we use most frequently...
		frequentlyUsedPens.add(new Pen("Local Pen"));
		if (props.containsKey(REMOTE_PENS_KEY)) {
			String theProp = props.getProperty(REMOTE_PENS_KEY);
			// DebugUtils.println("Loading Frequently Used Remote Pens from PaperToolkit.xml: " + theProp);
			String[] pens = theProp.split(","); // comma delimited
			for (String pen : pens) {
				int colonIndex = pen.indexOf(":");
				String penServerName = pen.substring(0, colonIndex);
				int penServerPort = Integer.parseInt(pen.substring(colonIndex + 1, pen.length()));
				// DebugUtils.println("Adding Pen " + penServerName + " : " + penServerPort);
				String shortName = "";
				if (penServerName.contains(".")) {
					shortName = penServerName.substring(0, penServerName.indexOf("."));
				} else {
					shortName = penServerName;
				}
				frequentlyUsedPens.add(new Pen(shortName, penServerName, penServerPort));
			}
		}

	}

	/**
	 * Start this application and register all live pens with the event engine. The event engine will then
	 * start dispatching events for this application until the application is stopped.
	 * 
	 * @param paperApp
	 */
	public void startApplication(Application paperApp) {
		if (!loadedApplications.contains(paperApp)) {
			loadApplication(paperApp);
		}

		// run any initializers that need to happen before we begin
		paperApp.initializeBeforeStarting();

		// get all the pens and start them in live mode...
		// we assume we have decided where each pen server will run
		// start live mode will connect to that pen server.
		if (paperApp.getPenInputDevices().size() == 0) {
			// DebugUtils.println(paperApp.getName()
			// + " does not have any pens! We will add a single streaming pen for you.");
			final Pen aPen = new Pen();
			paperApp.addPenInput(aPen);
		}

		final List<PenInput> pens = paperApp.getPenInputDevices();
		// add all the live pens to the eventEngine
		for (PenInput pen : pens) {
			pen.startLiveMode(); // starts live mode at the pen's default
			// place
			if (pen.isLive()) {
				eventDispatcher.register(pen);
			}
		}

		// keep track of the pattern assigned to different sheets and regions
		final Collection<PatternToSheetMapping> patternMappings = paperApp.getPatternMaps();
		eventDispatcher.registerPatternMapsForEventHandling(patternMappings);

		// will populate the system tray with a feature for runtime binding of
		// regions... =)
		checkPatternMapsForUninitializedRegions(patternMappings);

		// XXX
		// Here, we should pass the event engine over...
		// When the Batch Server gets in the data, it will translate it to
		// streaming event
		// coordinates
		// And then pass it to the Event Engine
		// It will essentially "replay" the events as if it were through event
		// save/replay

		// DebugUtils.println("Starting Application: " + paperApp.getName());
		runningApplications.add(paperApp);
		if (useAppManager) {
			appManager.repaintListOfApps();
		}

		// provides access back to the toolkit object
		paperApp.setHostToolkit(this);
	}

	/**
	 * Helps guide developers through the Paper Toolkit.
	 */
	private void startToolExplorer() {
		new ToolExplorer(this);
	}

	/**
	 * Remove the application and stop receiving events from its pens....
	 * 
	 * @param paperApp
	 */
	public void stopApplication(Application paperApp) {
		final List<PenInput> pens = paperApp.getPenInputDevices();
		for (PenInput pen : pens) {
			if (pen.isLive()) {
				eventDispatcher.unregisterPen(pen);
				// stop the pen from listening!
				pen.stopLiveMode();
			}
		}

		eventDispatcher.unregisterPatternMapsForEventHandling(paperApp.getPatternMaps());

		// DebugUtils.println("Stopping Application: " + paperApp.getName());
		runningApplications.remove(paperApp);

		if (useAppManager) {
			appManager.repaintListOfApps();
		}

		paperApp.setHostToolkit(null);
	}

	/**
	 * @param app
	 */
	public void unloadApplication(Application app) {
		loadedApplications.remove(app);
	}

	/**
	 * @param flag
	 *            whether or not to load the app manager when you load an application.
	 */
	public void useApplicationManager(boolean flag) {
		useAppManager = flag;
	}
}
