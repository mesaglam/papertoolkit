package edu.stanford.hci.r3.pen.streaming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;

import edu.stanford.hci.r3.pen.gesture.Gesture;
import edu.stanford.hci.r3.pen.gesture.GestureDatabase;
import edu.stanford.hci.r3.pen.gesture.ShapeContext;

public class PenGestureListener implements PenListener {
	ArrayList<Gesture> gestures = new ArrayList<Gesture>();
	ArrayList<ShapeContext> contexts = new ArrayList<ShapeContext>();
	ArrayList<PenSample> samples = null;
	int gestureThreshold = 4;
	int remainingContexts = 0;
	private GestureDatabase database;
	
	private static BufferedReader stdin = new BufferedReader(new InputStreamReader( System.in ) );
	
	public void penDown(PenSample sample)
	{
		samples = new ArrayList<PenSample>();
	}
	
	public void setContexts(ArrayList<ShapeContext> contexts, int remainingContexts)
	{
		this.contexts = contexts;
		this.remainingContexts = remainingContexts;
	}
	
	public void penUp(PenSample sample)
	{
		final double categoryThreshold = 20;
		if (samples != null && samples.size() > gestureThreshold && remainingContexts > 0) {
			remainingContexts--;
			int index = -1;
			double distance = Double.MAX_VALUE;
			ShapeContext context = new ShapeContext(samples);
			contexts.add(context);
			if(remainingContexts == 0)
				System.out.println("Done reading gestures.");
		}
		else if (samples != null && samples.size() > gestureThreshold && database != null) {
			ShapeContext context = new ShapeContext(samples);
			database.test(context);
		}
		samples = null;
	}
	
	public void sample(PenSample sample)
	{
		samples.add(sample);
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
	    // end category
	    writer.write("endset\n");
	    // end set
	    writer.write("test\n");
	    writer.write("endpackage\n");
	    writer.close();
	}

	public void setDatabase(GestureDatabase database) {
		// TODO Auto-generated method stub
		this.database = database;
	}
}
