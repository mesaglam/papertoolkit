package edu.stanford.hci.r3.pen.gesture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

import edu.stanford.hci.r3.pen.streaming.PenGestureListener;

public class GestureDatabase {
	ArrayList<Gesture> gestures = new ArrayList<Gesture>();
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
				break;
			case 4:
				listener.setDatabase(this);
				System.out.println("Hit enter to end test.");
				stdin.readLine();
				listener.setDatabase(null);
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
	    writer.write("endpackage\n");
	    writer.close();
	}

	public void test(ShapeContext context) {
		double distance = Double.MAX_VALUE;
		int index= -1;
		for(int i=0;i<gestures.size();i++) {
			double d = gestures.get(i).bestMatch(context);
			System.out.println("Category " + i + "(" + gestures.get(i).size() + " items): " + d);
			if (d < distance) {
				distance = d;
				index = i;
			}
		}
		System.out.println("Best match is category " + index + " with distance " + distance + " using " + context.size() + " points.");	
	}
}
