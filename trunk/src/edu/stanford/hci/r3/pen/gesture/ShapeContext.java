package edu.stanford.hci.r3.pen.gesture;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import edu.stanford.hci.r3.pen.ink.InkSample;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author Avi Robinson-Mosher
 */
public class ShapeContext {
	
	ArrayList<InkSample> controlPoints = new ArrayList<InkSample>();
	public static int bands = 3;

	public ShapeContext(ArrayList<InkSample> controlPointsInput) 
	{
		// filter this for dupes
		for(int i=0;i<controlPointsInput.size();i++) {
			InkSample sample = controlPointsInput.get(i);
			boolean duplicate = false;
			for(InkSample old_sample : controlPoints)
				if(sample.x == old_sample.x && sample.y == old_sample.y) { // can't duplicate samples, unfortunately
					duplicate = true; break;
				}
			if (duplicate) continue;
			controlPoints.add(sample);
		}
		// there's me ANN done
		// ANN ann = new ANN();
	}
	
	public int size()
	{
		return 2*controlPoints.size();
	}
	
	public double[][] points()
	{
		int N = size();
		ArrayList<InkSample> controlPoints = resample(N);
		double[][] pts = new double[N][2];
		for(int i=0;i<N;i++) {
			pts[i][0] = controlPoints.get(i).x;
			pts[i][1] = controlPoints.get(i).y;
		}
		return pts;
	}
	
	public ArrayList<InkSample> resample(int samples)
	{
		// special case
		if (samples == controlPoints.size()) return (ArrayList<InkSample>)controlPoints.clone();
		// want to return something with time information
		ArrayList<InkSample> sampledPoints = new ArrayList<InkSample>();
		assert(controlPoints.size() > 1);
		// sampling in time is a little weird; I'll sample in "space"
		float fraction = (controlPoints.size()-1)/(float)(samples);
		for(int i=0; i < samples; i++) {
			float position = fraction * i;
			int truncated = (int)position;
			float remainder = position - truncated;
			sampledPoints.add(blendHelper(truncated, remainder));
		}
		return sampledPoints;
	}

	public ArrayList<InkSample> tangents(int samples)
	{
		// want to return something with time information
		ArrayList<InkSample> sampledPoints = new ArrayList<InkSample>();
		assert(controlPoints.size() > 1);
		// sampling in time is a little weird; I'll sample in "space"
		float fraction = (controlPoints.size()-1)/(float)(samples);
		for(int i=0; i < samples; i++) {
			float position = fraction * i;
			int truncated = (int)position;
			float remainder = position - truncated;
			sampledPoints.add(tangent(truncated, remainder));
		}
		return sampledPoints;
	}

	// Catmull-Rom FTW
	InkSample blendHelper(int i, float t)
	{
		InkSample[] samples = new InkSample[4];
		InkSample blendedSample = new InkSample(0, 0, 0, 0);
		if (i == 0) // double up the first
			samples[0] = controlPoints.get(0);
		else samples[0] = controlPoints.get(i-1);
		samples[1] = controlPoints.get(i);
    		samples[2] = controlPoints.get(i+1);
		if (i + 2 >= controlPoints.size())
			samples[3] = controlPoints.get(controlPoints.size() - 1);
		else
			samples[3] = controlPoints.get(i+2);
		for(int ii=0; ii<4; ii++) {
			float b = blend(ii, t);
			blendedSample.x += samples[ii].x * b;
			blendedSample.y += samples[ii].y * b;
			blendedSample.timestamp += samples[ii].timestamp * b;
		}
		return blendedSample;
	}
	
	  float blend(int i, float t) {
		  switch (i) {
		  case 0:
			  return ((-t+2)*t-1)*t/2;
		  case 1:
			  return (((3*t-5)*t)*t+2)/2;
		  case 2:
			  return ((-3*t+4)*t+1)*t/2;
		  case 3:
		      return ((t-1)*t*t)/2;
		  }
		  return 0; // we only get here if an invalid i is specified
	  }

