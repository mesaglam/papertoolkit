package edu.stanford.hci.r3.events.replay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import prefuse.Display;
import prefuse.data.query.NumberRangeModel;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pattern.calibrate.CalibrationEngine;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.WindowUtils;
import edu.stanford.hci.r3.util.components.JRangeSlider;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * The GUI for the Event Browser. It contains a JFrame with some cool visualizations that allow us to
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class EventBrowserView extends JFrame {

	/**
	 * The model.
	 */
	private EventBrowser browser;

	private JPanel commandsPanel;

	private Display display;

	private JPanel eventSliderPanel;

	private JPanel eventVizView;

	private JButton playButton;

	private BoundedRangeModel rangeModel;

	private JRangeSlider rangeSlider;

	private JTextArea infoArea;

	private JPanel bottomPanel;

	private JButton importButton;

	private JButton clearButton;

	private JButton playFastButton;

	/**
	 * @param browser
	 * @param disp
	 */
	public EventBrowserView(EventBrowser brows, Display disp) {
		browser = brows;
		display = disp;
		setTitle("Event Browser");
		setSize(800, 320);
		setupComponents();

		// this frame is displayed by the application manager
		// so it should not exit the application on close

		// center it relative to the dekstop
		setLocation(WindowUtils.getWindowOrigin(this, WindowUtils.DESKTOP_CENTER));
	}

	/**
	 * @return
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					browser.clearLoadedEvents();
				}
			});
		}
		return clearButton;
	}

	/**
	 * @return
	 */
	private Component getCommandsPanel() {
		if (commandsPanel == null) {
			commandsPanel = new JPanel();
			commandsPanel.add(getClearButton());
			commandsPanel.add(getImportButton());
			commandsPanel.add(getExportButton());
			commandsPanel.add(getPlayButton());
		}
		return commandsPanel;
	}

	private Component getInfoArea() {
		if (infoArea == null) {
			infoArea = new JTextArea();
			infoArea.setEditable(false);
			infoArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
			infoArea.setBorder(BorderFactory.createEmptyBorder(7, 5, 7, 5));
			infoArea.setForeground(Color.WHITE);
			infoArea.setText("Ready...");
		}
		return infoArea;
	}

	/**
	 * @return
	 */
	private Component getEventSliderPanel() {
		if (eventSliderPanel == null) {
			eventSliderPanel = new JPanel();
			eventSliderPanel.setLayout(new BorderLayout());
			eventSliderPanel.add(getEventVizView(), BorderLayout.SOUTH);
			eventSliderPanel.add(getRangeSlider(), BorderLayout.SOUTH);
		}
		return eventSliderPanel;
	}

	/**
	 * @return
	 */
	private Component getEventVizView() {
		if (eventVizView == null) {
			eventVizView = new JPanel();
			eventVizView.setLayout(new BorderLayout());
			eventVizView.add(display, BorderLayout.CENTER);
		}
		return eventVizView;
	}

	/**
	 * @return
	 */
	private JButton getExportButton() {
		return new JButton("Export");
	}

	/**
	 * @return
	 */
	private JButton getImportButton() {
		if (importButton == null) {
			importButton = new JButton("Import");
			importButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = FileUtils.createNewFileChooser(EventReplayManager.FILE_EXTENSION);
					chooser.setCurrentDirectory(new File(PaperToolkit.getToolkitRootPath(), "eventData/"));
					chooser.setMultiSelectionEnabled(true);
					int result = chooser.showDialog(null, "Import Event Data");
					if (result == JFileChooser.APPROVE_OPTION) {
						File[] selectedFiles = chooser.getSelectedFiles();
						for (File f : selectedFiles) {
							DebugUtils.println("Loading " + f);
							browser.loadEventData(f);
						}
					}

				}
			});
		}
		return importButton;
	}

	/**
	 * @return
	 */
	private JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton("Play");
			playButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						DebugUtils.println("Waiting 1.5 seconds...");
						Thread.sleep(1500);
						DebugUtils.println("Play Events Now...");
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					browser.replayLoadedEvents();
				}
			});
		}
		return playButton;
	}

	private JButton getPlayFastButton() {
		if (playFastButton == null) {
			playFastButton = new JButton("Play Fast");
			playFastButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						DebugUtils.println("Waiting 1.5 seconds...");
						Thread.sleep(1500);
						DebugUtils.println("Play Events Now...");
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					browser.replayLoadedEvents();
				}
			});
		}
		return playFastButton;
	}

	/**
	 * @return
	 */
	private BoundedRangeModel getRangeModel() {
		if (rangeModel == null) {
			rangeModel = new NumberRangeModel(0, 1000, 0, 1000);
		}
		return rangeModel;
	}

	/**
	 * @return
	 */
	private Component getRangeSlider() {
		if (rangeSlider == null) {
			rangeSlider = new EventSlider(getRangeModel(), JRangeSlider.HORIZONTAL,
					JRangeSlider.LEFTRIGHT_TOPBOTTOM);
			rangeSlider.setBackground(Color.DARK_GRAY);
			rangeSlider.setForeground(new Color(202, 217, 253));
			rangeSlider.setThumbColor(new Color(249, 248, 244));
			rangeSlider.setMinExtent(1); // between the two handles
			rangeSlider.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					DebugUtils.println("Pressed");
				}

				public void mouseReleased(MouseEvent e) {
					DebugUtils.println("Released");
				}
			});

		}
		return rangeSlider;
	}

	/**
	 * 
	 */
	private void setupComponents() {
		final Container contentPane = getContentPane();
		contentPane.add(getEventSliderPanel(), BorderLayout.CENTER);
		contentPane.add(getBottomPanel(), BorderLayout.SOUTH);
	}

	private Component getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.add(getInfoArea(), BorderLayout.CENTER);
			bottomPanel.add(getCommandsPanel(), BorderLayout.EAST);

		}
		return bottomPanel;
	}
}
