package edu.stanford.hci.r3.demos.batched;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.filters.InkCollector;
import edu.stanford.hci.r3.paper.Region;
import edu.stanford.hci.r3.paper.Sheet;
import edu.stanford.hci.r3.pattern.coordinates.PatternLocationToSheetLocationMapping;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.streaming.PenAdapter;
import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.layout.RiverLayout;

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
public class SimpleBatched {

	private static final String SPACE_BAR_ACTION_NAME = "spacebarAction";

	public static void main(String[] args) {
		SimpleBatched batched = new SimpleBatched();
		batched.askForNewRectangularRegion();
	}

	private Application app;

	private Runnable currentAdvanceAction;

	private JFrame frame;

	private JTextField heightField;

	private JPanel mainPanel;

	private double maxX = Double.MIN_VALUE;

	private JTextField maxXField;

	private double maxY = Double.MIN_VALUE;

	private JTextField maxYField;

	private double minX = Double.MAX_VALUE;

	private JTextField minXField;

	private double minY = Double.MAX_VALUE;

	private JTextField minYField;

	private Pen pen;

	private PaperToolkit toolkit;

	private JTextField widthField;

	/**
	 * 
	 */
	public SimpleBatched() {
		app = new Application("Simple Batched Test");
		app.addPen(getPen());
		toolkit = new PaperToolkit();
		toolkit.startApplication(app);
		setupGUI();
	}

	/**
	 * Create a Sheet and Region on the fly. Add it to this app, with an ink collector.
	 */
	private void addSheetAndRegionToApp() {
		double width = maxX - minX;
		double height = maxY - minY;

		PatternDots wDots = new PatternDots(width);
		PatternDots hDots = new PatternDots(height);

		Inches wInches = wDots.toInches();
		Inches hInches = hDots.toInches();
		final double wInchesD = wInches.getValue();
		final double hInchesD = hInches.getValue();

		DebugUtils.println(wInches + " " + hInches);

		// create a sheet object
		final Sheet s = new Sheet(wInchesD, hInchesD);
		final Region r = new Region("Main Inking Area", 0, 0, wInchesD, hInchesD);
		r.addContentFilter(new InkCollector() {
			public void contentArrived() {
				System.out.println("Last Stroke at: " + getTimestampOfMostRecentPenUp());
				System.out.println("Num Strokes: " + getNumStrokesCollected());
			}
		});

		s.addRegion(r);

		// create a custom mapping object
		final PatternLocationToSheetLocationMapping mapping = new PatternLocationToSheetLocationMapping(
				s);

		// tie the pattern bounds to this region object
		mapping.setPatternInformationOfRegion(r, //
				new PatternDots(minX), new PatternDots(minY), // 
				new PatternDots(width), new PatternDots(height));
		app.addSheet(s, mapping);
	}

	/**
	 * 
	 */
	private void askForNewRectangularRegion() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		getPen().addLivePenListener(new PenAdapter() {
			public void sample(PenSample sample) {
				minX = Math.min(minX, sample.x);
				minY = Math.min(minY, sample.y);
				maxX = Math.max(maxX, sample.x);
				maxY = Math.max(maxY, sample.y);
				updateTextFields();
			}

		});

		setCurrentAdvanceAction(new Runnable() {
			public void run() {
				setCurrentAdvanceAction(null);
				addSheetAndRegionToApp();
			}

		});

		System.out.println();
		System.out.println("-------------- Simple Batched Processing Test --------------");
		System.out
				.println("Draw one Rectangle on your Patterned Sheet. Press the Space Bar when you are done.");
	}

	private Pen getPen() {
		if (pen == null) {
			pen = new Pen();

		}
		return pen;
	}

	private void runCurrentAdvanceAction() {
		if (currentAdvanceAction != null) {
			new Thread(currentAdvanceAction).start();
		}
	}

	private void setCurrentAdvanceAction(Runnable r) {
		currentAdvanceAction = r;
	}

	private static final Font HEADING_FONT = new Font("Trebuchet MS", Font.BOLD, 20);

	private void setupGUI() {

		// The Components for the top third of the GUI
		JLabel defineLabel = new JLabel("Define the Region");
		defineLabel.setFont(HEADING_FONT);

		Component minXLabel = new JLabel("Min X:");
		Component minYLabel = new JLabel("Min Y:");
		Component maxXLabel = new JLabel("Max X:");
		Component maxYLabel = new JLabel("Max Y:");
		Component widthLabel = new JLabel("Width:");
		Component heightLabel = new JLabel("Height:");

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
		JLabel streamingLabel = new JLabel("Try Drawing in Streaming Mode");
		streamingLabel.setFont(HEADING_FONT);

		// populate the main panel with our components
		mainPanel = new JPanel(new RiverLayout());

		mainPanel.add("left", defineLabel);

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

		// create the frame
		frame = new JFrame("Simple Batched Test");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(mainPanel, BorderLayout.CENTER);
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
					public void actionPerformed(ActionEvent arg0) {
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
