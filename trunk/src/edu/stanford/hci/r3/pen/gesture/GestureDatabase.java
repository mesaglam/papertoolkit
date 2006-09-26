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
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkSample;
import edu.stanford.hci.r3.pen.ink.InkPanel;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.streaming.PenGestureListener;
import edu.stanford.hci.r3.units.Points;
import edu.stanford.hci.r3.util.WindowUtils;

public class GestureDatabase implements ActionListener, FocusListener{
	ArrayList<Gesture> gestures = new ArrayList<Gesture>();
	ArrayList<ShapeContext> unlabeledContexts = new ArrayList<ShapeContext>();
	ArrayList<Gesture> testGestures = new ArrayList<Gesture>();
	private transient static BufferedReader stdin = new BufferedReader(new InputStreamReader( System.in ) );
	PenGestureListener listener;
	String databaseName;
	private transient boolean commandMode;
	private transient String inputString;
	private transient static JFrame inkDisplay;
	private transient static InkPanel inkPanel;
	private transient static JPanel mainPanel;
	private transient static JPanel statusPanel;
	private transient static JTextField entryField;
	transient static GestureDatabase instance;
	private transient static JLabel labelField;
	private transient Thread commandThread;
	private transient boolean inputAvailable;
	private ArrayList<ShapeContext> unlabeledTestContexts = new ArrayList<ShapeContext>();
	
	public GestureDatabase(String databaseName)
	{
		this.databaseName = databaseName;
		listener = new PenGestureListener();
	}
	
	public PenGestureListener getListener() {
		return listener;
	}

	public void command(int option) throws IOException
	{
		// build a set of gestures
		// options: add a gesture (N times)
		//          add more of a gesture (N)
		//          remove some of a gesture (? with images?)
		//int option;
		//do{
			/*System.out.println("Options:\n" +
					"0: Add a gesture\n" +
					"1: Add more of an existing gesture\n" +
					"2: Save\n" +
					"3: List\n" +
					"4: Test (interactive)\n" +
					"5: Add a test gesture (for existing)\n" +
					"6: Run autotest\n" +
					"7: Determine class parameters\n" +
					"8: Assign class labels to unlabeled gestures\n" +
					"9: Add unlabeled gestures\n" +
					"-1: Exit");*/
			//option = Integer.parseInt(stdin.readLine());
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
				for(int i = 0; i < gestures.size(); i++)
					System.out.println("Gesture " + gestures.get(i).name + ": " + gestures.get(i).size());
				System.out.println("Tests:");
				for(int i = 0; i < testGestures.size(); i++)
					System.out.println("Gesture " + testGestures.get(i).name + ": " + testGestures.get(i).size());
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
					if (g.name.compareTo(name + "TEST")==0) {
						gesture = g;
						break;
					}
				print("Examples: ");
				count = Integer.parseInt(getString());
				listener.setContexts(gesture.contexts, count);
				break;
			case 6:
				System.out.println("Test without time: ");
				ShapeContext.bands = 2;
				autotest();
				//System.out.println("Test using time: ");
				//ShapeContext.bands = 3;
				//autotest();
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
			case -1:
				System.out.println("Exiting.");
				System.exit(0); // no automatic save on exit
			case 2:
				Writer writer = new FileWriter(new File("C:\\dev\\quill\\data\\"+databaseName+".gp"));
				quillWrite(writer);
				Save(new FileWriter(new File(databaseName + ".xml")));
				break;
			}
		//	Thread.yield();
		//} while (option > -1);
			commandMode = false;
			labelField.setText("Option:");
	}
	
	private void print(String string) {
		labelField.setText(string);
	}

	private String getString() {
		while (!inputAvailable)
			Thread.yield();
		inputAvailable = false;
		return inputString;
	}

	public void Save(Writer writer) throws IOException
	{
		XStream xstream = new XStream();
		xstream.toXML(this,writer);
	}

	public void quillWrite(Writer writer) throws IOException
	{
		final String VERSION = "gdt 2.0";

	    writer.write(VERSION + "\n");
	    // gesture package
		String name = "bob";
	    writer.write("name\t" + name + "\n");
	    writer.write("training\n");
	    // gesture set
	    writer.write("name\t" + name + "\n");
	    for(Gesture gesture : gestures) {
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
	    for(Gesture gesture : testGestures) {
	    	writer.write("category\n");
	    	gesture.quillWrite(writer);
	    }
	    writer.write("endset\n");
	    writer.write("endmetaset\n");
	    writer.write("endpackage\n");
	    writer.close();
	}

	public String test(ShapeContext context, boolean verbose) {
		// do KNN
		int k = 3;
		double[] distance = new double[k];
		int[] index = new int[k];
		String[] clazz = new String[k];
		for(int i=0;i<k;i++) {
			distance[i] = Double.MAX_VALUE;
			index[i] = -1;
		}
		for(int i=0;i<gestures.size();i++) {
			gestures.get(i).knnMatch(context, k, distance, clazz, verbose);
			//double d = gestures.get(i).bestMatch(context);
			//System.out.println("Category " + i + "(" + gestures.get(i).size() + " items): " + d);
		}
		if (verbose) {
			System.out.println("Best matches are:");
			for(int i=0;i<k;i++)
				System.out.println(i + ": category " + clazz[i] + " with distance " + distance[i] + " using " + context.size() + " points.");
		}
		HashMap<String,Integer> counts = new HashMap<String,Integer>();
		HashMap<String,Double> costs = new HashMap<String,Double>();
		int max_count = 1;
		boolean unique = true;
		for(int i=0;i<k;i++) {
			Integer total = counts.get(clazz[i]);
			int count;
			double d;
			if (total == null) {
				count = 1;
				d = distance[i];
			}
			else {
				count = total + 1;
				d = costs.get(clazz[i]) + distance[i];
			}
			if (count == max_count) unique = false;
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
			if(cost < min_cost) {
				min_cost = cost;
				min_clazz = c;
			}
		}
		return min_clazz;
	}
	
	public void autotest() {
		// use knn, most occurences or best average weight
		int tested = 0;
		int correct = 0;
		for (int i=0;i<gestures.size();i++) {
			Gesture gesture = gestures.get(i);
			Gesture testGesture = testGestures.get(i);
			for (ShapeContext context : testGesture.contexts) {
				tested++;
				String assignment = test(context, false);
				if (assignment.compareTo(gesture.name) == 0)
					correct++;
				else {
					test(context, true);
					System.out.println(gesture.name + " misclassified as " + assignment);
				}
			}
		}
		System.out.println("Tested " + tested + " contexts, " + correct + " correct.");
	}
	
	public void determineClassParameters()
	{
		for (Gesture gesture : gestures)
			gesture.determineClassParameters();
	}
	
	static JFrame getInkDisplay() {
		if (inkDisplay == null) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			inkDisplay = new JFrame("Sketch! Display");
			inkDisplay.setContentPane(getMainPanel());
			inkDisplay.setSize(690, 740);
			inkDisplay.setLocation(WindowUtils.getWindowOrigin(inkDisplay,
					WindowUtils.DESKTOP_CENTER));
			inkDisplay.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			//inkDisplay.pack();
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
			String[] labelStrings = {
					"0: Add a gesture",
					"1: Add more of an existing gesture",
					"2: Save",
					"3: List",
					"4: Test (interactive)",
					"5: Add a test gesture (for existing)",
					"6: Run autotest",
					"7: Determine class parameters",
					"8: Assign class labels to unlabeled gestures",
					"9: Add unlabeled gestures",
					"10: Add unlabeled test gestures",
					"11: Optimize cost weighting",
					"-1: Exit",
					"Option: "
			};
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
					tf = (JTextField)fields[i];
					tf.addActionListener(instance);
					tf.addFocusListener(instance);
				}
			}
			int GAP = 10;
			makeCompactGrid(statusPanel,
					labelStrings.length, 2,
					GAP , GAP,
					GAP, GAP/2);
		}
		return statusPanel;
	}
	
	public static void makeCompactGrid(Container parent,
			int rows, int cols,
			int initialX, int initialY,
			int xPad, int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout)parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

