package edu.stanford.hci.r3.pen.gesture;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Gesture {
	ArrayList<ShapeContext> contexts = new ArrayList<ShapeContext>();
	String name;
	
	public Gesture(String name)
	{
		this.name = name;
	}
	
	public void addGesture(ShapeContext context)
	{
		contexts.add(context);
	}
	
	public double bestMatch(ShapeContext context)
	{
		double distance = Double.MAX_VALUE;
		for(ShapeContext gesture : contexts) {
			distance = Math.min(distance, ShapeHistogram.shapeContextMetric(context, gesture));
		}
		return distance;
	}
	
	public int size()
	{
		return contexts.size();
	}
	
	public void quillWrite(Writer writer) throws IOException
	{
		// gesture category
	    writer.write("name\t" + name + "\n");
	    boolean directionInvariant = false;
	    boolean orientationInvariant = false;
	    boolean sizeInvariant = false;
	    writer.write("directionInvariant\t" + directionInvariant + "\n");
	    writer.write("orientationInvariant\t" + orientationInvariant + "\n");
	    writer.write("sizeInvariant\t" + sizeInvariant + "\n");
	    writer.write("gestures\t" + contexts.size() + "\n");
	    for (int i = 0; i < contexts.size(); i++) {
	    	contexts.get(i).quillWrite(writer);
	    }
	    writer.write("endcategory\n");

	}
}
