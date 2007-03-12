package edu.stanford.hci.r3;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
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
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.DarkStar;
import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.actions.remote.ActionReceiverTrayApp;
import edu.stanford.hci.r3.config.Configuration;
import edu.stanford.hci.r3.events.EventEngine;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.replay.EventBrowser;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.RegionID;
import edu.stanford.hci.r3.pattern.coordinates.conversion.TiledPatternCoordinateConverter;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.batch.BatchServer;
import edu.stanford.hci.r3.pen.handwriting.HandwritingRecognitionService;
import edu.stanford.hci.r3.pen.streaming.PenServerTrayApp;
import edu.stanford.hci.r3.tools.debug.DebuggingEnvironment;
import edu.stanford.hci.r3.tools.design.acrobat.AcrobatDesignerLauncher;
import edu.stanford.hci.r3.tools.design.acrobat.RegionConfiguration;
import edu.stanford.hci.r3.units.Centimeters;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.StringUtils;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.components.EndlessProgressDialog;
import edu.stanford.hci.r3.util.files.FileUtils;
import edu.stanford.hci.r3.util.graphics.ImageCache;
import edu.stanford.hci.r3.util.layout.StackedLayout;

/**
 * <p>
 * Every PaperToolit has one EventEngine that handles input from users, and schedules output for the
 * system. A PaperToolkit can run one or more Applications at the same time. You can also deactivate
 * applications (to pause them). Or, you can remove them altogether. (These features are not yet
 * fully implemented.)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PaperToolkit {

	/**
	 * Font for the App Manager GUI.
	 */
	private static final Font APP_MANAGER_FONT = new Font("Trebuchet MS", Font.PLAIN, 18);

	public static final String CONFIG_FILE_KEY = "papertoolkit.startupinformation";

	public static final String CONFIG_FILE_VALUE = "/config/PaperToolkit.xml";

	public static final String CONFIG_PATTERN_PATH_KEY = "tiledpatterngenerator.patternpath";

	public static final String CONFIG_PATTERN_PATH_VALUE = "/pattern/";

	/**
	 * Whether we have called initializeLookAndFeel() yet...
	 */
	private static boolean lookAndFeelInitialized = false;

	/**
	 * Where to find the directories that store our pattern definition files.
	 */
	public static final File PATTERN_PATH = getPatternPath();

	private static PaperToolkit toolkitInstance;

	/**
	 * The version of the PaperToolkit.
	 * 
	 * <p>
	 * Version 0.3 should include:
	 * <ul>
	 * <li>Better Batched Event Support</li>
	 * </ul>
	 * Version 0.4 should include:
	 * <ul>
	 * <li>Better Debugging Tools</li>
	 * </ul>
	 * </p>
	 */
	private static String versionString = "0.4";

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

	/**
	 * Before 1.0, we will need to make sure this can work with a JAR-style deployment. Currently,
	 * this does NOT work as a packaged jar.
	 * 
	 * @return the root path to the toolkit (or some other canonical path where we can expect to
	 *         find certain resources, like the HandwritingRecognition Server)
	 */
	public static File getToolkitRootPath() {
		File file = null;
		try {
			URL resource = PaperToolkit.class.getResource("/config");
			file = new File(resource.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (file == null) {
			return null;
		} else {
			return file.getParentFile();
		}
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
			xmlEngine.alias("TiledPatternCoordinateConverter",
					TiledPatternCoordinateConverter.class);
			xmlEngine.alias("RegionID", RegionID.class);
			xmlEngine.alias("PenEvent", PenEvent.class);

		}
		return xmlEngine;
	}

	/**
	 * Sets up parameters for any Java Swing UI we need. Feel free to call this from an external
	 * class. If you use the default PaperToolkit() constructor, it will also use the custom look
	 * and feel. All PaperToolkit utility classes will also use this look and feel.
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
	 * Alternatively, try using the batch files instead.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			return;
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
		System.out.println("-----------------------------------------------------------" + dashes);
		System.out.println("Reduce/Reuse/Recycle: A Paper Applications Toolkit ver. "
				+ versionString);
		System.out.println("-----------------------------------------------------------" + dashes);
	}

	/**
	 * 
	 */
	private static void printUsage() {
		System.out.println("Takes One Argument: ");
		System.out.println("	-actions	// runs the action receiver");
		System.out.println("	-pen		// runs the pen server");
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

	private JTextArea appDetailsPanel;

	private JScrollPane appDetailsScrollPane;

	/**
	 * Stop, run, pause applications.
	 */
	private JFrame appManager;

	private JPanel appsInspectorPanel;

	/**
	 * Processes batched ink.
	 */
	private BatchServer batchServer;

	private JPanel controls;

	private JButton designSheetsButton;

	private EventBrowser eventBrowser;

	private JButton eventBrowserButton;

	/**
	 * The engine that processes all pen events, producing the correct outputs and calling the right
	 * event handlers.
	 */
	private EventEngine eventEngine;

	/**
	 * Exits the app manager.
	 */
	private JButton exitAppManagerButton;

	/**
	 * Visual list of loaded (and possibly running) apps.
	 */
	private JXList listOfApps;

	/**
	 * A list of all applications loaded (but not necessarily running) in this system.
	 */
	private List<Application> loadedApplications = new ArrayList<Application>();

	/**
	 * Description for the app manager.
	 */
	private JLabel mainMessage;

	/**
	 * 
	 */
	private JButton printSheetsButton;

	/**
	 * Progress bar... for when we are rendering, etc.
	 */
	private EndlessProgressDialog progress;

	/**
	 * The list of running applications.
	 */
	private List<Application> runningApplications = new ArrayList<Application>();

	/**
	 * Starts the selected application.
	 */
	private JButton startAppButton;

	/**
	 * Stops the selected application.
	 */
	private JButton stopAppButton;

	private TrayIcon trayIcon;

	/**
	 * Whether to show the application manager whenever an app is loaded/started. Defaults to false.
	 * True is useful for debugging and stopping apps that don't have a GUI.
	 */
	private boolean useAppManager = false;

	/**
	 * 
	 */
	private boolean useHandwriting;

	private PopupMenu popupMenu;

	/**
	 * Start up a paper toolkit. A toolkit can load multiple applications, and dispatch events
	 * accordingly (and between applications, ideally). There will be one event engine in the paper
	 * toolkit, and all events that applications generate will be fed through this single event
	 * engine.
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
	 * @param useAppManager
	 */
	public PaperToolkit(boolean useLookAndFeel, boolean useAppManager,
			boolean useHandwritingRecognitionServer) {
		loadStartupConfiguration();

		if (useLookAndFeel) {
			initializeLookAndFeel();
		}

		eventEngine = new EventEngine();
		batchServer = new BatchServer(eventEngine);

		// Start the local server up whenever the paper toolkit is initialized.
		// the either flag can override the other. They will both need to be TRUE to actually load
		// it.
		if (useHandwriting && useHandwritingRecognitionServer) {
			HandwritingRecognitionService.getInstance();
		}

		// whether or not to show the app manager GUI when an application is loaded
		// the idea is that one can load multiple applications (TODO)!
		useApplicationManager(useAppManager);
	}

	/**
	 * @return the scrollpane that shows the internals of the application.
	 */
	private Component getAppDetailsPane() {
		if (appDetailsScrollPane == null) {
			appDetailsScrollPane = new JScrollPane(getAppDetailsTextArea());
		}
		return appDetailsScrollPane;
	}

	/**
	 * @return
	 */
	private JTextArea getAppDetailsTextArea() {
		if (appDetailsPanel == null) {
			appDetailsPanel = new JTextArea(8, 50 /* cols */);
			appDetailsPanel.setBackground(new Color(240, 240, 240));
			appDetailsPanel.setEditable(false);
		}
		return appDetailsPanel;
	}

	/**
	 * Allows an end user to stop, start, and otherwise manage loaded applications.
	 * 
	 * @return
	 */
	public JFrame getApplicationManager() {
		if (appManager == null) {
			appManager = new JFrame("R3 Applications");

			appManager.setLayout(new BorderLayout());
			appManager.add(getMainMessage(), BorderLayout.NORTH);
			appManager.add(getAppsInspectorPanel(), BorderLayout.CENTER);
			appManager.add(getExitAppManagerButton(), BorderLayout.SOUTH);
			appManager.add(getControls(), BorderLayout.EAST);

			appManager.setSize(640, 480);
			appManager.setLocation(WindowUtils.getWindowOrigin(appManager,
					WindowUtils.DESKTOP_CENTER));
			appManager.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			appManager.setVisible(true);
		}
		return appManager;
	}

	/**
	 * @return
	 */
	private Component getAppsInspectorPanel() {
		if (appsInspectorPanel == null) {
			appsInspectorPanel = new JPanel();
			appsInspectorPanel.setLayout(new BorderLayout());
			appsInspectorPanel.add(getListOfApps(), BorderLayout.CENTER);
			appsInspectorPanel.add(getAppDetailsPane(), BorderLayout.SOUTH);
		}
		return appsInspectorPanel;
	}

	/**
	 * @return The strip of buttons on the right.
	 */
	private Component getControls() {
		if (controls == null) {
			controls = new JPanel();
			controls.setLayout(new StackedLayout(StackedLayout.VERTICAL));
			controls.add(getDesignSheetsButton(), "TopWide");
			controls.add(getRenderSheetsButton(), "TopWide");
			controls.add(Box.createVerticalStrut(10), "TopWide");
			controls.add(getStartApplicationButton(), "TopWide");
			controls.add(getEventBrowserButton(), "TopWide");
			controls.add(getStopApplicationButton(), "TopWide");
		}
		return controls;
	}

	/**
	 * @return
	 */
	private Component getDesignSheetsButton() {
		if (designSheetsButton == null) {
			designSheetsButton = new JButton("Design Sheets");
			designSheetsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JFrame frame = AcrobatDesignerLauncher.start();
					frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				}
			});
		}
		return designSheetsButton;
	}

	/**
	 * @return button for accessing the EventReplayManager's GUI, which allows us to load up and
	 *         replay eventData files.
	 */
	private Component getEventBrowserButton() {
		if (eventBrowserButton == null) {
			eventBrowserButton = new JButton("View/Replay Events");
			eventBrowserButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (eventBrowser == null) {
						eventBrowser = new EventBrowser(eventEngine.getEventReplayManager());
					}
					eventBrowser.setVisible(true);
				}
			});
		}
		return eventBrowserButton;
	}

	/**
	 * EXPERTS ONLY: Interact with the EventEngine at runtime!
	 * 
	 * @return
	 */
	public EventEngine getEventEngine() {
		return eventEngine;
	}

	/**
	 * @return
	 */
	private Component getExitAppManagerButton() {
		// stop all apps and then exit the application manager
		if (exitAppManagerButton == null) {
			exitAppManagerButton = new JButton("Exit App Manager");
			exitAppManagerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("Stopping all Applications...");
					Object[] objects = runningApplications.toArray();
					for (Object o : objects) {
						stopApplication((Application) o);
					}
					System.out.println("Exiting the Paper Toolkit Application Manager...");
					System.exit(0);
				}
			});
		}
		return exitAppManagerButton;
	}

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
	 * @return where to save our Sheet PDFs. NULL if the user cancels.
	 */
	private File getFolderToSavePDFs() {
		return FileUtils.showDirectoryChooser(appManager, "Choose a Directory for your PDFs");
	}

	/**
	 * @return a GUI list of loaded applications (running or not).
	 */
	private JXList getListOfApps() {
		if (listOfApps == null) {
			listOfApps = new JXList();
			listOfApps.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					Application selectedApp = (Application) listOfApps.getSelectedValue();
					if (selectedApp != null) {
						// show a list of sheets
						final List<Sheet> thisAppsSheets = selectedApp.getSheets();
						StringBuilder sb = new StringBuilder();
						for (Sheet s : thisAppsSheets) {
							// use the longer, more descriptive string
							sb.append(s.toDetailedString());
						}
						getAppDetailsTextArea().setText(sb.toString());
					}
				}
			});

			listOfApps.addHighlighter(new ConditionalHighlighter(Color.WHITE, Color.LIGHT_GRAY, 0,
					-1) {
				@Override
				protected boolean test(ComponentAdapter c) {
					if (c.getValue() instanceof Application) {
						Application app = (Application) c.getValue();
						if (!runningApplications.contains(app)) { // loaded, but not running
							return true;
						}
					}
					return false;
				}
			});
			listOfApps.setCellRenderer(new DefaultListCellRenderer() {
				public Component getListCellRendererComponent(JList list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					String appDescription = value.toString();
					if (value instanceof Application) {
						Application app = (Application) value;
						if (runningApplications.contains(app)) { // loaded, but not running
							appDescription = appDescription + " [running]";
						} else {
							appDescription = appDescription + " [stopped]";
						}
					}
					return super.getListCellRendererComponent(list, appDescription, index,
							isSelected, cellHasFocus);
				}
			});
			listOfApps.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
			listOfApps.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listOfApps.setFont(APP_MANAGER_FONT);
			updateListOfApps();
		}
		return listOfApps;
	}

	/**
	 * @return
	 */
	private Component getMainMessage() {
		if (mainMessage == null) {
			mainMessage = new JLabel("<html>Manage your applications here.<br/>"
					+ "Closing this App Manager will stop <b>all</b> running applications.</html>");
			mainMessage.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
			mainMessage.setFont(APP_MANAGER_FONT);
		}
		return mainMessage;
	}

	private ListModel getModel() {
		final ListModel model = new AbstractListModel() {
			public Object getElementAt(int appIndex) {
				return loadedApplications.get(appIndex);
			}

			public int getSize() {
				return loadedApplications.size();
			}
		};
		return model;
	}

	private PopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new PopupMenu("Paper Toolkit Options");

			// exit the application
			final MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(getExitListener());

			popupMenu.add(exitItem);
			popupMenu.add(new MenuItem("-")); // separator
		}

		return popupMenu;
	}

	private ActionListener getDebugListener(final Application app) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DebugUtils.println("Starting Debugging...");
				new DebuggingEnvironment(app);
			}
		};
	}

	/**
	 * @return turn sheets into PDF files.
	 */
	private Component getRenderSheetsButton() {
		if (printSheetsButton == null) {
			printSheetsButton = new JButton("Make PDFs", new ImageIcon(PaperToolkit.class
					.getResource("/icons/pdfIcon32x32.png")));
			printSheetsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					final Application selectedApp = (Application) listOfApps.getSelectedValue();
					if (selectedApp != null) {
						if (selectedApp.isUserChoosingDestinationForPDF()) {
							renderToSpecificFolder(selectedApp);
						} else {
							selectedApp.renderToPDF();
						}
						listOfApps.repaint();
					}
				}

			});
		}
		return printSheetsButton;
	}

	/**
	 * @return
	 */
	private Component getStartApplicationButton() {
		if (startAppButton == null) {
			startAppButton = new JButton("Start Selected Application");
			startAppButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Application selectedApp = (Application) listOfApps.getSelectedValue();
					if (selectedApp != null) {
						startApplication(selectedApp);
						listOfApps.repaint();
					}
				}
			});
		}
		return startAppButton;
	}

	/**
	 * @return
	 */
	private Component getStopApplicationButton() {
		if (stopAppButton == null) {
			stopAppButton = new JButton("Stop Selected Application");
			stopAppButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Application selectedApp = (Application) listOfApps.getSelectedValue();
					if (selectedApp != null) {
						stopApplication(selectedApp);
						listOfApps.repaint();
					}
				}
			});
		}
		return stopAppButton;
	}

	private void getSystemTrayIcon() {
		if (trayIcon == null) {
			// this is the icon that sits in our tray...
			trayIcon = new TrayIcon(ImageCache.loadBufferedImage(PaperToolkit.class
					.getResource("/icons/glue.png")), "Paper Toolkit", getPopupMenu());
			trayIcon.setImageAutoSize(true);
			try {
				if (SystemTray.isSupported()) {
					SystemTray.getSystemTray().add(trayIcon);

					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
						@Override
						public void run() {
							if (trayIcon != null) {
								SystemTray.getSystemTray().remove(trayIcon);
							}
						}
					}));
				}
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds an application to the loaded list, and displays the application manager.
	 * 
	 * @param app
	 */
	public void loadApplication(Application app) {
		DebugUtils.println("Loading " + app.getName());
		loadedApplications.add(app);
		// show the app manager
		if (useAppManager) {
			getApplicationManager();
			updateListOfApps();
		} else {
			DebugUtils.println("Not using the Application Manager. "
					+ "If you would like to use the GUI launcher, "
					+ "call PaperToolkit.useAppManager(true)");
			DebugUtils.println("We will use the System Tray icon instead.");
			getSystemTrayIcon();

			final MenuItem debugItem = new MenuItem("Debug [" + app.getName() + "]");
			debugItem.addActionListener(getDebugListener(app));

			getPopupMenu().add(debugItem); // separator
		}
	}

	/**
	 * Load the configuration information on startup...
	 */
	private void loadStartupConfiguration() {
		final Properties props = Configuration.getPropertiesFromConfigFile(CONFIG_FILE_KEY);
		useHandwriting = Boolean.parseBoolean(props.getProperty("handwritingRecognition"));
	}

	/**
	 * TODO: Figure out the easiest way to send a PDF (with or without regions) to the default
	 * printer.
	 * 
	 * @param sheet
	 */
	public void print(Sheet sheet) {
		// Implement this...
		DebugUtils.println("Unimplemented Method");
	}

	/**
	 * @param selectedApp
	 */
	private void renderToSpecificFolder(final Application selectedApp) {
		new Thread(new Runnable() {
			public void run() {
				final File folderToSavePDFs = getFolderToSavePDFs();
				if (folderToSavePDFs != null) { // user approved
					// an endless progress bar
					progress = new EndlessProgressDialog(appManager, "Creating the PDF",
							"Please wait while your PDF is generated.");
					// start rendering
					selectedApp.renderToPDF(folderToSavePDFs, selectedApp.getName());
					DebugUtils.println("Done Rendering.");

					// open the folder in explorer! =)
					try {
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
	 * Start this application and register all live pens with the event engine. The event engine
	 * will then start dispatching events for this application until the application is stopped.
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
		if (paperApp.getPens().size() == 0) {
			DebugUtils.println(paperApp.getName()
					+ " does not have any pens! We will add a single streaming pen for you.");
			final Pen aPen = new Pen();
			paperApp.addPen(aPen);
		}

		final List<Pen> pens = paperApp.getPens();
		// add all the live pens to the eventEngine
		for (Pen pen : pens) {
			pen.startLiveMode(); // starts live mode at the pen's default place
			if (pen.isLive()) {
				eventEngine.register(pen);
			}
		}

		// keep track of the pattern assigned to different sheets and regions
		eventEngine.registerPatternMapsForEventHandling(paperApp.getPatternMaps());
		batchServer.registerBatchEventHandlers(paperApp.getBatchEventHandlers());

		// XXX
		// Here, we should pass the event engine over...
		// When the Batch Server gets in the data, it will translate it to streaming event
		// coordinates
		// And then pass it to the Event Engine
		// It will essentially "replay" the events as if it were through event save/replay

		DebugUtils.println("Starting Application: " + paperApp.getName());
		runningApplications.add(paperApp);
		getListOfApps().repaint();

		paperApp.setHostToolkit(this);
	}

	/**
	 * Remove the application and stop receiving events from its pens....
	 * 
	 * @param paperApp
	 */
	public void stopApplication(Application paperApp) {
		final List<Pen> pens = paperApp.getPens();
		for (Pen pen : pens) {
			if (pen.isLive()) {
				eventEngine.unregisterPen(pen);
				// stop the pen from listening!
				pen.stopLiveMode();
			}
		}

		eventEngine.unregisterPatternMapsForEventHandling(paperApp.getPatternMaps());
		batchServer.unregisterBatchEventHandlers(paperApp.getBatchEventHandlers());

		DebugUtils.println("Stopping Application: " + paperApp.getName());
		runningApplications.remove(paperApp);
		getListOfApps().repaint();

		paperApp.setHostToolkit(null);
	}

	/**
	 * @param app
	 */
	public void unloadApplication(Application app) {
		loadedApplications.remove(app);
	}

	/**
	 * 
	 */
	private void updateListOfApps() {
		listOfApps.setModel(getModel());
		if (getModel().getSize() > 0) {
			listOfApps.setSelectedIndex(0);
		}
	}

	/**
	 * @param flag
	 *            whether or not to load the app manager when you load an application.
	 */
	public void useApplicationManager(boolean flag) {
		useAppManager = flag;
	}
}