//		Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
						getConstraintsForCell(r, c, parent, cols).
						getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
					getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

//		Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
						getConstraintsForCell(r, c, parent, cols).
						getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =
					getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

//		Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}

	   /* Used by makeCompactGrid. */
    private static SpringLayout.Constraints getConstraintsForCell(
                                                int row, int col,
                                                Container parent,
                                                int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }
    
	public void labelGestures()
	{
		labelField.setText("Class label:");
		while (unlabeledContexts.size() > 0) {
			ShapeContext context = unlabeledContexts.get(unlabeledContexts.size()-1);
			for(InkSample sample : context.controlPoints) {
				sample.x += mainPanel.getWidth() / 2;
				sample.y += mainPanel.getHeight() / 2;
			}
			InkStroke stroke = new InkStroke(context.controlPoints, new Points());
			Ink ink = new Ink();
			ink.addStroke(stroke);
			inkPanel.clear();
			inkPanel.addInk(ink);
			String label = getString();
			if (label.compareTo("-1") == 0) { // discard this gesture and continue
				unlabeledContexts.remove(context);
				continue;
			}
			else if (label.compareTo("") == 0) {
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
			ShapeContext context = unlabeledTestContexts.get(unlabeledTestContexts.size()-1);
			for(InkSample sample : context.controlPoints) {
				sample.x += mainPanel.getWidth() / 2;
				sample.y += mainPanel.getHeight() / 2;
			}
			InkStroke stroke = new InkStroke(context.controlPoints, new Points());
			Ink ink = new Ink();
			ink.addStroke(stroke);
			inkPanel.clear();
			inkPanel.addInk(ink);
			String label = getString();
			if (label.compareTo("-1") == 0) { // discard this gesture and continue
				unlabeledTestContexts.remove(context);
				continue;
			}
			else if (label.compareTo("") == 0) {
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
	
	public void leaveOneOutOptimizeCostWeighting()
	{
		// try a range from .1 to 2 for kicks
		int[] errors = new int[11];
		for(int i=1;i<=10;i++) {
			ShapeHistogram.costWeighting = i * .1;
			for (Gesture gesture : gestures) {
				for (int j=gesture.contexts.size()-1; j >= 0; j--) {
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
			System.out.println("Error with cost weighting " + ShapeHistogram.costWeighting + ": " + errors[i]);
		}
		ShapeHistogram.costWeighting = .3;
	}

	public void actionPerformed(ActionEvent e) {
		if (commandMode) {
			inputString = e.getActionCommand();
			inputAvailable = true;
		}
		else {
			try {
				final int option = Integer.parseInt(e.getActionCommand());
				commandMode = true;
				commandThread = new Thread(new Runnable() {
					public void run()
					{
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
			} catch (NumberFormatException error) {}
		}
		entryField.setText("");
	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}	
}