	  float dblend_du(int i, float t) {
		  switch (i) {
		  case 0:
			  return (4*t-1-3*t*t)/2;
		  case 1:
			  return (9*t*t-10*t)/2;
		  case 2:
			  return (8*t-9*t*t+1)/2;
		  case 3:
			  return (3*t*t-2*t)/2;
		  }
		  return 0; // we only get here if an invalid i is specified
	  }

	  
	  // all I actually need is x and y, or even angle. but this will do
	  InkSample tangent(int i, float t)
	  {
			InkSample[] samples = new InkSample[4];
			InkSample blendedSample = new InkSample(0, 0, 0, 0);
			if (i == 0) // double up the first
				samples[0] = controlPoints.get(0);
			else samples[0] = controlPoints.get(i-1);
			samples[1] = controlPoints.get(i);
			samples[2] = controlPoints.get(i+1);
			if (i + 2 >= controlPoints.size())
				samples[3] = controlPoints.get(controlPoints.size() - 1);
			else
				samples[3] = controlPoints.get(i+2);
			for(int ii=0; ii<4; ii++) {
				float b = dblend_du(ii, t);
				blendedSample.x += samples[ii].x * b;
				blendedSample.y += samples[ii].y * b;
				blendedSample.timestamp += samples[ii].timestamp * b;
			}
			return blendedSample;
	  }
	  
	  public ArrayList<ShapeHistogram> generateShapeHistogram(int points)
	  {
		  boolean rotation_invariant = false;
		  // histogram for each point
		  int dummy_points = size();
		  ArrayList<ShapeHistogram> histograms = new ArrayList<ShapeHistogram>();
		  ArrayList<InkSample> samples = resample(dummy_points);
		  ArrayList<InkSample> tangents = new ArrayList<InkSample>();// tangents(points);
		  for (int i=0; i<samples.size();i++) {
			  InkSample last,next;
			  if(i==0) last = samples.get(0);
			  else last = samples.get(i-1);
			  if(i==dummy_points-1) next = samples.get(dummy_points-1);
			  else next = samples.get(i+1);
			  tangents.add(new InkSample(next.x-last.x,next.y-last.y,0,0)); // crude
		  }
		  double[][] bins = new double[3][];
		  int[] bin_counts = new int[3];
		  boolean[] explicit_binning = new boolean[3];
		  explicit_binning[0] = true; // log scaled distances
		  bin_counts[0] = 5; // log r
		  bin_counts[1] = 12; // theta
		  bin_counts[2] = 2; // t (for the moment, only before/after)
		  // want a bin for "component" - how to discretize? connected ought to be fine. this may
			// be redundant with time...somewhat
		  double[] mins = new double[3];
		  mins[0] = Double.MAX_VALUE; // not; calc this based on actual min
		  mins[1] = -Math.PI; // yup
		  mins[2] = Double.MAX_VALUE; // not; calc on actual mins
		  double[] maxes = new double[3];
		  maxes[0] = -Double.MAX_VALUE;
		  maxes[1] = Math.PI;
		  maxes[2] = -Double.MAX_VALUE;
		  double distance_min=Double.MAX_VALUE;
		  double distance_max=-Double.MAX_VALUE;
		  double timestamp_min=Double.MAX_VALUE;
		  double timestamp_max=-Double.MAX_VALUE;
		  double mean_distance = 0;
		  for (InkSample sample : samples) {
			  	// wrong, should be considering deltas.
				// Alt, normalize all the times ahead.
			  if (sample.timestamp < timestamp_min) timestamp_min = sample.timestamp; 
			  if (sample.timestamp > timestamp_max) timestamp_max = sample.timestamp;
			  for (InkSample secondSample : samples) {
				  if (sample.equals(secondSample)) continue;
				  double distance = Math.sqrt(Math.pow(sample.x - secondSample.x, 2) + Math.pow(sample.y - secondSample.y, 2));
				  mean_distance += distance;
				  if (distance > 0) {
					  distance_min = Math.min(distance_min, distance);
					  distance_max = Math.max(distance_max, distance);}
			  }
		  }
		  mean_distance /= (dummy_points * (dummy_points - 1)) / 2;
		  double inner_threshold = Math.log10(mean_distance / 8);
		  double outer_threshold = Math.log10(2 * mean_distance);
		  double logscale = (outer_threshold - inner_threshold) / (bin_counts[0] - 1);
		  // log scale it
		  bins[0] = new double[bin_counts[0]];
		  for (int i = 0; i < bin_counts[0]; i++) {
			  bins[0][i] = Math.pow(10, inner_threshold + i * logscale);
		  }
		  mins[0] = Math.log(distance_min)-.01;
		  maxes[0] = Math.log(distance_max)+.01;
		  mins[2] = timestamp_min - timestamp_max;
		  maxes[2] = -mins[2];
		  // mins[0] = Math.log(distance_min);
		  // maxes[0] = Math.log(distance_max);
		  for (InkSample sample : samples) {
			  ShapeHistogram histogram = new ShapeHistogram(bin_counts, mins, maxes, explicit_binning, bins, bands);
			  InkSample tangent = tangents.get(samples.indexOf(sample)); // sue me, I'm lax
			  double theta = rotation_invariant?Math.atan2(tangent.y, tangent.x):0;
			  for (InkSample secondSample : samples) {
				  if (sample.equals(secondSample)) continue;
				  // for rotation invariance, adjust angles by setting normal to curve to some
					// axis. slightly tricky, but not too bad.
				  // I guess I'll do this the easy way (via spline lookups and calculation). Is
					// there an analytic way? Probably. But I'm lazy.
				  histogram.addPoint(logPolarAndTime(sample, secondSample, mins[0]/* sum * sum */, theta));
			  }
			  histograms.add(histogram);
		  }
		  for (int i = 0; i < points - samples.size(); i++) {
			  histograms.add(new ShapeHistogram(bin_counts, mins, maxes, explicit_binning, bins, bands)); // blank histograms
		  }
		  return histograms;
	  }
	  
