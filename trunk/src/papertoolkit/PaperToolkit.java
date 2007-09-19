package papertoolkit;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import papertoolkit.actions.remote.ActionReceiverTrayApp;
import papertoolkit.application.Application;
import papertoolkit.application.config.Configuration;
import papertoolkit.application.config.StartupOptions;
import papertoolkit.application.config.Constants.Ports;
import papertoolkit.events.EventDispatcher;
import papertoolkit.events.PenEvent;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternToSheetMapping;
import papertoolkit.pattern.coordinates.RegionID;
import papertoolkit.pattern.coordinates.conversion.TiledPatternCoordinateConverter;
import papertoolkit.pen.InputDevice;
import papertoolkit.pen.Pen;
import papertoolkit.pen.handwriting.HandwritingRecognitionService;
import papertoolkit.pen.replay.SaveAndReplay;
import papertoolkit.pen.streaming.PenServerTrayApp;
import papertoolkit.pen.synch.BatchedDataDispatcher;
import papertoolkit.tools.ToolExplorer;
import papertoolkit.tools.design.acrobat.PaperUIDesigner;
import papertoolkit.tools.design.acrobat.RegionConfiguration;
import papertoolkit.tools.services.ToolkitMonitoringService;
import papertoolkit.units.Centimeters;
import papertoolkit.units.Inches;
import papertoolkit.units.Pixels;
import papertoolkit.units.Points;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.graphics.ImageCache;
import papertoolkit.util.graphics.ImageUtils;

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
	 * Ensure the toolkit initialization happens once and only once.
	 */
	private static boolean alreadyInitialized = false;

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
	 * We should move this out to a config file, I know... :(
	 */
	private static final String DEFAULT_TOOLKIT_RESOURCE_DIR = "C:/Documents and Settings/Ron Yeh/My Documents/Projects/PaperToolkit/bin";

	/**
	 * Property Keys.
	 */
	private static final String HW_REC_KEY = "handwritingRecognition";

	/**
	 * 
	 */
	private static boolean isfirstAppPopulatingSystemTray = true;

	/**
	 * Whether we have called initializeLookAndFeel() yet...
	 */
	private static boolean lookAndFeelInitialized = false;

	/**
	 * Where PaperToolkit is installed.
	 */
	private static File toolkitRootPath;

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = getPatternPath();

	/**
	 * A key for the configuration file.
	 */
	private static final String REMOTE_PENS_KEY = "remotePens";

	/**
	 * The instance that is created when you use the convenience function.
	 */
	private static PaperToolkit toolkitInstance;

	/**
	 * In the Windows System Tray...
	 */
	private static TrayIcon trayIcon;

	/**
	 * The System Tray right click menu.
	 */
	private static PopupMenu trayMenu;

	/**
	 * The version of PaperToolkit.
	 * 
	 * 0.7 added gesture recognition<br>
	 * 0.8 should add SideCar testing tools<br>
	 * 0.9 should do major cleaning and bug fixing....<br>
	 * 1.0 should re-include the printing API.<br>
	 */
	private static String versionString = "0.7";

	/**
	 * Serializes/Unserializes toolkit objects to/from XML strings.
	 */
	private static XStream xmlEngine;

	/**
	 * Print an Intro Message.
	 */
	static {
		init();
	}

	/**
	 * @return a new application with a file name that is based on the calling class... That is, if you use
	 *         this convenience function, we will assume that the class you called it from is the main app.
	 */
	public static Application createApplication() {
		return new Application(DebugUtils.getClassNameFromStackTraceElement(Thread.currentThread()
				.getStackTrace()[2]));
	}

	/**
	 * For debugging running applications. =)
	 */
	private static void createSystemTrayIcon() {
		if (!SystemTray.isSupported()) {
			return;
		}
		if (trayIcon == null) {
			// this is the icon that sits in our tray...
			trayIcon = new TrayIcon(ImageCache
					.loadBufferedImage(PaperToolkit.getDataFile("/icons/paper.png")), "Paper Toolkit",
					getTrayMenu());
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (!SwingUtilities.isRightMouseButton(e)) {
						exitPaperToolkit();
					}
				}
			});
			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
			// Don't add a Shutdown Hook, as that is buggy, and can stop shutdown
		}
	}

	/**
	 * Remove any tray icons, and exit!
	 * 
	 * @return
	 */
	private static void exitPaperToolkit() {
		System.out.println("Exiting PaperToolkit...");
		if (trayIcon != null) {
			TrayIcon iconToRemove = trayIcon;
			trayIcon = null;
			SystemTray.getSystemTray().remove(iconToRemove);
		}
		System.exit(0);
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
	 * @param relativePath
	 * @return a file under the /data/ directory...
	 */
	public static File getDataFile(String relativePath) {
		return new File(new File(getToolkitRootPath(), "/data/"), relativePath);
	}

	/**
	 * @return
	 */
	public static synchronized PaperToolkit getInstance() {
		if (toolkitInstance == null) {
			toolkitInstance = new PaperToolkit();
		}
		return toolkitInstance;
	}

	/**
	 * @return
	 */
	public static Image getPaperToolkitIcon() {
		return ImageUtils.readImage(getDataFile("icons/paper.png"));
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
	 * Can only point to files that are in the "classpath"...
	 * 
	 * @param resourcePath
	 * @return
	 */
	public static File getResourceFile(String resourcePath) {
		try {
			// we need a way to anchor the PaperToolkit Path...
			// maybe through a config file? but where might we access this config file...
			// in the user's home directory?
			// instead, if it's a resource we can't understand, we just default to the toolkitRootPath field
			URI resourceURI = PaperToolkit.class.getResource(resourcePath).toURI();
			if (resourceURI.getScheme().equals("bundleresource")) {
				return new File(DEFAULT_TOOLKIT_RESOURCE_DIR, resourcePath);
			} else {
				return new File(resourceURI);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param relativePath
	 * @return a file or directory relative to PaperToolkit/
	 */
	public static File getToolkitFile(String relativePath) {
		return new File(getToolkitRootPath(), relativePath);
	}

	/**
	 * There are no plans for PaperToolkit to be deployed as a single JAR file. Thus, this assumes you have it
	 * installed on the file system.
	 * 
	 * @return the root path to the toolkit (e.g., C:\Documents and Settings\User Name\Projects\PaperToolkit\)
	 */
	public static File getToolkitRootPath() {

		if (toolkitRootPath == null) {
			// get the runtime directory for the papertoolkit package (e.g.,
			// PaperToolkit/bin/papertoolkit) the parent will be the root
			// directory!
			// 
			// darn... this doesn't work if it is accessed by eclipse
			File resourceFile = getResourceFile("/papertoolkitroot");
			toolkitRootPath = resourceFile.getParentFile().getParentFile();
		}
		return toolkitRootPath;
	}

	/**
	 * @return a menu for the System Tray Icon.
	 */
	private static PopupMenu getTrayMenu() {
		if (trayMenu == null) {
			trayMenu = new PopupMenu("Paper Toolkit Options");

			// for exiting the application
			final MenuItem exitItem = new MenuItem("Exit PaperToolkit");
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					exitPaperToolkit();
				}
			});
			trayMenu.add(exitItem);

			// for event save and replay
			final Menu replayItem = new Menu("Event Replay");
			final MenuItem replayNow = new MenuItem("Play");
			replayNow.addActionListener(SaveAndReplay.getInstance().getActionListenerForReplay());
			final MenuItem latestSession = new MenuItem("Load Most Recent Session");
			latestSession.addActionListener(SaveAndReplay.getInstance().getActionListenerForLoadLatest());
			final MenuItem chooseSession = new MenuItem("Load a Different Session...");
			chooseSession.addActionListener(SaveAndReplay.getInstance().getActionListenerForChooseSession());
			final Menu playBookmarked = new Menu("Bookmarked Sessions");
			SaveAndReplay.getInstance().populateBookmarks(playBookmarked);
			replayItem.add(chooseSession);
			replayItem.add(latestSession);
			replayItem.add(playBookmarked);
			replayItem.add(replayNow);
			trayMenu.add(replayItem);
		}

		return trayMenu;
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
	 * Called only once, on toolkit startup. (Also is called manually by the Pen object, if you use it without
	 * the toolkit.)
	 */
	public static void init() {
		if (alreadyInitialized) {
			return;
		}
		alreadyInitialized = true;

		printInitializationMessages();
		createSystemTrayIcon(); // load the system tray icon...
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
	 * @return
	 */
	public static SaveAndReplay initializeSaveAndReplay() {
		// the static initializer will have run by this time, so getInstance will be valid!
		return SaveAndReplay.getInstance();
	}

	/**
	 * Alternatively, try using the *.bat files instead.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			// the 0 args branch will run the Paper Toolkit GUI, which helps
			// designers learn what you can do
			// with this toolkit. It integrates with the documentation and stuff
			// too!
			printUsage();
			getInstance().startToolExplorer();
		} else if (args[0].startsWith("-actions")) {
			ActionReceiverTrayApp.main(new String[] {});
		} else if (args[0].startsWith("-pen")) {
			PenServerTrayApp.main(new String[] {});
		}
	}

	/**
	 * @param popupMenu
	 */
	private static void populateTrayMenuForSideCar(Menu popupMenu) {
		final MenuItem openSideCarItem = new MenuItem("Open SideCar Display");
		openSideCarItem.addActionListener(new ActionListener() {
			private PrintWriter sideCarPrintWriter;
			private Socket sideCarSocket;

			public void actionPerformed(ActionEvent arg0) {
				DebugUtils.println("Opening Sidecar Flex GUI...");
				// make a socket connection and ask the (already running) SideCar to start its Flex GUI
				try {
					if (sideCarSocket == null) {
						sideCarSocket = new Socket("localhost", Ports.SIDE_CAR_COMMUNICATIONS);
						OutputStream outputStream = sideCarSocket.getOutputStream();
						sideCarPrintWriter = new PrintWriter(outputStream);
					}
					sideCarPrintWriter.println("StartFlexGUI");
					sideCarPrintWriter.flush();
				} catch (Exception e) {
					DebugUtils.println("Is SideCar Running Yet? If not... start SideCar, and try again!");
					DebugUtils.println("We are expecting SideCar to be listening at Port: "
							+ Ports.SIDE_CAR_COMMUNICATIONS);
				}
			}
		});
		popupMenu.add(openSideCarItem);
	}

	/**
	 * A Welcome message.
	 */
	private static void printInitializationMessages() {
		System.out.println("---------------------------------------------");
		System.out.println(" PaperToolkit version " + versionString);
		System.out.println(" Copyright (c) 2006-2007 Stanford University ");
		System.out.println(" Ron B. Yeh [ronyeh@cs.stanford.edu] ");
		System.out.println("---------------------------------------------");
	}

	/**
	 * 
	 */
	private static void printUsage() {
		System.out.println("Without any arguments, we run the PaperToolkit Explorer. "
				+ "You can also run Papertoolkit with one argument: ");
		System.out.println("	-actions	// runs the action receiver");
		System.out.println("	-pen		// runs the pen server");
		System.out.println("Thank you for using PaperToolkit! Feel free to send feedback (good & bad) to "
				+ "ronyeh@cs.stanford.edu.");
	}

	/**
	 * 
	 */
	public static void startAcrobatDesigner() {
		PaperUIDesigner.start();
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
	 * Processes batched ink.
	 */
	@SuppressWarnings("unused")
	private BatchedDataDispatcher batchedDataDispatcher;

	/**
	 * The engine that processes all pen events, producing the correct outputs and calling the right event
	 * handlers.
	 */
	private EventDispatcher eventDispatcher;

	/**
	 * The list of running or stopped applications.
	 */
	private List<Application> loadedApplications = new ArrayList<Application>();

	/**
	 * Feel free to edit the PaperToolkit.xml in your local directory, to add configuration properties for
	 * your own program. Then, you can get the local properties from this properties object.
	 */
	private final Properties localProperties = new Properties();

	/**
	 * For allowing external apps (e.g., SideCar) to monitor the toolkit's actions...
	 */
	@SuppressWarnings("unused")
	private ToolkitMonitoringService monitoringService;

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
		this(new StartupOptions()); // default options...
	}

	/**
	 * Use a custom parameter block, that lets you customize the look and feel, handwriting recognition,
	 * etc...
	 */
	public PaperToolkit(StartupOptions startupOptions) {
		loadStartupConfiguration();

		if (startupOptions.getParamApplyGUILookAndFeel()) {
			initializeLookAndFeel();
		}

		eventDispatcher = new EventDispatcher();
		batchedDataDispatcher = new BatchedDataDispatcher(eventDispatcher);

		// the handwriting server starts up only if the sheet has a handwriting
		// recognizer... (or something
		// like that)
		// Start the local server up whenever the paper toolkit is initialized.
		// the either flag can override the other. They will both need to be
		// TRUE to actually load it.
		if (useHandwriting && startupOptions.getParamTurnOnHandwritingRecognitionServer()) {
			HandwritingRecognitionService.getInstance();
		}

		// set up the monitoring
		monitoringService = new ToolkitMonitoringService(this);
	}

	/**
	 * EXPERTS ONLY: Interact with the EventEngine at runtime!
	 * 
	 * @return
	 */
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public List<Application> getLoadedApps() {
		return loadedApplications;
	}

	/**
	 * @param propertyKey
	 * @return
	 */
	public String getProperty(String propertyKey) {
		return localProperties.getProperty(propertyKey);
	}

	/**
	 * For every application, ask it to load themost recent mappings...
	 */
	public void loadMostRecentPatternMappings() {
		DebugUtils.println("Loading most recent Pattern Mappings...");
		for (Application a : loadedApplications) {
			a.loadMostRecentPatternMappings();
		}
	}

	/**
	 * Load the configuration information on startup...
	 */
	private void loadStartupConfiguration() {
		final Properties props = Configuration.getPropertiesFromConfigFile(CONFIG_FILE_KEY);
		setStartupProperties(props);

		// also check for a custom PaperToolkit.xml in the run directory of the
		// application properties in that file will override the ones we just
		// loaded from
		// the default location
		// alternatively, you can just edit the default PaperToolkit.xml,
		// located in data/config/PaperToolkit.xml
		File localPropsFile = new File("PaperToolkit.xml");
		if (localPropsFile.exists()) {
			DebugUtils.println("Local Properties File Exists. Loading Properties from " + localPropsFile);
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
	 * TODO: Figure out the easiest way to send a PS/PDF (with or without regions) to the default printer.
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
			// DebugUtils.println(HW_REC_KEY + " was: [" + useHandwriting + "]
			// and is now [" + theProp + "]");
			useHandwriting = Boolean.parseBoolean(theProp);
		}

		// add the Pens that we use most frequently...
		Pen.addToQuickList("localhost");
		if (props.containsKey(REMOTE_PENS_KEY)) {
			String theProp = props.getProperty(REMOTE_PENS_KEY);
			// DebugUtils.println("Loading Frequently Used Remote Pens from
			// PaperToolkit.xml: " + theProp);
			String[] pens = theProp.split(","); // comma delimited
			for (String pen : pens) {
				Pen.addToQuickList(pen);
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
			if (isfirstAppPopulatingSystemTray) {
				populateTrayMenuForSideCar(trayMenu); // add the sidecar menu if there is an application!
				trayMenu.add(new MenuItem("-")); // separator
				isfirstAppPopulatingSystemTray = false;
			}

			paperApp.populateTrayMenu(getTrayMenu());

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

			loadedApplications.add(paperApp);
			// provides access back to the toolkit object
			paperApp.setHostToolkit(this);
		}

		// register all the live pens with the dispatcher
		final List<InputDevice> pens = paperApp.getPenInputDevices();
		for (InputDevice pen : pens) {
			pen.startLiveMode(); // starts live mode at the pen's default place
			if (pen.isLive()) {
				eventDispatcher.register(pen);
			}
		}

		// keep track of the pattern assigned to different sheets and regions
		final Collection<PatternToSheetMapping> patternMappings = paperApp.getPatternMaps();
		eventDispatcher.registerPatternMapsForEventHandling(patternMappings);
		paperApp.setRunning(true);
	}

	/**
	 * Helps guide developers through the Paper Toolkit.
	 */
	private void startToolExplorer() {
		new ToolExplorer(this);
	}

	/**
	 * Stop receiving events from its pens....
	 * 
	 * @param paperApp
	 */
	public void stopApplication(Application paperApp) {
		final List<InputDevice> pens = paperApp.getPenInputDevices();
		for (InputDevice pen : pens) {
			if (pen.isLive()) {
				eventDispatcher.unregisterPen(pen);
				// stop the pen from listening!
				pen.stopLiveMode();
			}
		}
		eventDispatcher.unregisterPatternMapsForEventHandling(paperApp.getPatternMaps());
		paperApp.setRunning(false);
	}
}
