package edu.stanford.hci.r3.pen.gesture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.pen.streaming.PenGestureListener;

public class GestureDatabase {
	ArrayList<Gesture> gestures = new ArrayList<Gesture>();
	ArrayList<Gesture> testGestures = new ArrayList<Gesture>();
	private static BufferedReader stdin = new BufferedReader(new InputStreamReader( System.in ) );
	PenGestureListener listener;
	String databaseName;
	
	public GestureDatabase(String databaseName)
	{
		this.databaseName = databaseName;
		listener = new PenGestureListener();
	}
	
	public PenGestureListener getListener() {
		return listener;
	}

	public void buildDatabase() throws NumberFormatException, IOException
	{
		// build a set of gestures
		// options: add a gesture (N times)
		//          add more of a gesture (N)
		//          remove some of a gesture (? with images?)
		int option;
		do{
			System.out.println("Options:\n" +
					"0: Add a gesture\n" +
					"1: Add more of an existing gesture\n" +
					"2: Save\n" +
					"3: List\n" +
					"4: Test (interactive)\n" +
					"5: Add a test gesture (for existing)\n" +
					"6: Run autotest\n" +
					"-1: Exit");
			option = Integer.parseInt(stdin.readLine());
			int count, index;
			Gesture gesture;
			switch (option) {
			case 0: // add a gesture
				System.out.print("Name: ");
				String name = stdin.readLine();
				System.out.print("Examples: ");
				count = Integer.parseInt(stdin.readLine());
				gesture = new Gesture(name);
				gestures.add(gesture);
				testGestures.add(new Gesture(name + "TEST"));
				listener.setContexts(gesture.contexts, count);
				break;
			case 1: // add examples to a gesture
				System.out.print("Index: ");
				index = Integer.parseInt(stdin.readLine());
				gesture = gestures.get(index);
				System.out.println("Name: " + gesture.name);
				System.out.print("Examples: ");
				count = Integer.parseInt(stdin.readLine());
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
				listener.setDatabase(this);
				System.out.println("Hit enter to end test.");
				stdin.readLine();
				listener.setDatabase(null);
			case 5: // add test examples to a gesture
				System.out.print("Name: ");
				name = stdin.readLine();
				gesture = null;
				for (Gesture g : testGestures)
					if (g.name.compareTo(name + "TEST")==0) {
						gesture = g;
						break;
					}
				System.out.print("Examples: ");
				count = Integer.parseInt(stdin.readLine());
				listener.setContexts(gesture.contexts, count);
				break;
			case 6:
				System.out.println("Test without time: ");
				ShapeContext.bands = 2;
				autotest();
				System.out.println("Test using time: ");
				ShapeContext.bands = 3;
				autotest();
				break;
			case -1:
				System.out.println("Exiting.");
			case 2:
				Writer writer = new FileWriter(new File("C:\\dev\\quill\\data\\"+databaseName+".gp"));
				quillWrite(writer);
				Save(new FileWriter(new File(databaseName + ".xml")));
				break;
			}
			
		} while (option > -1);
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

	public String test(ShapeContext context) {
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
			gestures.get(i).knnMatch(context, k, distance, clazz);
			//double d = gestures.get(i).bestMatch(context);
			//System.out.println("Category " + i + "(" + gestures.get(i).size() + " items): " + d);
		}
		/*System.out.println("Best matches are:");
		for(int i=0;i<k;i++)
			System.out.println(i + ": category " + clazz[i] + " with distance " + distance[i] + " using " + context.size() + " points.");
			*/
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
				String assignment = test(context);
				if (assignment.compareTo(gesture.name) == 0)
					correct++;
				else
					System.out.println(gesture.name + " misclassified as " + assignment);
			}
		}
		System.out.println("Tested " + tested + " contexts, " + correct + " correct.");
	}
}
