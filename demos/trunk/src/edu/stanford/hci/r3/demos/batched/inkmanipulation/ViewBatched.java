package edu.stanford.hci.r3.demos.batched.inkmanipulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import papertoolkit.PaperToolkit;
import papertoolkit.application.Application;
import papertoolkit.events.handlers.InkCollector;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.pattern.coordinates.PatternLocationToSheetLocationMapping;
import papertoolkit.pen.Pen;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.PenAdapter;
import papertoolkit.tools.components.InkPCanvas;
import papertoolkit.units.Inches;
import papertoolkit.units.PatternDots;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.layout.RiverLayout;


/**
 * <p>
 * A simple demo to allow the user to create ONE region on ARBITRARY pattern (in streaming mode) by
 * drawing a big rectangle. This is saved as a regions XML file. Then, the user turns off streaming
 * to write on the region. Finally, the pen is synchronized, and we are presented with a timeline
 * control that allows us to animate the strokes that we wrote in that region...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ViewBatched {

	private static final String DRAW_RECT_DIRECTIONS = "Draw one Rectangle on your Patterned Sheet. Press the Space Bar when you are done.";

	private static final Font HEADING_FONT = new Font("Trebuchet MS", Font.BOLD, 20);

	private static final String SPACE_BAR_ACTION_NAME = "spacebarAction";

	private static final Color DEFAULT_STEP_COLOR = Color.WHITE;

	private static final Color CURRENT_STEP_COLOR = new Color(220, 110, 180);

	public static void main(String[] args) {
		ViewBatched batched = new ViewBatched();
		batched.askForNewRectangularRegion();
	}

	private Application app;

	private JLabel batchedLabel;

	private JLabel batchedLabel2;

	private Runnable currentAdvanceAction;

	private JLabel defineLabel;

	private JLabel defineLabel2;

	private JFrame frame;

	private JTextField heightField;

	private JLabel heightLabel;

	private InkPCanvas inkCanvas;

	private JPanel mainPanel;

	private double maxX = Double.MIN_VALUE;

	private JTextField maxXField;

	private JLabel maxXLabel;

	private double maxY = Double.MIN_VALUE;

	private JTextField maxYField;

	private JLabel maxYLabel;

	private double minX = Double.MAX_VALUE;

	private JTextField minXField;

	private JLabel minXLabel;

	private double minY = Double.MAX_VALUE;

	private JTextField minYField;

	private JLabel minYLabel;

	private Pen pen;

	private JScrollPane scrollableView;

	private JLabel streamingLabel;

	private PaperToolkit toolkit;

	private JTextField widthField;

	private JLabel widthLabel;

	private JButton resetViewButton;

	/**
	 * 
	 */
	public ViewBatched() {
		app = new Application("Simple Batched Test");
		app.addPenInput(getPen());
		toolkit = new PaperToolkit();
		toolkit.startApplication(app);
		setupGUI();
	}

	/**
	 * Create a Sheet and Region on the fly. Add it to this app, with an ink collector.
	 */
	private void addSheetAndRegionToApp() {
		final double width = maxX - minX;
		final double height = maxY - minY;

		final PatternDots wDots = new PatternDots(width);
		final PatternDots hDots = new PatternDots(height);

		final Inches wInches = wDots.toInches();
		final Inches hInches = hDots.toInches();
		final double wInchesD = wInches.getValue();
		final double hInchesD = hInches.getValue();

		DebugUtils.println("Dimensions of the Paper Region: " + wInches + " " + hInches);

		// create a sheet object
		final Sheet sheet = new Sheet(wInchesD, hInchesD);
		final Region region = new Region("Main Inking Area", 0, 0, wInchesD, hInchesD);
		final InkCollector inkCollector = new InkCollector() {
			public void contentArrived() {
				// System.out.println("Last Stroke at: " + getTimestampOfMostRecentPenUp());
				// System.out.println("Num Strokes: " + getNumStrokesCollected());
				// display the ink in our Piccolo InkCanvas
				inkCanvas.setInk(getInk());
			}
		};
		region.addEventHandler(inkCollector);
		sheet.addRegion(region);

		// this is how you can dynamically register regions for event handling
		// first, create a custom mapping object
		final PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping(
				sheet);
		// tie the pattern bounds to this region object
		mapping.setPatternInformationOfRegion(region, //
				new PatternDots(minX), new PatternDots(minY), // 
				new PatternDots(width), new PatternDots(height));
		app.addSheet(sheet, mapping); // this addSheet is called at paper-application RUNTIME

		// update the GUI
		defineLabel.setForeground(DEFAULT_STEP_COLOR);
		streamingLabel.setForeground(CURRENT_STEP_COLOR);
	}

	/**
	 * Add a single listener that allows us to define an interactive region on the fly.
	 */
	private void askForNewRectangularRegion() {
		getPen().addLivePenListener(new PenAdapter() {
			public void sample(PenSample sample) {
				minX = Math.min(minX, sample.x);
				minY = Math.min(minY, sample.y);
				maxX = Math.max(maxX, sample.x);
				maxY = Math.max(maxY, sample.y);
				updateTextFields();
			}

		});

		// set the space bar to 1) disable the space bar, and then 2) add the region at runtime.
		setCurrentAdvanceAction(new Runnable() {
			public void run() {
				// run only once!
				setCurrentAdvanceAction(null);
				addSheetAndRegionToApp();
			}

		});
	}

	/**
	 * @return
	 */
	private Pen getPen() {
		if (pen == null) {
			pen = new Pen();

		}
		return pen;
	}

	private Component getScrollableMainPanel() {
		if (scrollableView == null) {
			scrollableView = new JScrollPane(mainPanel);
		}
		return scrollableView;
	}

	private void populateMainPanel() {
		mainPanel = new JPanel(new RiverLayout());

		mainPanel.add("left", defineLabel);
		mainPanel.add("br left", defineLabel2);

		mainPanel.add("br center", minXLabel);
		mainPanel.add(minXField);
		mainPanel.add(maxXLabel);
		mainPanel.add(maxXField);

		mainPanel.add("br center", minYLabel);
		mainPanel.add(minYField);
		mainPanel.add(maxYLabel);
		mainPanel.add(maxYField);

		mainPanel.add("br center", widthLabel);
		mainPanel.add(widthField);
		mainPanel.add(heightLabel);
		mainPanel.add(heightField);

		mainPanel.add("br", Box.createVerticalStrut(10));
		mainPanel.add("br left", streamingLabel);
		mainPanel.add("br right", resetViewButton);
		mainPanel.add("br center vfill hfill", inkCanvas);

		mainPanel.add("br", Box.createVerticalStrut(10));
		mainPanel.add("br left", batchedLabel);
		mainPanel.add("br left", batchedLabel2);
	}

	/**
	 * 
	 */
	private void runCurrentAdvanceAction() {
		if (currentAdvanceAction != null) {
			new Thread(currentAdvanceAction).start();
		}
	}

	/**
	 * @param r
	 */
	private void setCurrentAdvanceAction(Runnable r) {
		currentAdvanceAction = r;
	}

	/**
	 * Create and assemble all the Swing GUI components here...
	 */
	private void setupGUI() {

		// The Components for the top third of the GUI
		defineLabel = new JLabel("Define the Region");
		defineLabel.setFont(HEADING_FONT);
		defineLabel.setForeground(CURRENT_STEP_COLOR);
		defineLabel2 = new JLabel("<html>" + DRAW_RECT_DIRECTIONS + "</html>");

		minXLabel = new JLabel("Min X:");
		minYLabel = new JLabel("Min Y:");
		maxXLabel = new JLabel("Max X:");
		maxYLabel = new JLabel("Max Y:");
		widthLabel = new JLabel("Width:");
		heightLabel = new JLabel("Height:");

		minXField = new JTextField(20);
		minYField = new JTextField(20);
		maxXField = new JTextField(20);
		maxYField = new JTextField(20);
		widthField = new JTextField(20);
		heightField = new JTextField(20);

		minXField.setForeground(Color.WHITE);
		minYField.setForeground(Color.WHITE);
		maxXField.setForeground(Color.WHITE);
		maxYField.setForeground(Color.WHITE);
		widthField.setForeground(Color.WHITE);
		heightField.setForeground(Color.WHITE);

		minXField.setEditable(false);
		minYField.setEditable(false);
		maxYField.setEditable(false);
		maxXField.setEditable(false);
		widthField.setEditable(false);
		heightField.setEditable(false);

		// The Components for the middle third of the GUI
		streamingLabel = new JLabel("Try Drawing in Streaming Mode");
		streamingLabel.setFont(HEADING_FONT);

		resetViewButton = new JButton("Reset View");
		resetViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				inkCanvas.resetViewOffsetAndScale();
			}
		});

		inkCanvas = new InkPCanvas();
		// inkCanvas.setPreferredSize(new Dimension(800, 480));

		batchedLabel = new JLabel("Try Drawing in BatchedMode");
		batchedLabel.setFont(HEADING_FONT);
		batchedLabel2 = new JLabel("<html>Turn off Streaming Mode. Continue to draw in the "
				+ "region you defined. Finally, synchronize your pen.</html>");

		// /////////////////////////////////////////////
		// populate the main panel with our components
		populateMainPanel();

		// //////////////////
		// create the frame
		frame = new JFrame("Simple Batched Test");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(getScrollableMainPanel(), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		setupKeyboardBindings();
	}

	private void setupKeyboardBindings() {
		InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("SPACE"), SPACE_BAR_ACTION_NAME);
		mainPanel.getActionMap().put(SPACE_BAR_ACTION_NAME,
				new AbstractAction(SPACE_BAR_ACTION_NAME) {
					public void actionPerformed(ActionEvent ae) {
						DebugUtils.println("Space Bar Pressed");
						runCurrentAdvanceAction();
					}

				});
	}

	private void updateTextFields() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					minXField.setText("" + minX);
					minYField.setText("" + minY);
					maxXField.setText("" + maxX);
					maxYField.setText("" + maxY);
					widthField.setText("" + (maxX - minX));
					heightField.setText("" + (maxY - minY));
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