	  static double[] logPolarAndTime(InkSample first, InkSample second, double distanceScaling, double baseRotation)
	  {
		  // normalize the times. actually, probably ought to normalize all of them -
			// scale-invariance? not theta, though.
		  double dx = second.x - first.x;
		  double dy = second.y - first.y;
		  double[] results = new double[3];
		  if (dx == 0 && dy == 0) {
			  results[0] = distanceScaling; // hack
			  results[1] = 0;
		  }
		  else {
			  results[0] = Math.sqrt(dx*dx + dy*dy) / distanceScaling;
			  //results[0] = .5 * Math.log((dx*dx + dy*dy)/* / distanceScaling */);
			  results[1] = renormalize(Math.atan2(dy, dx) - baseRotation);
		  }
		  results[2] = second.timestamp - first.timestamp;
		  return results;
	  }
	  
	  static double renormalize(double theta)
	   { // cast into -PI to PI
		  if(theta < -Math.PI) return theta+2*Math.PI;
		  if(theta > Math.PI) return theta-2*Math.PI;
		  return theta;
		  
	  }
	 
	  
	  public void quillWrite(Writer writer) throws IOException
	  {
		  final DecimalFormat df = new DecimalFormat("#");
		  boolean normalized = false;
	      writer.write("normalized\t" + normalized + "\n");
	      writer.write("points\t" + controlPoints.size() + "\n");
	      int x = Integer.MAX_VALUE;
	      int y = Integer.MAX_VALUE;
	      for(InkSample sample : controlPoints) {
	    	  x = (int)Math.min(x, sample.x);
	    	  y = (int)Math.min(y, sample.y);
	      }
	      for (int i = 0; i < controlPoints.size(); i++) {
	    	  InkSample sample = controlPoints.get(i);
	    	  writer.write("\t" + df.format(sample.x-x) + "\t" + df.format(sample.y-y) +
	  		   "\t" + df.format(sample.timestamp) + "\n");
	      }
	      writer.write("endgesture\n");
	  }
	  // pseudocode
		/*
		 * 
		 * constructor(points[]) {create shapehistogram for each point}
		 * 
		 * double[] logpolar(point1,point2) given input points, gets logpolar offset
		 * 
		 * distance(ShapeContext) need to be able to sample a pointset to get the right number of
		 * points to match. do this via spline interpolation
		 * 
		 * 
		 * bipartite matching
		 */

}
