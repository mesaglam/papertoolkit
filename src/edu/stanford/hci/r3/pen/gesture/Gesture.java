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
	
	public void knnMatch(ShapeContext context, int k, double[] distance, String[] clazz)
	{
		for(ShapeContext gesture : contexts) {
			double d = ShapeHistogram.shapeContextMetric(context, gesture);
			for(int i=0;i<k;i++) {
				if(d < distance[i]) {
					for(int j=k-1;j>i;j--) {
						distance[j]=distance[j-1];
						clazz[j]=clazz[j-1];
					}
					distance[i] = d;
					clazz[i] = name;
					break;
				}
			}
		}
	}
	
	public int size()
	{
		return contexts.size();
	}
	
	public void quillWrite(Writer writer) throws IOException
	{
		// gesture category
		String stripTestName = name.replace("TEST", "");
	    writer.write("name\t" + stripTestName + "\n");
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
