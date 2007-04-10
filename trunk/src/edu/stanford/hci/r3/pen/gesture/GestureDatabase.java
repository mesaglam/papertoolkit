package edu.stanford.hci.r3.pen.gesture;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.components.InkPanel;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.render.ink.InkRenderer;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.util.WindowUtils;

public class GestureDatabase implements ActionListener, FocusListener {
	private transient static JTextField entryField;

	private transient static JFrame inkDisplay;

	private transient static InkPanel inkPanel;

	transient static GestureDatabase instance;

	private transient static JLabel labelField;

	private transient static JPanel mainPanel;

	private transient static JPanel statusPanel;

	private transient static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	/* Used by makeCompactGrid. */
	private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}

	static JFrame getInkDisplay() {
		if (inkDisplay == null) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			inkDisplay = new JFrame("Sketch! Display");
			inkDisplay.setContentPane(getMainPanel());
			inkDisplay.setSize(690, 740);
			inkDisplay.setLocation(WindowUtils.getWindowOrigin(inkDisplay, WindowUtils.DESKTOP_CENTER));
			inkDisplay.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			// inkDisplay.pack();
			inkDisplay.setVisible(true);
		}
		return inkDisplay;
	}

	/**
	 * @return
	 */
	private static Container getInkPanel() {
		if (inkPanel == null) {
			inkPanel = new InkPanel();
		}
		return inkPanel;
	}

	/**
	 * @return
	 */
	private static Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getInkPanel(), BorderLayout.CENTER);
			mainPanel.add(getStatusPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	/**
	 * @return
	 */
	private static Component getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new JPanel(new SpringLayout());
			String[] labelStrings = { "0: Add a gesture", "1: Add more of an existing gesture", "2: Save",
					"3: List", "4: Test (interactive)", "5: Add a test gesture (for existing)",
					"6: Run autotest", "7: Determine class parameters",
					"8: Assign class labels to unlabeled gestures", "9: Add unlabeled gestures",
					"10: Add unlabeled test gestures", "11: Optimize cost weighting",
					"12: Run leave-10%-out test", "13: Move gestures out of test",
					"14: Run leave-one-user-out test", "15: Compute best examples per class",
					"16: Run leave-10%-out test using only best examples", "17: Create best image",
					"18: Generate gesture", "-1: Exit", "Option: " };
			JLabel[] labels = new JLabel[labelStrings.length];
			JComponent[] fields = new JComponent[labelStrings.length];
			int fieldNum = 0;
			for (fieldNum = 0; fieldNum < labels.length - 1; fieldNum++) {
				fields[fieldNum] = new JLabel();
			}
			entryField = new JTextField();
			entryField.setColumns(20);
			fields[fieldNum++] = entryField;
			for (int i = 0; i < labelStrings.length; i++) {
				labels[i] = new JLabel(labelStrings[i], JLabel.TRAILING);
				labels[i].setLabelFor(fields[i]);
				statusPanel.add(labels[i]);
				statusPanel.add(fields[i]);
				JTextField tf = null;
				if (fields[i] instanceof JTextField) {
					labelField = labels[i];
					tf = (JTextField) fields[i];
					tf.addActionListener(instance);
					tf.addFocusListener(instance);
				}
			}
			int GAP = 10;
			makeCompactGrid(statusPanel, labelStrings.length, 2, GAP, GAP, GAP, GAP / 2);
		}
		return statusPanel;
	}

	public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY,
			int xPad, int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout) parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}

	private transient ArrayList<ShapeContext> bestExamples = null;

	private transient ArrayList<Gesture> bestGestures = null;

	private transient boolean commandMode;

	private transient Thread commandThread;

	String databaseName;

	ArrayList<Gesture> gestures = new ArrayList<Gesture>();

	private transient boolean inputAvailable;

	private transient String inputString;

	PenGestureListener listener;

	ArrayList<Gesture> testGestures = new ArrayList<Gesture>();

	ArrayList<ShapeContext> unlabeledContexts = new ArrayList<ShapeContext>();

	private ArrayList<ShapeContext> unlabeledTestContexts = new ArrayList<ShapeContext>();

	public GestureDatabase(String databaseName) {
		this.databaseName = databaseName;
		listener = new PenGestureListener();
	}

	public void actionPerformed(ActionEvent e) {
		if (commandMode) {
			inputString = e.getActionCommand();
			inputAvailable = true;
		} else {
			try {
				final int option = Integer.parseInt(e.getActionCommand());
				commandMode = true;
				commandThread = new Thread(new Runnable() {
					public void run() {
						try {
							GestureDatabase.instance.command(option);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				commandThread.start();
			} catch (NumberFormatException error) {
			}
		}
		entryField.setText("");
	}

	public void autotest() {
		autotest(gestures);
	}

	public void autotest(ArrayList<Gesture> gestures) {
		// use knn, most occurences or best average weight
		int tested = 0;
		int correct = 0;
		for (int i = 0; i < gestures.size(); i++) {
			Gesture gesture = gestures.get(i);
			Gesture testGesture = testGestures.get(i);
			for (ShapeContext context : testGesture.contexts) {
				tested++;
				String assignment = test(context, false, gestures);
				if (assignment.compareTo(gesture.name) == 0)
					correct++;
				else {
					test(context, true, gestures);
					System.out.println(gesture.name + " misclassified as " + assignment);
				}
			}
		}
		System.out.println("Tested " + tested + " contexts, " + correct + " correct.");
	}

	private void bestExamples() {
		double totalN = 0;
		int count = 0;
		for (Gesture gesture : gestures) {
			for (ShapeContext context : gesture.contexts) {
				count++;
				totalN += context.controlPoints.size();
			}
		}
		System.out.println("Average points per context: " + totalN / count);
		bestExamples = new ArrayList<ShapeContext>();
		bestGestures = new ArrayList<Gesture>();
		for (int i = 0; i < gestures.size(); i++) {
			Gesture gesture = gestures.get(i);
			ShapeContext best = null;
			double distance = Double.MAX_VALUE;
			System.out.println("Computing best example for " + gesture.name);
			if (gesture.contexts.size() == 1)
				best = gesture.contexts.get(0);
			else {
				for (int j = gesture.contexts.size() - 1; j >= 0; j--) {
					ShapeContext context = gesture.contexts.get(j);
					gesture.contexts.remove(context);
					// double d = gesture.bestMatch(context); // dumb; this just tells me who's
					// closest to their nearest neighbor. We want centroid.
					double d = gesture.averageMatch(context);
					if (d < distance) {
						distance = d;
						best = context;
					}
					gesture.contexts.add(context);
					// AVINOTE: remove this
					break;
				}
			}
			bestExamples.add(best);
			Gesture bestGesture = new Gesture(gesture.name);
			bestGesture.addGesture(best);
			bestGestures.add(bestGesture);
			print("Best example for " + gesture.name);
			display(best, mainPanel.getWidth() / 2, mainPanel.getHeight() / 2);
		}
	}

	private void chunk(double threshold) {
		Random rand = new Random();
		moveAllToTrain();
		for (int i = 0; i < gestures.size(); i++) {
			ArrayList<ShapeContext> contexts = gestures.get(i).contexts;
			ArrayList<ShapeContext> testContexts = testGestures.get(i).contexts;
			for (int j = contexts.size() - 1; j >= 0; j--) {
				if (rand.nextDouble() < threshold) {
					ShapeContext context = contexts.get(j);
					contexts.remove(context);
					testContexts.add(context);
				}
			}
		}
	}

	public void command(int option) throws IOException {
		// build a set of gestures
		// options: add a gesture (N times)
		// add more of a gesture (N)
		// remove some of a gesture (? with images?)
		// int option;
		// do{
		/*
		 * System.out.println("Options:\n" + "0: Add a gesture\n" + "1: Add more of an existing gesture\n" +
		 * "2: Save\n" + "3: List\n" + "4: Test (interactive)\n" + "5: Add a test gesture (for existing)\n" +
		 * "6: Run autotest\n" + "7: Determine class parameters\n" + "8: Assign class labels to unlabeled
		 * gestures\n" + "9: Add unlabeled gestures\n" + "-1: Exit");
		 */
		// option = Integer.parseInt(stdin.readLine());
		int count, index;
		String author;
		Gesture gesture;
		switch (option) {
		case 0: // add a gesture
			print("Name: ");
			String name = getString();
			print("Examples: ");
			count = Integer.parseInt(getString());
			gesture = new Gesture(name);
			gestures.add(gesture);
			testGestures.add(new Gesture(name + "TEST"));
			listener.setContexts(gesture.contexts, count);
			break;
		case 1: // add examples to a gesture
			print("Index: ");
			index = Integer.parseInt(getString());
			gesture = gestures.get(index);
			System.out.println("Name: " + gesture.name);
			print("Examples: ");
			count = Integer.parseInt(getString());
			listener.setContexts(gesture.contexts, count);
			break;
		case 3:
			System.out.println("Gestures:");
			for (int i = 0; i < gestures.size(); i++)
				System.out.println("Gesture " + gestures.get(i).name + ": " + gestures.get(i).size());
			System.out.println("Tests:");
			for (int i = 0; i < testGestures.size(); i++)
				System.out.println("Gesture " + testGestures.get(i).name + ": " + testGestures.get(i).size());
			System.out.println("Queued gestures: " + unlabeledContexts.size());
			System.out.println("Queued test gestures: " + unlabeledTestContexts.size());
			break;
		case 4:
			listener.setContexts(null, 0);
			listener.setDatabase(this);
			print("Hit enter to end test.");
			getString();
			listener.setDatabase(null);
		case 5: // add test examples to a gesture
			print("Name: ");
			name = getString();
			gesture = null;
			for (Gesture g : testGestures)
				if (g.name.compareTo(name + "TEST") == 0) {
					gesture = g;
					break;
				}
			print("Examples: ");
			count = Integer.parseInt(getString());
			listener.setContexts(gesture.contexts, count);
			break;
		case 6:
			// System.out.println("Test without time: ");
			// ShapeContext.bands = 2;
			// autotest();
			System.out.println("Test using time: ");
			ShapeContext.bands = 3;
			autotest();
			break;
		case 7:
			determineClassParameters();
			break;
		case 8:
			labelGestures();
			break;
		case 9:
			print("Author: ");
			author = getString();
			System.out.println(author);
			listener.setAuthor(author);
			listener.setContexts(unlabeledContexts, 1000);
			print("Hit enter to end test.");
			getString();
			break;
		case 10:
			print("Author: ");
			author = getString();
			System.out.println(author);
			listener.setAuthor(author);
			listener.setContexts(unlabeledTestContexts, 1000);
			print("Hit enter to end test.");
			getString();
			break;
		case 11:
			leaveOneOutOptimizeCostWeighting();
			break;
		case 12:
			leaveChunkOut(10);
			break;
		case 13:
			moveAllToTrain();
			break;
		case 14:
			leaveOneUserOut();
			break;
		case 15:
			bestExamples();
			break;
		case 16:
			testChunkOutOnBest(20);
			break;
		case 17:
			createBestImage();
			break;
		case 18:
			generateGesture();
			break;
		case -1:
			System.out.println("Exiting.");
			System.exit(0); // no automatic save on exit
		case 2:
			Writer writer = new FileWriter(new File("C:\\dev\\quill\\data\\" + databaseName + ".gp"));
			quillWrite(writer);
			Save(new FileWriter(new File(databaseName + ".xml")));
			break;
		}
		// Thread.yield();
		// } while (option > -1);
		commandMode = false;
		labelField.setText("Option:");
	}

	public void createBestImage() {
		if (bestExamples == null)
			bestExamples();
		// write 'em out, staggered.
		Ink ink = new Ink();
		inkPanel.clear();
		double max_range_x = 0;
		double max_range_y = 0;
		for (int i = 0; i < bestExamples.size(); i++) {
			double min_x = Double.MAX_VALUE;
			double min_y = Double.MAX_VALUE;
			double max_x = Double.MIN_VALUE;
			double max_y = Double.MIN_VALUE;
			for (PenSample sample : bestExamples.get(i).controlPoints) {
				min_x = Math.min(sample.x, min_x);
				min_y = Math.min(sample.y, min_y);
				max_x = Math.max(sample.x, max_x);
				max_y = Math.max(sample.y, max_y);

			}
			max_range_x = Math.max(max_range_x, max_x - min_x);
			max_range_y = Math.max(max_range_y, max_y - min_y);
		}
		for (int i = 0; i < bestExamples.size(); i++) {
			ShapeContext context = bestExamples.get(i);
			display(context, max_range_x * (2 * (i % 3) + 1), max_range_y * (2 * (i / 3) + 1));
			ink.addStroke(new InkStroke(context.controlPoints, new Points()));
		}
		InkRenderer renderer = new InkRenderer(ink);
		renderer.renderToJPEG(new File("best.jpg"), new Pixels(300), new Points(max_range_x * 7), new Points(
				max_range_y * 7));
	}

	public void determineClassParameters() {
		for (Gesture gesture : gestures)
			gesture.determineClassParameters();
	}

	public void display(ShapeContext context, double w, double h) {
		double min_x = Double.MAX_VALUE;
		double min_y = Double.MAX_VALUE;
		for (PenSample sample : context.controlPoints) {
			min_x = Math.min(sample.x, min_x);
			min_y = Math.min(sample.y, min_y);
		}
		for (PenSample sample : context.controlPoints) {
			sample.x += w - min_x;
			sample.y += h - min_y;
		}
		InkStroke stroke = new InkStroke(context.controlPoints, new Points());
		Ink ink = new Ink();
		ink.addStroke(stroke);
		inkPanel.clear();
		inkPanel.addInk(ink);
	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void generateGesture() {
		if (bestExamples == null)
			bestExamples();
		ShapeContext.bands = 3;
		int max_points = 0;
		for (ShapeContext context : bestExamples) {
			max_points = Math.max(max_points, context.controlPoints.size());
		}
		Random random = new Random();
		ArrayList<PenSample> newSamples = new ArrayList<PenSample>();
		for (int i = 0; i < max_points; i++) {
			newSamples.add(new PenSample(random.nextDouble(), random.nextDouble(), 0, i));
		}
		ShapeContext noiseExample = new ShapeContext(newSamples, "noise");
		max_points = 10;
		ArrayList<PenSample> testSamples = new ArrayList<PenSample>();
		for (int i = 0; i < max_points; i++) {
			testSamples.add(new PenSample(random.nextDouble() * max_points * max_points, random.nextDouble()
					* max_points * max_points, 0, i));
		}
		ShapeContext testExample = new ShapeContext(testSamples, "test");
		double distanceMetric = -Double.MAX_VALUE;
		int maxIteration = 1001;
		int acceptedWeak = 0;
		int scale = ((max_points * max_points) / 8) * 2 + 1;
		for (int i = 0; i < maxIteration; i++) {
			// pick a random point in testExample
			int point = random.nextInt(max_points);
			PenSample sample = testExample.controlPoints.get(point);
			// scale is max_points
			int dim = random.nextInt(2);
			int mod = random.nextInt(scale) - (scale / 2);
			if (dim == 0) {
				mod = Math.max((int) -sample.x, mod);
				mod = Math.min((int) (max_points * max_points - sample.x), mod);
				sample.x += mod;
			} else {
				mod = Math.max((int) -sample.y, mod);
				mod = Math.min((int) (max_points * max_points - sample.y), mod);
				sample.y += mod;
			}
			double[] distances = new double[bestExamples.size()];
			double average = 0;
			for (int c = 0; c < bestExamples.size(); c++) {
				ShapeContext context = bestExamples.get(c);
				distances[c] = ShapeHistogram.shapeContextMetric(context, testExample, false, false, false);
				average += distances[c];
			}
			average /= bestExamples.size();
			// compute stddev distance
			double stddev = 0;
			for (int c = 0; c < bestExamples.size(); c++)
				stddev += Math.pow(distances[c] - average, 2);
			stddev /= bestExamples.size() - 1;
			stddev = Math.sqrt(stddev);
			double angleCost = 0;
			for (int c = 1; c < max_points - 1; c++) {
				// compute angles
				PenSample first = testExample.controlPoints.get(c - 1);
				PenSample second = testExample.controlPoints.get(c);
				PenSample third = testExample.controlPoints.get(c + 1);
				double x1 = second.x - first.x;
				double y1 = second.y - first.y;
				double x2 = third.x - second.x;
				double y2 = third.y - second.y;
				double dot = (x1 * x2 + y1 * y2) / Math.sqrt(x1 * x1 + y1 * y1)
						/ Math.sqrt(x2 * x2 + y2 * y2);
				// from -1 to 1
				angleCost += dot;
			}
			angleCost /= (max_points - 2);
			double springCost = 0;
			double averageLength = 0;
			double[] length = new double[max_points - 1];
			for (int c = 1; c < max_points; c++) {
				PenSample first = testExample.controlPoints.get(c - 1);
				PenSample second = testExample.controlPoints.get(c);
				length[c - 1] = Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
				averageLength += length[c - 1];
			}
			averageLength /= max_points - 1;
			for (int c = 0; c < max_points - 1; c++) {
				springCost += Math.pow(length[c] - averageLength, 2);
			}
			springCost /= max_points - 2;
			springCost = Math.sqrt(springCost);
			double noiseDistance = ShapeHistogram.shapeContextMetric(noiseExample, testExample, false, false,
					false);
			double metric = -10 * stddev + noiseDistance + average / 3 + angleCost * 10000 - springCost
					* 5000 + averageLength * 5000;
			System.out.println("iteration " + i + " distance " + metric + " stddev " + stddev + " noise "
					+ noiseDistance + " average " + average + " angle " + angleCost + " sc " + springCost
					+ " al " + averageLength);
			if (!Double.isNaN(metric) && (metric > distanceMetric || random.nextDouble() < /*
																							 * Math.exp((-Math.abs(mod) -
																							 * scale)*(2*i/(double)maxIteration)/5.)))
																							 */Math.exp((metric - distanceMetric) / (3000 - i)))) {
				if (metric < distanceMetric)
					acceptedWeak++;
				distanceMetric = metric;
			} else {
				if (dim == 0)
					sample.x -= mod;
				else
					sample.y -= mod;
			}
			if (i % 100 == 0) {
				System.out.println("Accepted " + acceptedWeak + " weak samples.");
				display(testExample, max_points * max_points, max_points * max_points);
				Ink ink = new Ink();
				ink.addStroke(new InkStroke(testExample.controlPoints, new Points()));
				InkRenderer renderer = new InkRenderer(ink);
				renderer.renderToJPEG(new File(databaseName + "_generated_" + (i / 100) + ".jpg"),
						new Pixels(300), new Points(max_points * max_points * 3), new Points(max_points
								* max_points * 3));
			}
		}
	}

	public PenGestureListener getListener() {
		return listener;
	}

	private String getString() {
		while (!inputAvailable)
			Thread.yield();
		inputAvailable = false;
		return inputString;
	}

	public void labelGestures() {
		labelField.setText("Class label:");
		while (unlabeledContexts.size() > 0) {
			ShapeContext context = unlabeledContexts.get(unlabeledContexts.size() - 1);
			if (context.authorName.contains("jerry"))
				for (PenSample sample : context.controlPoints) { // that clown
					sample.x *= -1;
					sample.y *= -1;
				}
			display(context, mainPanel.getWidth() / 2, mainPanel.getHeight() / 2);
			String label = getString();
			if (label.compareTo("-1") == 0) { // discard this gesture and continue
				unlabeledContexts.remove(context);
				continue;
			} else if (label.compareTo("") == 0) {
				// stop for the moment
				break;
			}
			for (Gesture gesture : gestures) {
				if (gesture.name.compareTo(label) == 0) {
					gesture.addGesture(context);
					unlabeledContexts.remove(context);
					break;
				}
			}
		}
		labelField.setText("Class label (test):");
		while (unlabeledTestContexts.size() > 0) {
			ShapeContext context = unlabeledTestContexts.get(unlabeledTestContexts.size() - 1);
			display(context, mainPanel.getWidth() / 2, mainPanel.getHeight() / 2);
			String label = getString();
			if (label.compareTo("-1") == 0) { // discard this gesture and continue
				unlabeledTestContexts.remove(context);
				continue;
			} else if (label.compareTo("") == 0) {
				// stop for the moment
				break;
			}
			for (Gesture gesture : testGestures) {
				if (gesture.name.compareTo(label + "TEST") == 0) {
					gesture.addGesture(context);
					unlabeledTestContexts.remove(context);
					break;
				}
			}
		}
	}

	private void leaveChunkOut(int trials) throws IOException {
		for (int trial = 0; trial < trials; trial++) {
			chunk(.1);
			Writer writer = new FileWriter(new File("C:\\dev\\quill\\data\\" + databaseName + "_" + trial
					+ ".gp"));
			quillWrite(writer);
			Save(new FileWriter(new File(databaseName + "_" + trial + ".xml")));
			Date before = new Date();
			System.out.println("Beginning no-time run leaving out " + trial);
			ShapeContext.bands = 2;
			autotest();
			Date after = new Date();
			double secondsElapsed = (after.getTime() - before.getTime()) / 1000.;
			System.out.println("Elapsed time was: " + secondsElapsed);

			before = new Date();
			System.out.println("Beginning time run leaving out " + trial);
			ShapeContext.bands = 3;
			autotest();
			after = new Date();
			secondsElapsed = (after.getTime() - before.getTime()) / 1000.;
			System.out.println("Elapsed time was: " + secondsElapsed);

		}
	}

	public void leaveOneOutOptimizeCostWeighting() {
		// try a range from .1 to 2 for kicks
		int[] errors = new int[11];
		for (int i = 1; i <= 10; i++) {
			ShapeHistogram.costWeighting = i * .1;
			for (Gesture gesture : gestures) {
				for (int j = gesture.contexts.size() - 1; j >= 0; j--) {
					ShapeContext context = gesture.contexts.get(j);
					gesture.contexts.remove(context);
					String assignment = test(context, false);
					if (assignment.compareTo(gesture.name) != 0) {
						errors[i]++;
						test(context, true);
						System.out.println(gesture.name + " misclassified as " + assignment);
					}
					gesture.addGesture(context);
				}
			}
			System.out
					.println("Error with cost weighting " + ShapeHistogram.costWeighting + ": " + errors[i]);
		}
		ShapeHistogram.costWeighting = .3;
	}

	private void leaveOneUserOut() throws IOException {
		// go through the users, pull one out into the test set at a time
		moveAllToTrain();
		Gesture gesture = gestures.get(0);
		HashMap<String, Boolean> names = new HashMap<String, Boolean>();
		for (int i = 0; i < gesture.contexts.size(); i++) {
			names.put(gesture.contexts.get(i).authorName, true);
		}
		for (String name : names.keySet()) {
			for (int i = 0; i < gestures.size(); i++) {
				ArrayList<ShapeContext> contexts = gestures.get(i).contexts;
				ArrayList<ShapeContext> testContexts = testGestures.get(i).contexts;
				for (int j = contexts.size() - 1; j >= 0; j--) {
					ShapeContext context = contexts.get(j);
					if (context.authorName.compareTo(name) == 0) {
						contexts.remove(context);
						testContexts.add(context);
					}
				}
			}
			Writer writer = new FileWriter(new File("C:\\dev\\quill\\data\\" + databaseName + "_" + name
					+ ".gp"));
			quillWrite(writer);
			Save(new FileWriter(new File(databaseName + "_" + name + ".xml")));
			Date before = new Date();
			System.out.println("Beginning no-time run leaving out " + name);
			ShapeContext.bands = 2;
			autotest();
			Date after = new Date();
			double secondsElapsed = (after.getTime() - before.getTime()) / 1000.;
			System.out.println("Elapsed time was: " + secondsElapsed);

			before = new Date();
			System.out.println("Beginning time run leaving out " + name);
			ShapeContext.bands = 3;
			autotest();
			after = new Date();
			secondsElapsed = (after.getTime() - before.getTime()) / 1000.;
			System.out.println("Elapsed time was: " + secondsElapsed);
		}
	}

	private void moveAllToTrain() {
		for (int i = 0; i < gestures.size(); i++) {
			ArrayList<ShapeContext> contexts = gestures.get(i).contexts;
			ArrayList<ShapeContext> testContexts = testGestures.get(i).contexts;
			for (int j = testContexts.size() - 1; j >= 0; j--) {
				ShapeContext context = testContexts.get(j);
				testContexts.remove(context);
				contexts.add(context);
			}
		}
	}

	private void print(String string) {
		labelField.setText(string);
	}

	public void quillWrite(Writer writer) throws IOException {
		final String VERSION = "gdt 2.0";

		writer.write(VERSION + "\n");
		// gesture package
		String name = "bob";
		writer.write("name\t" + name + "\n");
		writer.write("training\n");
		// gesture set
		writer.write("name\t" + name + "\n");
		for (Gesture gesture : (bestGestures == null ? gestures : bestGestures)) {
			writer.write("category\n");
			gesture.quillWrite(writer);
		}
		writer.write("endset\n");
		// end set
		writer.write("test\n");
		// gesture set
		writer.write("name\t" + name + "\n");
		writer.write("set\n");
		writer.write("name\t testset1\n");
		for (Gesture gesture : testGestures) {
			writer.write("category\n");
			gesture.quillWrite(writer);
		}
		writer.write("endset\n");
		writer.write("endmetaset\n");
		writer.write("endpackage\n");
		writer.close();
	}

	public void Save(Writer writer) throws IOException {
		XStream xstream = new XStream();
		xstream.toXML(this, writer);
	}

	public String test(ShapeContext context, boolean verbose) {
		return test(context, verbose, gestures);
	}

	public String test(ShapeContext context, boolean verbose, ArrayList<Gesture> gestures) {
		// do KNN
		int k = 3;
		double[] distance = new double[k];
		int[] index = new int[k];
		String[] clazz = new String[k];
		for (int i = 0; i < k; i++) {
			distance[i] = Double.MAX_VALUE;
			index[i] = -1;
		}
		for (int i = 0; i < gestures.size(); i++) {
			gestures.get(i).knnMatch(context, k, distance, clazz, verbose);
			// double d = gestures.get(i).bestMatch(context);
			// System.out.println("Category " + i + "(" + gestures.get(i).size() + " items): " + d);
		}
		if (verbose) {
			System.out.println("Best matches are:");
			for (int i = 0; i < k; i++)
				System.out.println(i + ": category " + clazz[i] + " with distance " + distance[i] + " using "
						+ context.size() + " points.");
		}
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		HashMap<String, Double> costs = new HashMap<String, Double>();
		int max_count = 1;
		boolean unique = true;
		for (int i = 0; i < k; i++) {
			Integer total = counts.get(clazz[i]);
			int count;
			double d;
			if (total == null) {
				count = 1;
				d = distance[i];
			} else {
				count = total + 1;
				d = costs.get(clazz[i]) + distance[i];
			}
			if (count == max_count)
				unique = false;
			else if (count > max_count) {
				unique = true;
				max_count = count;
			}
			counts.put(clazz[i], count);
			costs.put(clazz[i], d);
		}
		if (unique)
			for (String c : counts.keySet()) {
				if (counts.get(c) == max_count) {
					return c;
				}
			}
		double min_cost = Double.MAX_VALUE;
		String min_clazz = null;
		for (String c : counts.keySet()) {
			double cost = costs.get(c) / counts.get(c);
			if (cost < min_cost) {
				min_cost = cost;
				min_clazz = c;
			}
		}
		return min_clazz;
	}

	private void testChunkOutOnBest(int trials) throws IOException {
		for (int trial = 0; trial < trials; trial++) {
			chunk(.1);
			bestExamples();
			Writer writer = new FileWriter(new File("C:\\dev\\quill\\data\\" + databaseName + "_best_"
					+ trial + ".gp"));
			quillWrite(writer);
			Save(new FileWriter(new File(databaseName + "_best_" + trial + ".xml")));
			Date before = new Date();
			System.out.println("Beginning no-time run on best leaving out " + trial);
			ShapeContext.bands = 2;
			autotest(bestGestures);
			Date after = new Date();
			double secondsElapsed = (after.getTime() - before.getTime()) / 1000.;
			System.out.println("Elapsed time was: " + secondsElapsed);

			before = new Date();
			System.out.println("Beginning time run on best leaving out " + trial);
			ShapeContext.bands = 3;
			autotest(bestGestures);
			after = new Date();
			secondsElapsed = (after.getTime() - before.getTime()) / 1000.;
			System.out.println("Elapsed time was: " + secondsElapsed);
		}
	}
}