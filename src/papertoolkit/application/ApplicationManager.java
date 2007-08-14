package papertoolkit.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;

import papertoolkit.PaperToolkit;
import papertoolkit.paper.Sheet;
import papertoolkit.tools.design.acrobat.AcrobatDesignerLauncher;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.WindowUtils;
import papertoolkit.util.components.EndlessProgressDialog;
import papertoolkit.util.files.FileUtils;
import papertoolkit.util.layout.StackedLayout;

/**
 * <p>
 * A GUI for running multiple applications, designing sheets, or accessing the tool explorer.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class ApplicationManager {

	/**
	 * Font for the App Manager GUI.
	 */
	private static final Font APP_MANAGER_FONT = new Font("Trebuchet MS", Font.PLAIN, 18);

	private JScrollPane appDetailsScrollPane;

	private JTextArea appDetailsTextArea;

	/**
	 * Stop, run, pause applications.
	 */
	private JFrame appManager;

	private JPanel appsInspectorPanel;

	/**
	 * The buttons on the right side of the app manager.
	 */
	private JPanel controls;

	/**
	 * 
	 */
	private JButton designSheetsButton;

	/**
	 * Exits the app manager, and the toolkit too!
	 */
	private JButton exitAppManagerButton;

	/**
	 * Visual list of loaded (and possibly running) apps.
	 */
	private JXList listOfApps;

	/**
	 * Description for the app manager.
	 */
	private JLabel mainMessage;

	/**
	 * A button to ask the toolkit to create patterned PDFs.
	 */
	private JButton printSheetsButton;

	/**
	 * Progress bar... for when we are rendering, etc.
	 */
	private EndlessProgressDialog progress;

	/**
	 * Starts the selected application.
	 */
	private JButton startAppButton;

	/**
	 * Stops the selected application.
	 */
	private JButton stopAppButton;

	private PaperToolkit toolkit;

	public ApplicationManager(PaperToolkit paperToolkit) {
		toolkit = paperToolkit;
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
		if (appDetailsTextArea == null) {
			appDetailsTextArea = new JTextArea(8, 50 /* cols */);
			appDetailsTextArea.setBackground(new Color(240, 240, 240));
			appDetailsTextArea.setEditable(false);
		}
		return appDetailsTextArea;
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
			appManager.setLocation(WindowUtils.getWindowOrigin(appManager, WindowUtils.DESKTOP_CENTER));
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
			controls.add(getStopApplicationButton(), "TopWide");
		}
		return controls;
	}

	/**
	 * @return the button to load the Acrobat plugin for designing Paper UIs (drawing out regions)...
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
	 * @return
	 */
	private Component getExitAppManagerButton() {
		// stop all apps and then exit the application manager
		if (exitAppManagerButton == null) {
			exitAppManagerButton = new JButton("Exit App Manager");
			exitAppManagerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					// DebugUtils.println("Stopping all Applications...");
					final Object[] objects = toolkit.getRunningApplications().toArray();
					for (Object o : objects) {
						toolkit.stopApplication((Application) o);
					}
					// DebugUtils.println("Exiting the Paper Toolkit & Application Manager...");
					System.exit(0);
				}
			});
		}
		return exitAppManagerButton;
	}

	/**
	 * @return where to save our Sheet's PDFs. NULL if the user cancels.
	 */
	private File getFolderToSavePDFs() {
		return FileUtils.showDirectoryChooser(appManager, "Choose a Directory for your PDFs");
	}

	/**
	 * @return a list of applications for the application manager.
	 */
	private ListModel getListModel() {
		final ListModel model = new AbstractListModel() {
			public Object getElementAt(int appIndex) {
				return toolkit.getLoadedApplications().get(appIndex);
			}

			public int getSize() {
				return toolkit.getLoadedApplications().size();
			}
		};
		return model;
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

			listOfApps.addHighlighter(new ConditionalHighlighter(Color.WHITE, Color.LIGHT_GRAY, 0, -1) {
				protected boolean test(ComponentAdapter c) {
					if (c.getValue() instanceof Application) {
						Application app = (Application) c.getValue();
						if (!toolkit.getRunningApplications().contains(app)) { // loaded, but not running
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
						if (toolkit.getRunningApplications().contains(app)) { // loaded, but not running
							appDescription = appDescription + " [running]";
						} else {
							appDescription = appDescription + " [stopped]";
						}
					}
					return super.getListCellRendererComponent(list, appDescription, index, isSelected,
							cellHasFocus);
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
	 * @return a label for the App Manager. Tells Users what they can do.
	 */
	private Component getMainMessage() {
		if (mainMessage == null) {
			mainMessage = new JLabel(
					"<html>Manage your applications here.<br/>"
							+ "Closing this App Manager will stop <b>all</b> running applications, and exit the PaperToolkit!</html>");
			mainMessage.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
			mainMessage.setFont(APP_MANAGER_FONT);
		}
		return mainMessage;
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
							renderPDFToSpecificFolder(selectedApp);
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
	 * @return a button to start the currently selected application.
	 */
	private Component getStartApplicationButton() {
		if (startAppButton == null) {
			startAppButton = new JButton("Start Selected Application");
			startAppButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Application selectedApp = (Application) listOfApps.getSelectedValue();
					if (selectedApp != null) {
						toolkit.startApplication(selectedApp);
						listOfApps.repaint();
					}
				}
			});
		}
		return startAppButton;
	}

	/**
	 * @return a button to stop the currently selected application.
	 */
	private Component getStopApplicationButton() {
		if (stopAppButton == null) {
			stopAppButton = new JButton("Stop Selected Application");
			stopAppButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Application selectedApp = (Application) listOfApps.getSelectedValue();
					if (selectedApp != null) {
						toolkit.stopApplication(selectedApp);
						listOfApps.repaint();
					}
				}
			});
		}
		return stopAppButton;
	}

	/**
	 * Renders a PDF of the Application's sheets.
	 * 
	 * @param selectedApp
	 */
	private void renderPDFToSpecificFolder(final Application selectedApp) {
		new Thread(new Runnable() {
			public void run() {
				final File folderToSavePDFs = getFolderToSavePDFs();
				if (folderToSavePDFs != null) { // user approved
					// an endless progress bar
					progress = new EndlessProgressDialog(appManager, "Creating the PDF",
							"Please wait while your PDF is generated.");
					// start rendering
					selectedApp.renderToPDF(folderToSavePDFs, selectedApp.getName());
					// DebugUtils.println("Done Rendering.");

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
	 * Refreshes the Swing component that lists the applications.
	 */
	public void repaintListOfApps() {
		getListOfApps().repaint();
	}

	/**
	 * Refresh the visual list of applications that are running...
	 */
	public void updateListOfApps() {
		listOfApps.setModel(getListModel());
		if (getListModel().getSize() > 0) {
			listOfApps.setSelectedIndex(0);
		}
	}
}
